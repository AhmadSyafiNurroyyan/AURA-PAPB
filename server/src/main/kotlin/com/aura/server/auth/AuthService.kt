package com.aura.server.auth

import com.aura.server.plugins.ApiException
import io.ktor.http.HttpStatusCode
import java.sql.SQLException
import java.util.UUID

internal data class LoginResult(val account: Akun, val store: TokoRingkas, val token: TokenSession)

internal class AuthService(
    private val repository: AuthRepository,
    private val jwt: JwtService,
) {
    fun registerOwner(request: OwnerRegisterRequest): LoginResult {
        val email = request.email?.trim()?.lowercase().orEmpty()
        val password = request.kataSandi.orEmpty()
        val storeName = request.namaToko?.trim().orEmpty()
        val openingBalance = request.saldoAwal
        if (!EMAIL_PATTERN.matches(email) || password.length < MIN_PASSWORD_LENGTH || storeName.isBlank() || openingBalance == null || openingBalance < 0) {
            badRequest("DATA_REGISTRASI_TIDAK_VALID", "Data pendaftaran tidak valid.")
        }
        if (storeName.length > 120 || (request.jenisUsaha?.length ?: 0) > 80) {
            badRequest("DATA_REGISTRASI_TIDAK_VALID", "Data pendaftaran tidak valid.")
        }
        val created = try {
            repository.createOwner(email, PasswordHasher.hash(password), storeName, request.jenisUsaha?.trim(), openingBalance, request.fotoProfilFileId)
        } catch (error: Exception) {
            if (error.findSqlState() == "23505") conflict("EMAIL_SUDAH_DIPAKAI", "Email sudah digunakan.")
            throw error
        }
        val token = jwt.issue(created.first)
        repository.registerSession(created.first, token)
        val refreshed = repository.findAccount(created.first.id) ?: created.first
        return LoginResult(refreshed, repository.findStore(refreshed.idToko)!!, token)
    }

    fun loginOwner(request: OwnerLoginRequest): LoginResult {
        val email = request.email?.trim()?.lowercase().orEmpty()
        val loginKey = "OWNER:$email"
        requireNotLocked(loginKey)
        val account = repository.findOwnerByEmail(email)
        val digest = account?.let { PasswordDigest(it.hash, it.salt) } ?: DUMMY_DIGEST
        val validPassword = PasswordHasher.verify(request.kataSandi.orEmpty(), digest.hash, digest.salt)
        if (account == null || account.status != StatusAkun.AKTIF || account.hapusTokoPada?.let { it <= System.currentTimeMillis() } == true || !validPassword) loginFailed(loginKey)
        return loginSuccess(loginKey, account)
    }

    fun loginCashier(request: KasirLoginRequest): LoginResult {
        val storeId = request.idToko?.trim()?.uppercase().orEmpty()
        val username = request.namaAkun?.trim().orEmpty()
        val loginKey = "KASIR:$storeId:${AuthRepository.normalize(username)}"
        requireNotLocked(loginKey)
        val account = repository.findCashier(storeId, username)
        val digest = account?.let { PasswordDigest(it.hash, it.salt) } ?: DUMMY_DIGEST
        val validPassword = PasswordHasher.verify(request.kataSandi.orEmpty(), digest.hash, digest.salt)
        if (account == null || account.status != StatusAkun.AKTIF || account.hapusTokoPada != null || !validPassword) loginFailed(loginKey)
        return loginSuccess(loginKey, account)
    }

    fun changePassword(account: Akun, request: ChangePasswordRequest, currentJti: UUID? = null) {
        val newPassword = request.kataSandiBaru.orEmpty()
        if (!PasswordHasher.verify(request.kataSandiSaatIni.orEmpty(), account.hash, account.salt)) {
            throw ApiException(HttpStatusCode.Unauthorized, "KREDENSIAL_TIDAK_VALID", "Kata sandi saat ini tidak valid.")
        }
        validatePassword(newPassword)
        repository.updatePassword(account, PasswordHasher.hash(newPassword), forceChange = false, keepJti = currentJti)
    }

    fun createCashier(owner: Akun, request: CreateKasirRequest): Akun {
        val name = request.namaAkun?.trim().orEmpty()
        val password = request.kataSandiAwal.orEmpty()
        if (owner.peran != Peran.OWNER) forbidden()
        if (request.id == null || name.isBlank() || name.length > 80 || password.length < MIN_PASSWORD_LENGTH) {
            badRequest("DATA_KASIR_TIDAK_VALID", "Data Kasir tidak valid.")
        }
        return try {
            repository.createCashier(owner.idToko, request.id, name, PasswordHasher.hash(password))
        } catch (error: Exception) {
            if (error.findSqlState() == "23505") conflict("NAMA_AKUN_SUDAH_DIPAKAI", "Nama akun sudah digunakan di toko ini.")
            throw error
        }
    }

    fun resetCashierPassword(owner: Akun, cashierId: UUID, request: ResetKasirPasswordRequest): Akun {
        if (owner.peran != Peran.OWNER) forbidden()
        val cashier = repository.findAccount(cashierId)
        if (cashier == null || cashier.idToko != owner.idToko || cashier.peran != Peran.KASIR || cashier.status == StatusAkun.DIHAPUS) notFound()
        val password = request.kataSandiAwal.orEmpty()
        validatePassword(password)
        repository.updatePassword(cashier, PasswordHasher.hash(password), forceChange = true)
        return repository.findAccount(cashierId)!!
    }

    fun setCashierStatus(owner: Akun, cashierId: UUID, request: SetKasirStatusRequest): Akun {
        if (owner.peran != Peran.OWNER) forbidden()
        val status = when (request.status) {
            "AKTIF" -> StatusAkun.AKTIF
            "NONAKTIF" -> StatusAkun.NONAKTIF
            else -> badRequest("STATUS_TIDAK_VALID", "Status Kasir tidak valid.")
        }
        if (!repository.setCashierStatus(owner.idToko, cashierId, status)) notFound()
        return repository.findAccount(cashierId)!!
    }

    fun deleteCashier(owner: Akun, cashierId: UUID): Akun {
        if (owner.peran != Peran.OWNER) forbidden()
        return repository.softDeleteCashier(owner.idToko, cashierId) ?: notFound()
    }

    fun deleteOwner(owner: Akun, request: OwnerDeleteRequest): OwnerDeleteResponse {
        if (owner.peran != Peran.OWNER) forbidden()
        if (request.konfirmasi != "HAPUS") badRequest("KONFIRMASI_TIDAK_VALID", "Ketik HAPUS untuk menjadwalkan penghapusan akun.")
        if (!PasswordHasher.verify(request.kataSandiSaatIni.orEmpty(), owner.hash, owner.salt)) {
            throw ApiException(HttpStatusCode.Unauthorized, "KREDENSIAL_TIDAK_VALID", "Kata sandi saat ini tidak valid.")
        }
        val (requestedAt, deleteAt) = repository.scheduleOwnerDeletion(owner)
        return OwnerDeleteResponse(requestedAt, deleteAt)
    }

    fun validatePassword(password: String) {
        if (password.length < MIN_PASSWORD_LENGTH) {
            badRequest("KATA_SANDI_TIDAK_VALID", "Kata sandi harus minimal 8 karakter.")
        }
    }

    private fun loginSuccess(key: String, original: Akun): LoginResult {
        repository.clearLoginFailures(key)
        val token = jwt.issue(original)
        repository.registerSession(original, token)
        val refreshed = repository.findAccount(original.id)!!
        val store = repository.findStore(refreshed.idToko)!!
        return LoginResult(refreshed, store, token)
    }

    private fun requireNotLocked(key: String) {
        if (repository.loginLocked(key)) throw ApiException(HttpStatusCode.Unauthorized, "LOGIN_DIKUNCI", "Login dikunci sementara. Coba lagi dalam 5 menit.")
    }

    private fun loginFailed(key: String): Nothing {
        repository.recordFailedLogin(key)
        throw ApiException(HttpStatusCode.Unauthorized, "KREDENSIAL_TIDAK_VALID", "Email atau informasi login tidak valid.")
    }

    private fun badRequest(code: String, message: String): Nothing = throw ApiException(HttpStatusCode.BadRequest, code, message)
    private fun forbidden(): Nothing = throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Hanya Owner yang dapat mengubah data ini.")
    private fun notFound(): Nothing = throw ApiException(HttpStatusCode.NotFound, "DATA_TIDAK_DITEMUKAN", "Data tidak ditemukan.")
    private fun conflict(code: String, message: String): Nothing = throw ApiException(HttpStatusCode.Conflict, code, message)

    private fun Throwable.findSqlState(): String? = generateSequence(this) { it.cause }
        .filterIsInstance<SQLException>().firstOrNull()?.sqlState

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
        private val EMAIL_PATTERN = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        private val DUMMY_DIGEST = PasswordHasher.hash("dummy-password-never-used")
    }
}
