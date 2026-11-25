package com.aura.app.data.local.converter

import androidx.room.TypeConverter
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

/**
 * Konverter Room untuk menyimpan enum domain sebagai String sesuai konvensi spec/14.
 */
class Converters {
    @TypeConverter fun fromPeran(value: Peran): String = value.name
    @TypeConverter fun toPeran(value: String): Peran = Peran.valueOf(value)

    @TypeConverter fun fromStatusStok(value: StatusStok): String = value.name
    @TypeConverter fun toStatusStok(value: String): StatusStok = StatusStok.valueOf(value)

    @TypeConverter fun fromJenisPergerakan(value: JenisPergerakan): String = value.name
    @TypeConverter fun toJenisPergerakan(value: String): JenisPergerakan = JenisPergerakan.valueOf(value)

    @TypeConverter fun fromSumberPergerakan(value: SumberPergerakan): String = value.name
    @TypeConverter fun toSumberPergerakan(value: String): SumberPergerakan = SumberPergerakan.valueOf(value)

    @TypeConverter fun fromAlasanPenyesuaian(value: AlasanPenyesuaian?): String? = value?.name
    @TypeConverter fun toAlasanPenyesuaian(value: String?): AlasanPenyesuaian? = value?.let(AlasanPenyesuaian::valueOf)

    @TypeConverter fun fromMetodePembayaran(value: MetodePembayaran): String = value.name
    @TypeConverter fun toMetodePembayaran(value: String): MetodePembayaran = MetodePembayaran.valueOf(value)

    @TypeConverter fun fromStatusTransaksi(value: StatusTransaksi): String = value.name
    @TypeConverter fun toStatusTransaksi(value: String): StatusTransaksi = StatusTransaksi.valueOf(value)

    @TypeConverter fun fromStatusBarang(value: StatusBarang): String = value.name
    @TypeConverter fun toStatusBarang(value: String): StatusBarang = StatusBarang.valueOf(value)

    @TypeConverter fun fromStatusAkun(value: StatusAkun): String = value.name
    @TypeConverter fun toStatusAkun(value: String): StatusAkun = StatusAkun.valueOf(value)

    @TypeConverter fun fromStatusCatatanKeuangan(value: StatusCatatanKeuangan): String = value.name
    @TypeConverter fun toStatusCatatanKeuangan(value: String): StatusCatatanKeuangan = StatusCatatanKeuangan.valueOf(value)

    @TypeConverter fun fromJenisPengeluaran(value: JenisPengeluaran): String = value.name
    @TypeConverter fun toJenisPengeluaran(value: String): JenisPengeluaran = JenisPengeluaran.valueOf(value)

    @TypeConverter fun fromJenisCatatanTerbatas(value: JenisCatatanTerbatas): String = value.name
    @TypeConverter fun toJenisCatatanTerbatas(value: String): JenisCatatanTerbatas = JenisCatatanTerbatas.valueOf(value)
}
