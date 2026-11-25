package com.aura.app.data.preferences

import com.aura.app.core.model.Peran
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RiwayatAkunHelperTest {

    private fun sample(id: String, loginAt: Long = 1000L) = RiwayatAkunLogin(
        akunId = id,
        idToko = "TOKO0001",
        nama = "Akun $id",
        email = null,
        peran = Peran.KASIR,
        fotoFileId = null,
        terakhirLogin = loginAt,
    )

    @Test
    fun `riwayat akun dibatasi maksimal 5 dan akun terbaru di urutan teratas`() {
        var daftar = emptyList<RiwayatAkunLogin>()
        for (i in 1..7) {
            daftar = RiwayatAkunHelper.tambahAtauPerbarui(daftar, sample("akun-$i", i.toLong()))
        }

        assertEquals(5, daftar.size)
        assertEquals(listOf("akun-7", "akun-6", "akun-5", "akun-4", "akun-3"), daftar.map { it.akunId })
    }

    @Test
    fun `login ulang akun yang sudah ada memindahkannya ke depan tanpa duplikat`() {
        val awal = listOf(sample("a", 1), sample("b", 2), sample("c", 3))
        val diperbarui = RiwayatAkunHelper.tambahAtauPerbarui(awal, sample("b", 99))

        assertEquals(3, diperbarui.size)
        assertEquals(listOf("b", "a", "c"), diperbarui.map { it.akunId })
        assertEquals(99L, diperbarui.first().terakhirLogin)
    }

    @Test
    fun `encode dan decode mempertahankan urutan serta decode data rusak aman`() {
        val awal = listOf(sample("a", 10), sample("b", 20))
        val json = RiwayatAkunHelper.encode(awal)
        val decoded = RiwayatAkunHelper.decode(json)

        assertEquals(awal, decoded)
        assertTrue(RiwayatAkunHelper.decode("bukan-json").isEmpty())
        assertTrue(RiwayatAkunHelper.decode(null).isEmpty())
    }

    @Test
    fun `hapusAkun menghapus entri sesuai akunId`() {
        val awal = listOf(sample("a"), sample("b"), sample("c"))
        val hasil = RiwayatAkunHelper.hapusAkun(awal, "b")

        assertEquals(listOf("a", "c"), hasil.map { it.akunId })
    }
}
