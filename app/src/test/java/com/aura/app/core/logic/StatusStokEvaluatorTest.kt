package com.aura.app.core.logic

import com.aura.app.core.model.StatusStok
import org.junit.Assert.assertEquals
import org.junit.Test

class StatusStokEvaluatorTest {
    @Test
    fun arsipMengalahkanStatusStokLain() {
        assertEquals(StatusStok.DIARSIPKAN, StatusStokEvaluator.evaluate(0, 0, diarsipkan = true))
        assertEquals(StatusStok.DIARSIPKAN, StatusStokEvaluator.evaluate(20, 5, diarsipkan = true))
    }

    @Test
    fun stokNolSelaluHabisWalauMinimumNol() {
        assertEquals(StatusStok.HABIS, StatusStokEvaluator.evaluate(0, 0, diarsipkan = false))
    }

    @Test
    fun stokNegatifDianggapHabis() {
        assertEquals(StatusStok.HABIS, StatusStokEvaluator.evaluate(-1, 0, diarsipkan = false))
    }

    @Test
    fun batasTepatMinimumBerstatusMenipis() {
        assertEquals(StatusStok.MENIPIS, StatusStokEvaluator.evaluate(5, 5, diarsipkan = false))
    }

    @Test
    fun minimumNolMembuatStokPositifAman() {
        assertEquals(StatusStok.AMAN, StatusStokEvaluator.evaluate(1, 0, diarsipkan = false))
    }

    @Test
    fun stokDiAtasMinimumAman() {
        assertEquals(StatusStok.AMAN, StatusStokEvaluator.evaluate(6, 5, diarsipkan = false))
    }
}
