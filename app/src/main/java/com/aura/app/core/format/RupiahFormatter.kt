package com.aura.app.core.format

import java.text.NumberFormat
import java.util.Locale

object RupiahFormatter {
    private val localeIndonesia = Locale.forLanguageTag("id-ID")

    fun format(nominal: Long): String {
        val formatter = NumberFormat.getIntegerInstance(localeIndonesia).apply {
            isGroupingUsed = true
            maximumFractionDigits = 0
        }
        val nilaiTerformat = formatter.format(nominal)
        return if (nominal < 0) {
            "-Rp ${nilaiTerformat.removePrefix("-")}"
        } else {
            "Rp $nilaiTerformat"
        }
    }
}
