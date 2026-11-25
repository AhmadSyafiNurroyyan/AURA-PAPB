package com.aura.app.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class ApiErrorParserTest {

    @Test
    fun `parse membaca kode dan pesan Bahasa Indonesia dari JSON server`() {
        val raw = """{"kode":"STOK_TIDAK_CUKUP","pesan":"Stok Indomie tidak mencukupi. Tersedia 2 pcs."}"""

        val exception = ApiErrorParser.parse(409, raw)

        assertEquals(409, exception.httpCode)
        assertEquals("STOK_TIDAK_CUKUP", exception.kode)
        assertEquals("Stok Indomie tidak mencukupi. Tersedia 2 pcs.", exception.message)
    }

    @Test
    fun `parse memakai pesan fallback saat body kosong atau bukan JSON`() {
        val empty401 = ApiErrorParser.parse(401, null)
        assertEquals("SESI_TIDAK_VALID", empty401.kode)
        assertEquals("Sesi tidak valid atau telah berakhir.", empty401.message)

        val invalid403 = ApiErrorParser.parse(403, "<html>Forbidden</html>")
        assertEquals("AKSES_DITOLAK", invalid403.kode)
        assertEquals("Anda tidak memiliki izin untuk aksi ini.", invalid403.message)

        val server500 = ApiErrorParser.parse(500, "{}")
        assertEquals("KESALAHAN_SERVER", server500.kode)
    }
}
