package com.aura.app.data.repository

import androidx.room.withTransaction
import com.aura.app.core.model.Peran
import com.aura.app.core.model.StatusAkun
import com.aura.app.data.local.AppDatabase
import com.aura.app.data.local.entity.AkunEntity
import com.aura.app.data.local.entity.TokoEntity
import com.aura.app.data.network.KoneksiChecker
import com.aura.app.data.preferences.SesiRepository
import com.aura.app.data.remote.datasource.AuthRemoteDataSource
import com.aura.app.data.remote.dto.CreateKasirRequestDto
import com.aura.app.data.remote.dto.ResetKasirPasswordRequestDto
import com.aura.app.data.remote.dto.SetKasirStatusRequestDto
import com.aura.app.data.remote.dto.UpdateProfileRequestDto
import com.aura.app.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

interface AkunRepository {
    fun observeAkun(akunId: String): Flow<AkunEntity?>
    fun observeToko(idToko: String): Flow<TokoEntity?>
    fun observeKasir(idToko: String): Flow<List<AkunEntity>>

    suspend fun refreshProfil(): AkunEntity
    suspend fun updateProfil(nama: String?, email: String?, nomorHp: String?): AkunEntity
    suspend fun refreshKasir(idToko: String)
    suspend fun tambahKasir(namaAkun: String, kataSandiAwal: String): AkunEntity
    suspend fun ubahStatusKasir(akunId: String, aktif: Boolean): AkunEntity
    suspend fun resetKataSandiKasir(akunId: String, kataSandiAwal: String): AkunEntity
    suspend fun hapusKasir(akunId: String)
}

class DefaultAkunRepository(
    private val db: AppDatabase,
    private val remote: AuthRemoteDataSource,
    private val sesiRepository: SesiRepository,
    private val koneksi: KoneksiChecker,
) : AkunRepository {

    override fun observeAkun(akunId: String): Flow<AkunEntity?> = db.akunDao().observeAkun(akunId)

    override fun observeToko(idToko: String): Flow<TokoEntity?> = db.tokoDao().observeToko(idToko)

    override fun observeKasir(idToko: String): Flow<List<AkunEntity>> =
        db.akunDao().observeKasirAktifDanNonaktif(idToko)

    override suspend fun refreshProfil(): AkunEntity {
        koneksi.pastikanOnline()
        val me = remote.me()
        val akun = me.akun.toEntity()
        val existingToko = db.tokoDao().getToko(me.tokoRingkas.idToko)
        val toko = me.tokoRingkas.toEntity(existingToko)
        db.withTransaction {
            db.tokoDao().upsert(toko)
            db.akunDao().upsert(akun)
        }
        sesiRepository.perbaruiAkunAktif(
            nama = akun.namaAkun,
            email = akun.email,
            nomorHp = akun.nomorHp,
            fotoFileId = akun.fotoFileId,
            wajibGantiSandi = akun.wajibGantiSandi,
        )
        return akun
    }

    override suspend fun updateProfil(nama: String?, email: String?, nomorHp: String?): AkunEntity {
        koneksi.pastikanOnline()
        val current = sesiRepository.sesiAktif.first()
        val emailToSend = if (current?.peran == Peran.OWNER) email?.trim()?.lowercase() else null
        val response = remote.updateProfil(
            UpdateProfileRequestDto(
                nama = nama?.trim()?.takeIf { it.isNotEmpty() },
                email = emailToSend?.takeIf { it.isNotEmpty() },
                nomorHp = nomorHp?.trim(),
            ),
        )
        val updated = response.akun.toEntity()
        db.akunDao().upsert(updated)
        sesiRepository.perbaruiAkunAktif(
            nama = updated.namaAkun,
            email = updated.email,
            nomorHp = updated.nomorHp,
            fotoFileId = updated.fotoFileId,
            wajibGantiSandi = updated.wajibGantiSandi,
        )
        return updated
    }

    override suspend fun refreshKasir(idToko: String) {
        koneksi.pastikanOnline()
        val response = remote.listKasir()
        val list = response.data.map { it.toEntity(idToko) }
        db.akunDao().replaceKasirByToko(idToko, list)
    }

    override suspend fun tambahKasir(namaAkun: String, kataSandiAwal: String): AkunEntity {
        koneksi.pastikanOnline()
        val response = remote.createKasir(
            CreateKasirRequestDto(
                id = UUID.randomUUID().toString(),
                namaAkun = namaAkun.trim(),
                kataSandiAwal = kataSandiAwal,
            ),
        )
        val entity = response.akun.toEntity()
        db.akunDao().upsert(entity)
        return entity
    }

    override suspend fun ubahStatusKasir(akunId: String, aktif: Boolean): AkunEntity {
        koneksi.pastikanOnline()
        val targetStatus = if (aktif) StatusAkun.AKTIF.name else StatusAkun.NONAKTIF.name
        val response = remote.setKasirStatus(akunId, SetKasirStatusRequestDto(targetStatus))
        val entity = response.akun.toEntity()
        db.akunDao().upsert(entity)
        return entity
    }

    override suspend fun resetKataSandiKasir(akunId: String, kataSandiAwal: String): AkunEntity {
        koneksi.pastikanOnline()
        val response = remote.resetKasirPassword(
            akunId,
            ResetKasirPasswordRequestDto(kataSandiAwal = kataSandiAwal),
        )
        val entity = response.akun.toEntity()
        db.akunDao().upsert(entity)
        return entity
    }

    override suspend fun hapusKasir(akunId: String) {
        koneksi.pastikanOnline()
        val response = remote.deleteKasir(akunId)
        db.akunDao().upsert(response.akun.toEntity())
        sesiRepository.hapusAkunDariRiwayat(akunId)
    }
}
