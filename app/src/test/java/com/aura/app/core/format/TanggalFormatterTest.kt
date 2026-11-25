package com.aura.app.core.format

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class TanggalFormatterTest {
    @Test
    fun memformatTanggalDalamZonaWib() {
        val epochMillis = Instant.parse("2026-11-17T12:48:00Z").toEpochMilli()

        assertEquals("17 Nov 2026, 19.48 WIB", TanggalFormatter.format(epochMillis))
    }

    @Test
    fun menanganiPergantianTahunWib() {
        val epochMillis = Instant.parse("2026-12-31T16:59:00Z").toEpochMilli()

        assertEquals("31 Des 2026, 23.59 WIB", TanggalFormatter.format(epochMillis))
    }

    @Test
    fun tanggalUtcTahunBaruMenjadiTahunBaruWib() {
        val epochMillis = Instant.parse("2026-12-31T17:00:00Z").toEpochMilli()

        assertEquals("1 Jan 2027, 00.00 WIB", TanggalFormatter.format(epochMillis))
    }
}
