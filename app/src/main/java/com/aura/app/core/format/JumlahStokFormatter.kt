package com.aura.app.core.format

import java.text.NumberFormat
import java.util.Locale

object JumlahStokFormatter {
    fun format(jumlah: Int, satuan: String): String {
        val formatter = NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID")).apply {
            isGroupingUsed = true
            maximumFractionDigits = 0
        }
        return "${formatter.format(jumlah)} $satuan"
    }
}
