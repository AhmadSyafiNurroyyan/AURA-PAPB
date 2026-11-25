package com.aura.app.navigation

/** Route string yang dipakai oleh Navigation Compose. */
object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER_STEP1 = "register_step1"
    const val REGISTER_STEP2 = "register_step2"
    const val DASHBOARD = "dashboard"
    const val MENU_MANAJEMEN = "menu_manajemen"
    const val ANALISIS = "analisis"
    const val PROFIL = "profil"
    const val NOTIFIKASI = "notifikasi"
    const val PENGELUARAN = "pengeluaran"
    const val CATAT_PENGELUARAN = "catat_pengeluaran"
    const val PEMASUKAN = "pemasukan"
    const val CATAT_PEMASUKAN = "catat_pemasukan"
    const val MANAJEMEN_BARANG = "manajemen_barang"
    const val PROFIL_BARANG = "profil_barang"
    const val TAMBAH_BARANG = "tambah_barang"
    const val CATAT_RESTOCK = "catat_restock"
    const val PENYESUAIAN_STOK = "penyesuaian_stok"
    const val PERGERAKAN_STOK = "pergerakan_stok"
    const val SUPPLIER = "supplier"
    const val RIWAYAT_AKTIVITAS = "riwayat_aktivitas"
    const val PENGATURAN = "pengaturan"
    const val MANAJEMEN_AKUN_KASIR = "manajemen_akun_kasir"
    const val METODE_PEMBAYARAN = "metode_pembayaran"
    const val K1_BERANDA_KASIR = "k1_beranda_kasir"
    const val K2_CATAT_TRANSAKSI = "k2_catat_transaksi"
    const val K3_PEMBAYARAN = "k3_pembayaran"
    const val K4_TRANSAKSI_BERHASIL = "k4_transaksi_berhasil"
    const val K4_TRANSAKSI_BERHASIL_OWNER = "k4_transaksi_berhasil_owner"
    const val RIWAYAT_HARI_INI = "riwayat_hari_ini"
    const val RIWAYAT_HARI_INI_OWNER = "riwayat_hari_ini_owner"

    val all: List<String> = listOf(
        WELCOME, LOGIN, REGISTER_STEP1, REGISTER_STEP2, DASHBOARD, MENU_MANAJEMEN, ANALISIS, PROFIL,
        NOTIFIKASI, PENGELUARAN, CATAT_PENGELUARAN, PEMASUKAN, CATAT_PEMASUKAN, MANAJEMEN_BARANG,
        PROFIL_BARANG, TAMBAH_BARANG, CATAT_RESTOCK, PENYESUAIAN_STOK, PERGERAKAN_STOK, SUPPLIER,
        RIWAYAT_AKTIVITAS, PENGATURAN, MANAJEMEN_AKUN_KASIR, METODE_PEMBAYARAN, K1_BERANDA_KASIR,
        K2_CATAT_TRANSAKSI, K3_PEMBAYARAN, K4_TRANSAKSI_BERHASIL,
        K4_TRANSAKSI_BERHASIL_OWNER, RIWAYAT_HARI_INI, RIWAYAT_HARI_INI_OWNER,
    )
}
