package com.aura.app.core.format

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TanggalFormatter {
    private val zonaWib = ZoneId.of("Asia/Jakarta")
    private val formatter = DateTimeFormatter
        .ofPattern("d MMM uuuu, HH.mm 'WIB'", Locale.forLanguageTag("id-ID"))
        .withZone(zonaWib)

    fun format(epochMillis: Long): String = formatter.format(Instant.ofEpochMilli(epochMillis))
}
