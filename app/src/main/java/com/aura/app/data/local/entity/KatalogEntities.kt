package com.aura.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aura.app.core.model.StatusAkun
import com.aura.app.core.model.StatusBarang

@Entity(
    tableName = "barang",
    indices = [Index("idToko"), Index("sku")],
)
data class BarangEntity(
    @PrimaryKey val id: String,
    val idToko: String,
    val sku: String? = null,
    val nama: String,
    val satuan: String,
    val hargaJual: Long,
    val hppRataRata: Long,
    val stokSaatIni: Int,
    val stokMinimum: Int,
    val status: StatusBarang,
    val fotoFileId: String? = null,
    val dibuatOleh: String? = null,
    val dibuatPada: Long,
)

@Entity(
    tableName = "kategori",
    indices = [Index("idToko")],
)
data class KategoriEntity(
    @PrimaryKey val id: String,
    val idToko: String,
    val nama: String,
)

@Entity(
    tableName = "barang_kategori",
    primaryKeys = ["barangId", "kategoriId"],
    indices = [Index("kategoriId")],
)
data class BarangKategoriCrossRef(
    val barangId: String,
    val kategoriId: String,
)

@Entity(
    tableName = "supplier",
    indices = [Index("idToko")],
)
data class SupplierEntity(
    @PrimaryKey val id: String,
    val idToko: String,
    val nama: String,
    val link: String? = null,
    val catatan: String? = null,
    val status: StatusAkun,
)

@Entity(
    tableName = "harga_beli_supplier",
    indices = [Index("barangId"), Index("supplierId")],
)
data class HargaBeliSupplierEntity(
    @PrimaryKey val id: String,
    val idToko: String,
    val barangId: String,
    val supplierId: String,
    val supplierNama: String,
    val hargaPerItem: Long,
    val terakhirDiperbarui: Long,
)
