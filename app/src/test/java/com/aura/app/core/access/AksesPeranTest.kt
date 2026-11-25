package com.aura.app.core.access

import com.aura.app.core.model.Peran
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AksesPeranTest {
    @Test
    fun ownerDiizinkanDanKasirBolehMemakaiKasir() {
        assertTrue(AksesPeran.cek(AksiAkses.KELOLA_BARANG, Peran.OWNER).diizinkan)
        assertTrue(AksesPeran.cek(AksiAkses.CATAT_TRANSAKSI, Peran.KASIR).diizinkan)
    }

    @Test
    fun kasirTidakBolehMengubahMasterDanAlasanBakuDikembalikan() {
        val hasil = AksesPeran.cek(AksiAkses.KELOLA_BARANG, Peran.KASIR)

        assertFalse(hasil.diizinkan)
        assertEquals(AksesPeran.ALASAN_OWNER, hasil.alasan)
    }

    @Test
    fun tombolKembaliKasirMemakaiAlasanKhusus() {
        val hasil = AksesPeran.cek(AksiAkses.KEMBALI_DARI_KASIR, Peran.KASIR)

        assertFalse(hasil.diizinkan)
        assertEquals(AksesPeran.ALASAN_KASIR_KEMBALI, hasil.alasan)
    }
}
