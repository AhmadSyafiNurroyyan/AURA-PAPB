package com.aura.app.core.logic

import com.aura.app.core.model.StatusStok

object StatusStokEvaluator {
    fun evaluate(stokSaatIni: Int, stokMinimum: Int, diarsipkan: Boolean): StatusStok = when {
        diarsipkan -> StatusStok.DIARSIPKAN
        stokSaatIni <= 0 -> StatusStok.HABIS
        stokSaatIni <= stokMinimum -> StatusStok.MENIPIS
        else -> StatusStok.AMAN
    }
}
