package com.aura.server.db

import org.jetbrains.exposed.v1.core.Table

internal object BarangTable : Table("barang") {
    val id = uuid("id")
    val idToko = varchar("id_toko", 8)
    val nama = varchar("nama", 120)
    val satuan = varchar("satuan", 30)
    val sku = varchar("sku", 100).nullable()
    val hargaJual = long("harga_jual")
    val stokMinimum = integer("stok_minimum")
    val stokSaatIni = integer("stok_saat_ini")
    val hppRataRata = long("hpp_rata_rata")
    val status = varchar("status", 12)
    val fotoFileId = uuid("foto_file_id").nullable()
    val dibuatOleh = uuid("dibuat_oleh")
    val dibuatPada = long("dibuat_pada")
    val diubahPada = long("diubah_pada")

    override val primaryKey = PrimaryKey(id)
}

internal object KategoriTable : Table("kategori") {
    val id = uuid("id")
    val idToko = varchar("id_toko", 8)
    val nama = varchar("nama", 80)
    val dibuatPada = long("dibuat_pada")

    override val primaryKey = PrimaryKey(id)
}

internal object BarangKategoriTable : Table("barang_kategori") {
    val idToko = varchar("id_toko", 8)
    val barangId = uuid("barang_id")
    val kategoriId = uuid("kategori_id")

    override val primaryKey = PrimaryKey(barangId, kategoriId)
}

internal object SupplierTable : Table("supplier") {
    val id = uuid("id")
    val idToko = varchar("id_toko", 8)
    val nama = varchar("nama", 120)
    val link = varchar("link", 2048).nullable()
    val catatan = varchar("catatan", 500).nullable()
    val status = varchar("status", 10)
    val dibuatPada = long("dibuat_pada")

    override val primaryKey = PrimaryKey(id)
}

internal object HargaBeliSupplierTable : Table("harga_beli_supplier") {
    val idToko = varchar("id_toko", 8)
    val barangId = uuid("barang_id")
    val supplierId = uuid("supplier_id")
    val hargaPerItem = long("harga_per_item")
    val diperbaruiPada = long("diperbarui_pada")

    override val primaryKey = PrimaryKey(barangId, supplierId)
}

internal object PergerakanStokTable : Table("pergerakan_stok") {
    val id = uuid("id")
    val idToko = varchar("id_toko", 8)
    val barangId = uuid("barang_id")
    val akunId = uuid("akun_id").nullable()
    val jenis = varchar("jenis", 20)
    val sumber = varchar("sumber", 40)
    val jumlah = integer("jumlah")
    val stokSebelum = integer("stok_sebelum")
    val stokSesudah = integer("stok_sesudah")
    val referensiId = uuid("referensi_id").nullable()
    val jenisReferensi = varchar("jenis_referensi", 30).nullable()
    val dibuatPada = long("dibuat_pada")

    override val primaryKey = PrimaryKey(id)
}

internal object FileTable : Table("file") {
    val id = uuid("id")
    val idToko = varchar("id_toko", 8)
    val mimeType = varchar("mime_type", 30)
    val ukuran = long("ukuran")
    val dibuatPada = long("dibuat_pada")

    override val primaryKey = PrimaryKey(id)
}
