package com.aura.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aura.app.data.local.entity.AkunEntity
import com.aura.app.data.local.entity.TokoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokoDao {
    @Query("SELECT * FROM toko WHERE idToko = :idToko LIMIT 1")
    fun observeToko(idToko: String): Flow<TokoEntity?>

    @Query("SELECT * FROM toko WHERE idToko = :idToko LIMIT 1")
    suspend fun getToko(idToko: String): TokoEntity?

    @Upsert
    suspend fun upsert(toko: TokoEntity)

    @Query("DELETE FROM toko")
    suspend fun clearAll()
}

@Dao
interface AkunDao {
    @Query("SELECT * FROM akun WHERE id = :akunId LIMIT 1")
    fun observeAkun(akunId: String): Flow<AkunEntity?>

    @Query("SELECT * FROM akun WHERE idToko = :idToko AND peran = 'KASIR' AND status != 'DIHAPUS' ORDER BY namaAkun ASC")
    fun observeKasirAktifDanNonaktif(idToko: String): Flow<List<AkunEntity>>

    @Query("SELECT * FROM akun WHERE id = :akunId LIMIT 1")
    suspend fun getAkun(akunId: String): AkunEntity?

    @Upsert
    suspend fun upsert(akun: AkunEntity)

    @Upsert
    suspend fun upsertAll(daftar: List<AkunEntity>)

    @Query("DELETE FROM akun WHERE idToko = :idToko AND peran = 'KASIR'")
    suspend fun clearKasirByToko(idToko: String)

    @Transaction
    suspend fun replaceKasirByToko(idToko: String, daftar: List<AkunEntity>) {
        clearKasirByToko(idToko)
        if (daftar.isNotEmpty()) upsertAll(daftar)
    }

    @Query("DELETE FROM akun")
    suspend fun clearAll()
}
