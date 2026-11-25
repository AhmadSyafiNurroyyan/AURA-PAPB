package com.aura.app.data.remote.dto

import com.aura.app.core.model.Peran
import com.aura.app.core.model.StatusAkun
import com.aura.app.data.local.entity.TokoEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthMappersTest {

    @Test
    fun `AkunDto toEntity memetakan seluruh field dengan istilah baku`() {
        val dto = AkunDto(
            id = "akun-1",
            idToko = "ABCD1234",
            email = "owner@toko.id",
            namaAkun = "Toko Berkah",
            peran = "OWNER",
            status = "AKTIF",
            wajibGantiSandi = false,
            nomorHp = "08123456789",
            fotoFileId = "file-1",
            terakhirLogin = 1700000000000L,
        )

        val entity = dto.toEntity()

        assertEquals("akun-1", entity.id)
        assertEquals("ABCD1234", entity.idToko)
        assertEquals("owner@toko.id", entity.email)
        assertEquals("Toko Berkah", entity.namaAkun)
        assertEquals(Peran.OWNER, entity.peran)
        assertEquals(StatusAkun.AKTIF, entity.status)
        assertFalse(entity.wajibGantiSandi)
        assertEquals("08123456789", entity.nomorHp)
        assertEquals("file-1", entity.fotoFileId)
        assertEquals(1700000000000L, entity.terakhirLogin)
    }

    @Test
    fun `KasirListItemDto toEntity memetakan peran KASIR dan status`() {
        val item = KasirListItemDto(
            id = "kasir-1",
            namaAkun = "budi",
            status = "NONAKTIF",
            terakhirLogin = null,
        )

        val entity = item.toEntity("TOKO0001")

        assertEquals("kasir-1", entity.id)
        assertEquals("TOKO0001", entity.idToko)
        assertNull(entity.email)
        assertEquals("budi", entity.namaAkun)
        assertEquals(Peran.KASIR, entity.peran)
        assertEquals(StatusAkun.NONAKTIF, entity.status)
        assertNull(entity.terakhirLogin)
    }

    @Test
    fun `TokoDto toEntity mempertahankan field metode pembayaran dari cache yang sudah ada`() {
        val existing = TokoEntity(
            idToko = "TOKO0001",
            namaToko = "Lama",
            saldoAwal = 50_000L,
            saldoKumulatif = 75_000L,
            namaPenerima = "Budi Owner",
            infoRekening = "BCA 123456",
            qrisFileId = "qris-99",
        )
        val dto = TokoDto(
            idToko = "TOKO0001",
            namaToko = "Toko Baru",
            jenisUsaha = "Sembako",
            alamat = "Jl. Merdeka",
            logoFileId = "logo-1",
            saldoAwal = 100_000L,
            saldoKumulatif = 125_000L,
        )

        val entity = dto.toEntity(existing)

        assertEquals("Toko Baru", entity.namaToko)
        assertEquals("Sembako", entity.jenisUsaha)
        assertEquals(100_000L, entity.saldoAwal)
        assertEquals(125_000L, entity.saldoKumulatif)
        assertEquals("Budi Owner", entity.namaPenerima)
        assertEquals("BCA 123456", entity.infoRekening)
        assertEquals("qris-99", entity.qrisFileId)
    }

    @Test
    fun `SessionResponseDto toSesiAktif menggabungkan flag wajibGantiSandi`() {
        val response = SessionResponseDto(
            akun = AkunDto(
                id = "kasir-2",
                idToko = "TOKO0001",
                namaAkun = "sari",
                peran = "KASIR",
                status = "AKTIF",
                wajibGantiSandi = false,
            ),
            toko = TokoDto(
                idToko = "TOKO0001",
                namaToko = "Toko AURA",
                saldoAwal = 0L,
                saldoKumulatif = 0L,
            ),
            token = "jwt-token-xyz",
            expiresAt = 1800000000000L,
            wajibGantiSandi = true,
        )

        val sesi = response.toSesiAktif()

        assertEquals("jwt-token-xyz", sesi.token)
        assertEquals(1800000000000L, sesi.expiresAt)
        assertEquals("kasir-2", sesi.akunId)
        assertEquals("TOKO0001", sesi.idToko)
        assertEquals(Peran.KASIR, sesi.peran)
        assertEquals("sari", sesi.nama)
        assertTrue(sesi.wajibGantiSandi)
    }

    @Test
    fun `AkunEntity toRiwayatAkun menghasilkan ringkasan akun tanpa sandi`() {
        val entity = AkunDto(
            id = "akun-3",
            idToko = "TOKO0001",
            email = "owner@aura.id",
            namaAkun = "Owner",
            peran = "OWNER",
            status = "AKTIF",
            terakhirLogin = 1700000000123L,
        ).toEntity()

        val riwayat = entity.toRiwayatAkun()

        assertEquals("akun-3", riwayat.akunId)
        assertEquals("TOKO0001", riwayat.idToko)
        assertEquals("owner@aura.id", riwayat.email)
        assertEquals(Peran.OWNER, riwayat.peran)
        assertEquals(1700000000123L, riwayat.terakhirLogin)
    }
}
