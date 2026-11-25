package com.aura.app.core.format

import org.junit.Assert.assertEquals
import org.junit.Test

class JumlahStokFormatterTest {
    @Test
    fun memformatRibuanDanJutaanDenganPemisahIndonesia() {
        assertEquals("25.000 gram", JumlahStokFormatter.format(25_000, "gram"))
        assertEquals("1.250.000 pcs", JumlahStokFormatter.format(1_250_000, "pcs"))
    }

    @Test
    fun memformatNolDenganSatuan() {
        assertEquals("0 pcs", JumlahStokFormatter.format(0, "pcs"))
    }
}
