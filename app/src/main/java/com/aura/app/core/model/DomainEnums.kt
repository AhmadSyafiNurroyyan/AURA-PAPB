package com.aura.app.core.model

enum class Peran {
    OWNER,
    KASIR,
}

enum class StatusStok {
    AMAN,
    MENIPIS,
    HABIS,
    DIARSIPKAN,
}

enum class JenisPergerakan {
    MASUK,
    KELUAR,
    PENYESUAIAN,
}

enum class SumberPergerakan {
    RESTOCK,
    STOK_AWAL,
    RETUR,
    PEMBATALAN,
    PENJUALAN,
    RUSAK,
    HILANG,
    KADALUARSA,
    PENYESUAIAN,
}

enum class AlasanPenyesuaian {
    RUSAK,
    HILANG,
    KADALUARSA,
    SALAH_CATAT,
    LAINNYA,
}

enum class MetodePembayaran {
    TUNAI,
    TRANSFER,
    QRIS,
}

enum class StatusTransaksi {
    SELESAI,
    DIBATALKAN,
    DIRETUR,
}

enum class StatusBarang {
    AKTIF,
    DIARSIPKAN,
}

enum class StatusAkun {
    AKTIF,
    NONAKTIF,
    DIHAPUS,
}

enum class StatusCatatanKeuangan {
    TERCATAT,
    DIBATALKAN,
}

enum class JenisPengeluaran {
    PEMBELIAN,
    KUSTOM,
}

enum class JenisCatatanTerbatas {
    PENJUALAN,
    RESTOCK,
    PENYESUAIAN_STOK,
    PENGELUARAN_KUSTOM,
    PEMASUKAN_KUSTOM,
}
