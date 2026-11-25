package com.aura.app.data.local.converter

import com.aura.app.core.model.AlasanPenyesuaian
import com.aura.app.core.model.JenisCatatanTerbatas
import com.aura.app.core.model.JenisPengeluaran
import com.aura.app.core.model.JenisPergerakan
import com.aura.app.core.model.MetodePembayaran
import com.aura.app.core.model.Peran
import com.aura.app.core.model.StatusAkun
import com.aura.app.core.model.StatusBarang
import com.aura.app.core.model.StatusCatatanKeuangan
import com.aura.app.core.model.StatusStok
import com.aura.app.core.model.StatusTransaksi
import com.aura.app.core.model.SumberPergerakan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun `konversi semua enum domain bolak-balik konsisten`() {
        Peran.entries.forEach { assertEquals(it, converters.toPeran(converters.fromPeran(it))) }
        StatusStok.entries.forEach { assertEquals(it, converters.toStatusStok(converters.fromStatusStok(it))) }
        JenisPergerakan.entries.forEach { assertEquals(it, converters.toJenisPergerakan(converters.fromJenisPergerakan(it))) }
        SumberPergerakan.entries.forEach { assertEquals(it, converters.toSumberPergerakan(converters.fromSumberPergerakan(it))) }
        AlasanPenyesuaian.entries.forEach { assertEquals(it, converters.toAlasanPenyesuaian(converters.fromAlasanPenyesuaian(it))) }
        assertNull(converters.toAlasanPenyesuaian(converters.fromAlasanPenyesuaian(null)))
        MetodePembayaran.entries.forEach { assertEquals(it, converters.toMetodePembayaran(converters.fromMetodePembayaran(it))) }
        StatusTransaksi.entries.forEach { assertEquals(it, converters.toStatusTransaksi(converters.fromStatusTransaksi(it))) }
        StatusBarang.entries.forEach { assertEquals(it, converters.toStatusBarang(converters.fromStatusBarang(it))) }
        StatusAkun.entries.forEach { assertEquals(it, converters.toStatusAkun(converters.fromStatusAkun(it))) }
        StatusCatatanKeuangan.entries.forEach { assertEquals(it, converters.toStatusCatatanKeuangan(converters.fromStatusCatatanKeuangan(it))) }
        JenisPengeluaran.entries.forEach { assertEquals(it, converters.toJenisPengeluaran(converters.fromJenisPengeluaran(it))) }
        JenisCatatanTerbatas.entries.forEach { assertEquals(it, converters.toJenisCatatanTerbatas(converters.fromJenisCatatanTerbatas(it))) }
    }
}
