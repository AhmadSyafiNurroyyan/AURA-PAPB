package com.aura.app.core.format

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

object TanggalFormatter {
    private val zonaWib = ZoneId.of("Asia/Jakarta")
    private val formatter = DateTimeFormatter
        .ofPattern("d MMM uuuu, HH.mm 'WIB'", Locale.forLanguageTag("id-ID"))
        .withZone(zonaWib)

    fun format(epochMillis: Long): String = formatter.format(Instant.ofEpochMilli(epochMillis))

    /** Format tampilan lama AuraApp agar pemindahan Screen tidak mengubah teks yang terlihat. */
    fun formatTampilanSaatIni(epochMillis: Long): String =
        SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id", "ID")).format(Date(epochMillis))
}
