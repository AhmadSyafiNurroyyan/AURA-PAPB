package com.aura.app.core.access

import com.aura.app.core.model.Peran

enum class AksiAkses {
    DASHBOARD,
    MENU_MANAJEMEN,
    ANALISIS,
    PROFIL,
    KASIR,
    KATALOG_BARANG,
    KELOLA_BARANG,
    KELOLA_KATEGORI,
    RESTOCK,
    PENYESUAIAN_STOK,
    KELOLA_SUPPLIER,
    CATAT_TRANSAKSI,
    RIWAYAT_TRANSAKSI_HARI_INI,
    BATALKAN_TRANSAKSI,
    AJUKAN_PEMBATALAN,
    KELOLA_PENGELUARAN,
    KELOLA_PEMASUKAN,
    KELOLA_AKUN_KASIR,
    METODE_PEMBAYARAN,
    RIWAYAT_AKTIVITAS,
    PENGATURAN_TOKO,
    UBAH_PROFIL_SENDIRI,
    UBAH_KATA_SANDI_SENDIRI,
    KELUAR_AKUN,
    KEMBALI_DARI_KASIR,
}

data class HasilAkses(
    val diizinkan: Boolean,
    val alasan: String? = null,
)

object AksesPeran {
    const val ALASAN_OWNER = "Hanya Owner yang dapat mengubah data ini"
    const val ALASAN_KASIR_KEMBALI = "Kasir hanya dapat keluar melalui profil avatar"

    private val aksiKasir = setOf(
        AksiAkses.PROFIL,
        AksiAkses.KASIR,
        AksiAkses.KATALOG_BARANG,
        AksiAkses.CATAT_TRANSAKSI,
        AksiAkses.RIWAYAT_TRANSAKSI_HARI_INI,
        AksiAkses.AJUKAN_PEMBATALAN,
        AksiAkses.UBAH_PROFIL_SENDIRI,
        AksiAkses.UBAH_KATA_SANDI_SENDIRI,
        AksiAkses.KELUAR_AKUN,
    )

    fun cek(aksi: AksiAkses, peran: Peran): HasilAkses {
        val diizinkan = peran == Peran.OWNER || aksi in aksiKasir
        if (diizinkan) return HasilAkses(diizinkan = true)

        val alasan = if (aksi == AksiAkses.KEMBALI_DARI_KASIR) {
            ALASAN_KASIR_KEMBALI
        } else {
            ALASAN_OWNER
        }
        return HasilAkses(diizinkan = false, alasan = alasan)
    }
}
