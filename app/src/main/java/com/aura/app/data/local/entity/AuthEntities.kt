package com.aura.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aura.app.core.model.Peran
import com.aura.app.core.model.StatusAkun

@Entity(tableName = "toko")
data class TokoEntity(
    @PrimaryKey val idToko: String,
    val namaToko: String,
    val jenisUsaha: String? = null,
    val alamat: String? = null,
    val logoFileId: String? = null,
    val saldoAwal: Long,
    val saldoKumulatif: Long,
    val namaPenerima: String? = null,
    val infoRekening: String? = null,
    val qrisFileId: String? = null,
)

@Entity(tableName = "akun")
data class AkunEntity(
    @PrimaryKey val id: String,
    val idToko: String,
    val email: String? = null,
    val namaAkun: String,
    val peran: Peran,
    val status: StatusAkun,
    val wajibGantiSandi: Boolean = false,
    val nomorHp: String? = null,
    val fotoFileId: String? = null,
    val terakhirLogin: Long? = null,
)
