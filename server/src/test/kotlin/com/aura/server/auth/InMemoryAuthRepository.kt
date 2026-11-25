package com.aura.server.auth

import java.security.SecureRandom
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class InMemoryAuthRepository : AuthRepository {
    private data class StoreRecord(
        val idToko: String,
        var namaToko: String,
        var jenisUsaha: String?,
        var alamat: String?,
        var logoFileId: UUID?,
        var saldoAwal: Long,
        var saldoKumulatif: Long,
        var hapusPada: Long?,
    )

    private data class SessionRecord(
        val jti: UUID,
        val akunId: UUID,
        val berakhirPada: Long,
        var dicabutPada: Long? = null,
    )

    private data class AttemptRecord(
        var jumlahGagal: Int,
        var terkunciSampai: Long?,
    )

    data class AuditRecord(
        val idToko: String,
        val akunId: UUID?,
        val kategori: String,
        val aksi: String,
    )

    private val stores = ConcurrentHashMap<String, StoreRecord>()
    private val accounts = ConcurrentHashMap<UUID, Akun>()
    private val sessions = ConcurrentHashMap<UUID, SessionRecord>()
    private val attempts = ConcurrentHashMap<String, AttemptRecord>()
    val auditLogs = mutableListOf<AuditRecord>()
    private val random = SecureRandom()

    override fun createOwner(
        email: String,
        password: PasswordDigest,
        storeName: String,
        businessType: String?,
        openingBalance: Long,
        profilePhotoId: UUID?,
    ): Pair<Akun, TokoRingkas> {
        val normalizedEmail = email.lowercase()
        if (accounts.values.any { it.email?.lowercase() == normalizedEmail }) {
            throw SQLException("duplicate key value violates unique constraint \"akun_email_normalisasi_unik\"", "23505")
        }
        val storeId = generateStoreId()
        val ownerId = UUID.randomUUID()
        val store = StoreRecord(
            idToko = storeId,
            namaToko = storeName,
            jenisUsaha = businessType,
            alamat = null,
            logoFileId = null,
            saldoAwal = openingBalance,
            saldoKumulatif = openingBalance,
            hapusPada = null,
        )
        stores[storeId] = store
        val owner = Akun(
            id = ownerId,
            idToko = storeId,
            email = email,
            nama = storeName,
            namaNormalisasi = AuthRepository.normalize(storeName),
            peran = Peran.OWNER,
            hash = password.hash,
            salt = password.salt,
            status = StatusAkun.AKTIF,
            wajibGantiSandi = false,
            nomorHp = null,
            fotoFileId = profilePhotoId,
            gagalLogin = 0,
            terkunciSampai = null,
            terakhirLogin = null,
            hapusTokoPada = null,
        )
        accounts[ownerId] = owner
        auditLogs += AuditRecord(storeId, ownerId, "AKUN", "OWNER_DIBUAT")
        return owner to store.toRingkas()
    }

    override fun findOwnerByEmail(email: String): Akun? =
        accounts.values.firstOrNull { it.peran == Peran.OWNER && it.email == email }?.withStoreDeletion()

    override fun findCashier(storeId: String, username: String): Akun? {
        val normalized = AuthRepository.normalize(username)
        return accounts.values.firstOrNull {
            it.idToko == storeId &&
                it.peran == Peran.KASIR &&
                it.namaNormalisasi == normalized &&
                it.status != StatusAkun.DIHAPUS
        }?.withStoreDeletion()
    }

    override fun findAccount(accountId: UUID): Akun? =
        accounts[accountId]?.withStoreDeletion()

    override fun findStore(storeId: String): TokoRingkas? =
        stores[storeId]?.toRingkas()

    override fun registerSession(account: Akun, session: TokenSession) {
        val now = System.currentTimeMillis()
        sessions[session.jti] = SessionRecord(session.jti, account.id, session.expiresAt)
        accounts[account.id]?.let { current ->
            accounts[account.id] = current.copy(terakhirLogin = now, gagalLogin = 0, terkunciSampai = null)
        }
        auditLogs += AuditRecord(account.idToko, account.id, "LOGIN", "LOGIN_BERHASIL")
        if (account.peran == Peran.OWNER) {
            stores[account.idToko]?.let { store ->
                val deleteAt = store.hapusPada
                if (deleteAt != null && deleteAt > now) {
                    store.hapusPada = null
                    auditLogs += AuditRecord(account.idToko, account.id, "AKUN", "OWNER_HAPUS_DIBATALKAN")
                }
            }
        }
    }

    override fun isSessionActive(jti: UUID, accountId: UUID, storeId: String, role: String, now: Long): Boolean {
        val account = accounts[accountId] ?: return false
        if (account.idToko != storeId || account.peran.name != role || account.status != StatusAkun.AKTIF) return false
        val store = stores[storeId] ?: return false
        if (store.hapusPada != null) return false
        val session = sessions[jti] ?: return false
        return session.akunId == accountId && session.dicabutPada == null && session.berakhirPada > now
    }

    override fun logout(account: Akun, jti: UUID): Boolean {
        val session = sessions[jti] ?: return false
        if (session.akunId != account.id || session.dicabutPada != null) return false
        session.dicabutPada = System.currentTimeMillis()
        auditLogs += AuditRecord(account.idToko, account.id, "LOGIN", "LOGOUT")
        return true
    }

    override fun revokeOtherSessions(account: Akun, currentJti: UUID): Int {
        val now = System.currentTimeMillis()
        var count = 0
        sessions.values.forEach { session ->
            if (session.akunId == account.id && session.jti != currentJti && session.dicabutPada == null) {
                session.dicabutPada = now
                count++
            }
        }
        auditLogs += AuditRecord(account.idToko, account.id, "LOGIN", "SESI_LAIN_DICABUT")
        return count
    }

    override fun updatePassword(account: Akun, digest: PasswordDigest, forceChange: Boolean, keepJti: UUID?) {
        accounts[account.id]?.let { current ->
            accounts[account.id] = current.copy(
                hash = digest.hash,
                salt = digest.salt,
                wajibGantiSandi = forceChange,
            )
        }
        val now = System.currentTimeMillis()
        sessions.values.forEach { session ->
            if (session.akunId == account.id && session.dicabutPada == null && (keepJti == null || session.jti != keepJti)) {
                session.dicabutPada = now
            }
        }
        auditLogs += AuditRecord(account.idToko, account.id, "AKUN", "SANDI_DIUBAH")
    }

    override fun updateProfile(account: Akun, name: String?, phone: String?, photoId: UUID?, email: String?): Akun {
        val current = accounts[account.id]!!
        val updatedName = name ?: current.nama
        val normalized = AuthRepository.normalize(updatedName)
        if (current.peran == Peran.KASIR && accounts.values.any {
                it.id != current.id &&
                    it.idToko == current.idToko &&
                    it.peran == Peran.KASIR &&
                    it.status != StatusAkun.DIHAPUS &&
                    it.namaNormalisasi == normalized
            }
        ) {
            throw SQLException("duplicate key value violates unique constraint \"akun_nama_kasir_per_toko_unik\"", "23505")
        }
        if (email != null && current.peran == Peran.OWNER && accounts.values.any {
                it.id != current.id && it.email?.lowercase() == email.lowercase()
            }
        ) {
            throw SQLException("duplicate key value violates unique constraint \"akun_email_normalisasi_unik\"", "23505")
        }
        val updated = current.copy(
            nama = updatedName,
            namaNormalisasi = normalized,
            nomorHp = phone ?: current.nomorHp,
            fotoFileId = photoId ?: current.fotoFileId,
            email = if (email != null && current.peran == Peran.OWNER) email else current.email,
        )
        accounts[account.id] = updated
        auditLogs += AuditRecord(account.idToko, account.id, "AKUN", "PROFIL_DIUBAH")
        return updated.withStoreDeletion()
    }

    override fun updateStore(account: Akun, name: String?, businessType: String?, address: String?, logoId: UUID?): TokoRingkas {
        val store = stores[account.idToko]!!
        if (name != null) store.namaToko = name
        if (businessType != null) store.jenisUsaha = businessType
        if (address != null) store.alamat = address
        if (logoId != null) store.logoFileId = logoId
        auditLogs += AuditRecord(account.idToko, account.id, "AKUN", "TOKO_DIUBAH")
        return store.toRingkas()
    }

    override fun updateOpeningBalance(account: Akun, newBalance: Long): TokoRingkas {
        val store = stores[account.idToko]!!
        val diff = newBalance - store.saldoAwal
        store.saldoAwal = newBalance
        store.saldoKumulatif += diff
        auditLogs += AuditRecord(account.idToko, account.id, "KEUANGAN", "SALDO_AWAL_DIUBAH")
        return store.toRingkas()
    }

    override fun createCashier(storeId: String, accountId: UUID, name: String, digest: PasswordDigest): Akun {
        val normalized = AuthRepository.normalize(name)
        if (accounts.containsKey(accountId)) {
            throw SQLException("duplicate key value violates unique constraint \"akun_pkey\"", "23505")
        }
        if (accounts.values.any {
                it.idToko == storeId &&
                    it.peran == Peran.KASIR &&
                    it.status != StatusAkun.DIHAPUS &&
                    it.namaNormalisasi == normalized
            }
        ) {
            throw SQLException("duplicate key value violates unique constraint \"akun_nama_kasir_per_toko_unik\"", "23505")
        }
        val cashier = Akun(
            id = accountId,
            idToko = storeId,
            email = null,
            nama = name,
            namaNormalisasi = normalized,
            peran = Peran.KASIR,
            hash = digest.hash,
            salt = digest.salt,
            status = StatusAkun.AKTIF,
            wajibGantiSandi = true,
            nomorHp = null,
            fotoFileId = null,
            gagalLogin = 0,
            terkunciSampai = null,
            terakhirLogin = null,
            hapusTokoPada = stores[storeId]?.hapusPada,
        )
        accounts[accountId] = cashier
        auditLogs += AuditRecord(storeId, accountId, "AKUN", "KASIR_DIBUAT")
        return cashier
    }

    override fun listCashiers(storeId: String): List<Akun> =
        accounts.values.filter {
            it.idToko == storeId && it.peran == Peran.KASIR && it.status != StatusAkun.DIHAPUS
        }.map { it.withStoreDeletion() }

    override fun setCashierStatus(storeId: String, accountId: UUID, status: StatusAkun): Boolean {
        val account = accounts[accountId] ?: return false
        if (account.idToko != storeId || account.peran != Peran.KASIR || account.status == StatusAkun.DIHAPUS) return false
        accounts[accountId] = account.copy(status = status)
        if (status != StatusAkun.AKTIF) revokeAllForAccount(accountId)
        auditLogs += AuditRecord(storeId, accountId, "AKUN", if (status == StatusAkun.AKTIF) "KASIR_AKTIF" else "KASIR_NONAKTIF")
        return true
    }

    override fun softDeleteCashier(storeId: String, accountId: UUID): Akun? {
        val account = accounts[accountId] ?: return null
        if (account.idToko != storeId || account.peran != Peran.KASIR || account.status == StatusAkun.DIHAPUS) return null
        val deleted = account.copy(status = StatusAkun.DIHAPUS)
        accounts[accountId] = deleted
        revokeAllForAccount(accountId)
        auditLogs += AuditRecord(storeId, accountId, "AKUN", "KASIR_DIHAPUS")
        return deleted.withStoreDeletion()
    }

    override fun scheduleOwnerDeletion(account: Akun): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val deleteAt = now + AuthRepository.OWNER_DELETION_GRACE_MILLIS
        stores[account.idToko]?.hapusPada = deleteAt
        accounts.values.filter { it.idToko == account.idToko }.forEach { revokeAllForAccount(it.id) }
        auditLogs += AuditRecord(account.idToko, account.id, "AKUN", "OWNER_HAPUS_TERJADWAL")
        return now to deleteAt
    }

    override fun purgeExpiredStores(now: Long): Int {
        val expired = stores.values.filter { it.hapusPada != null && it.hapusPada!! <= now }.map { it.idToko }
        expired.forEach { storeId ->
            stores.remove(storeId)
            val removedIds = accounts.values.filter { it.idToko == storeId }.map { it.id }
            removedIds.forEach { id ->
                accounts.remove(id)
                sessions.entries.removeIf { it.value.akunId == id }
            }
            auditLogs.removeIf { it.idToko == storeId }
        }
        return expired.size
    }

    override fun recordFailedLogin(loginKey: String): Long? {
        val now = System.currentTimeMillis()
        val existing = attempts[loginKey]
        val count = (existing?.jumlahGagal ?: 0) + 1
        val lockUntil = if (count >= AuthRepository.MAX_FAILED_ATTEMPTS) now + AuthRepository.LOCKOUT_MILLIS else null
        attempts[loginKey] = AttemptRecord(if (lockUntil == null) count else 0, lockUntil)
        return lockUntil
    }

    override fun loginLocked(loginKey: String): Boolean {
        val until = attempts[loginKey]?.terkunciSampai ?: return false
        return until > System.currentTimeMillis()
    }

    override fun clearLoginFailures(loginKey: String) {
        attempts.remove(loginKey)
    }

    private fun revokeAllForAccount(accountId: UUID) {
        val now = System.currentTimeMillis()
        sessions.values.forEach { if (it.akunId == accountId && it.dicabutPada == null) it.dicabutPada = now }
    }

    private fun generateStoreId(): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        repeat(20) {
            val candidate = buildString { repeat(8) { append(alphabet[random.nextInt(alphabet.length)]) } }
            if (!stores.containsKey(candidate)) return candidate
        }
        error("Tidak dapat membuat ID Toko unik.")
    }

    private fun Akun.withStoreDeletion() = copy(hapusTokoPada = stores[idToko]?.hapusPada)

    private fun StoreRecord.toRingkas() = TokoRingkas(
        idToko = idToko,
        namaToko = namaToko,
        jenisUsaha = jenisUsaha,
        alamat = alamat,
        logoFileId = logoFileId,
        saldoAwal = saldoAwal,
        saldoKumulatif = saldoKumulatif,
    )
}
