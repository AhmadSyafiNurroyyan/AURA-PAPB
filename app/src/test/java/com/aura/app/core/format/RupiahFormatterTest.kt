package com.aura.app.core.format

import org.junit.Assert.assertEquals
import org.junit.Test

class RupiahFormatterTest {
    @Test
    fun memformatRibuanJutaanDanNol() {
        assertEquals("Rp 1.250", RupiahFormatter.format(1_250))
        assertEquals("Rp 1.250.000", RupiahFormatter.format(1_250_000))
        assertEquals("Rp 0", RupiahFormatter.format(0))
    }

    @Test
    fun menampilkanTandaMinusSebelumRupiah() {
        assertEquals("-Rp 1.250.000", RupiahFormatter.format(-1_250_000))
        assertEquals("-Rp 9.223.372.036.854.775.808", RupiahFormatter.format(Long.MIN_VALUE))
    }
}
