package com.aura.app.ui.components

import com.aura.app.core.format.RupiahFormatter
import com.aura.app.core.format.TanggalFormatter

internal fun money(value: Long): String = RupiahFormatter.format(value)
internal fun date(value: Long): String = TanggalFormatter.formatTampilanSaatIni(value)
