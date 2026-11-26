package com.aura.app.data.repository

import androidx.room.withTransaction
import com.aura.app.data.local.AppDatabase
import com.aura.app.data.network.KoneksiChecker
import com.aura.app.data.preferences.RiwayatAkunLogin
import com.aura.app.data.preferences.SesiAktif
import com.aura.app.data.preferences.SesiRepository
import com.aura.app.data.remote.ApiServerException
import com.aura.app.data.remote.datasource.AuthRemoteDataSource
import com.aura.app.data.remote.dto.ChangePasswordRequestDto
import com.aura.app.data.remote.dto.KasirLoginRequestDto
import com.aura.app.data.remote.dto.OwnerLoginRequestDto
import com.aura.app.data.remote.dto.OwnerRegisterRequestDto
import com.aura.app.data.remote.dto.SessionResponseDto
import com.aura.app.data.remote.dto.UpdateProfileRequestDto
import com.aura.app.data.remote.dto.toEntity
import com.aura.app.data.remote.dto.toSesiAktif
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface AuthRepository {
    val sesiAktif: Flow<SesiAktif?>
    val riwayatAkun: Flow<List<RiwayatAkunLogin>>

    suspend fun cekSesiSaatBuka(): SesiAktif?
    suspend fun loginOwner(email: String, kataSandi: String): SesiAktif
    suspend fun loginKasir(idToko: String, namaAkun: String, kataSandi: String): SesiAktif
    suspend fun registerOwner(
        nama: String,
        email: String,
        nomorHp: String,
        kataSandi: String,
        namaToko: String,
        jenisUsaha: String,
        saldoAwal: Long,
    ): HasilRegistrasiOwner

    suspend fun ubahKataSandi(kataSandiSaatIni: String, kataSandiBaru: String)
    suspend fun logout()
}

data class HasilRegistrasiOwner(
    val sesi: SesiAktif,
    val pesanPeringatanProfil: String? = null,
)

class DefaultAuthRepository(
    private val db: AppDatabase,
    private val remote: AuthRemoteDataSource,
    private val sesiRepository: SesiRepository,
    private val koneksi: KoneksiChecker,
) : AuthRepository {

    override val sesiAktif: Flow<SesiAktif?> = sesiRepository.sesiAktif
    override val riwayatAkun: Flow<List<RiwayatAkunLogin>> = sesiRepository.riwayatAkun

    override suspend fun cekSesiSaatBuka(): SesiAktif? {
        val current = sesiRepository.sesiAktif.first() ?: return null
        if (!koneksi.isOnline()) return current
        return try {
            val me = remote.me()
            val akunEntity = me.akun.toEntity()
            val existingToko = db.tokoDao().getToko(me.tokoRingkas.idToko)
            val tokoEntity = me.tokoRingkas.toEntity(existingToko)
            db.withTransaction {
                db.tokoDao().upsert(tokoEntity)
                db.akunDao().upsert(akunEntity)
            }
            sesiRepository.perbaruiAkunAktif(
                nama = akunEntity.namaAkun,
                email = akunEntity.email,
                nomorHp = akunEntity.nomorHp,
                fotoFileId = akunEntity.fotoFileId,
                wajibGantiSandi = akunEntity.wajibGantiSandi,
            )
            sesiRepository.sesiAktif.first()
        } catch (e: ApiServerException) {
            if (e.httpCode == 401) null else current
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            current
        }
    }

    override suspend fun loginOwner(email: String, kataSandi: String): SesiAktif {
        koneksi.pastikanOnline()
        val response = remote.loginOwner(
            OwnerLoginRequestDto(
                email = email.trim().lowercase(),
                kataSandi = kataSandi,
            ),
        )
        return simpanHasilSesi(response)
    }

    override suspend fun loginKasir(idToko: String, namaAkun: String, kataSandi: String): SesiAktif {
        koneksi.pastikanOnline()
        val response = remote.loginKasir(
            KasirLoginRequestDto(
                idToko = idToko.trim().uppercase(),
                namaAkun = namaAkun.trim(),
                kataSandi = kataSandi,
            ),
        )
        return simpanHasilSesi(response)
    }

    override suspend fun registerOwner(
        nama: String,
        email: String,
        nomorHp: String,
        kataSandi: String,
        namaToko: String,
        jenisUsaha: String,
        saldoAwal: Long,
    ): HasilRegistrasiOwner {
        koneksi.pastikanOnline()
        val response = remote.registerOwner(
            OwnerRegisterRequestDto(
                email = email.trim().lowercase(),
                kataSandi = kataSandi,
                namaToko = namaToko.trim(),
                jenisUsaha = jenisUsaha.trim().takeIf { it.isNotEmpty() },
                saldoAwal = saldoAwal,
            ),
        )
        var sesi = simpanHasilSesi(response)

        val trimmedName = nama.trim()
        val trimmedPhone = nomorHp.trim().takeIf { it.isNotEmpty() }
        if ((trimmedName.isNotEmpty() && trimmedName != response.akun.namaAkun) || trimmedPhone != null) {
            try {
                val updated = remote.updateProfil(
                    UpdateProfileRequestDto(
                        nama = trimmedName.takeIf { it.isNotEmpty() },
                        nomorHp = trimmedPhone,
                    ),
                )
                val updatedEntity = updated.akun.toEntity()
                db.akunDao().upsert(updatedEntity)
                sesiRepository.perbaruiAkunAktif(
                    nama = updatedEntity.namaAkun,
                    email = updatedEntity.email,
                    nomorHp = updatedEntity.nomorHp,
                    fotoFileId = updatedEntity.fotoFileId,
                    wajibGantiSandi = updatedEntity.wajibGantiSandi,
                )
                sesi = sesiRepository.sesiAktif.first() ?: sesi
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                return HasilRegistrasiOwner(
                    sesi = sesi,
                    pesanPeringatanProfil = e.message ?: "Profil Owner belum berhasil diperbarui.",
                )
            }
        }
        return HasilRegistrasiOwner(sesi = sesi)
    }

    override suspend fun ubahKataSandi(kataSandiSaatIni: String, kataSandiBaru: String) {
        koneksi.pastikanOnline()
        remote.changePassword(
            ChangePasswordRequestDto(
                kataSandiSaatIni = kataSandiSaatIni,
                kataSandiBaru = kataSandiBaru,
            ),
        )
        val current = sesiRepository.sesiAktif.first() ?: return
        sesiRepository.perbaruiAkunAktif(
            nama = current.nama,
            email = current.email,
            nomorHp = current.nomorHp,
            fotoFileId = current.fotoFileId,
            wajibGantiSandi = false,
        )
        db.akunDao().getAkun(current.akunId)?.let { entity ->
            db.akunDao().upsert(entity.copy(wajibGantiSandi = false))
        }
    }

    override suspend fun logout() {
        if (koneksi.isOnline() && !sesiRepository.tokenTersimpanSinkron().isNullOrBlank()) {
            try {
                remote.logout()
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (_: Exception) {
                // Tetap bersihkan sesi lokal walau server gagal merespons.
            }
        }
        sesiRepository.hapusSesiAktif()
        db.clearAllCache()
    }

    private suspend fun simpanHasilSesi(response: SessionResponseDto): SesiAktif {
        val sesi = response.toSesiAktif()
        val akunEntity = response.akun.toEntity()
        val sesiSebelumnya = sesiRepository.sesiAktif.first()
        if (sesiSebelumnya != null &&
            (sesiSebelumnya.akunId != sesi.akunId || sesiSebelumnya.idToko != sesi.idToko)
        ) {
            db.clearAllCache()
        }
        val existingToko = db.tokoDao().getToko(response.toko.idToko)
        val tokoEntity = response.toko.toEntity(existingToko)
        db.withTransaction {
            db.tokoDao().upsert(tokoEntity)
            db.akunDao().upsert(akunEntity)
        }
        sesiRepository.simpanSesi(sesi, response.akun.terakhirLogin ?: System.currentTimeMillis())
        return sesi
    }
}
