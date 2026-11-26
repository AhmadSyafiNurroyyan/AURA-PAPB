package com.aura.server.auth

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import java.util.UUID

class JwtServiceTest {
    @Test
    fun `issues seven day token with tenant role and session id`() {
        val account = Akun(
            id = UUID.randomUUID(), idToko = "A1B2C3D4", email = "owner@example.test",
            nama = "Toko A", namaNormalisasi = "toko a", peran = Peran.OWNER,
            hash = "hash", salt = "salt", status = StatusAkun.AKTIF,
            wajibGantiSandi = false, nomorHp = null, fotoFileId = null,
            gagalLogin = 0, terkunciSampai = null, terakhirLogin = null,
        )
        val now = Instant.now()
        val service = JwtService("a".repeat(40))

        val session = service.issue(account, now)
        val decoded = service.verifier.verify(session.token)

        assertEquals(account.id.toString(), decoded.subject)
        assertEquals(account.idToko, decoded.getClaim("idToko").asString())
        assertEquals("OWNER", decoded.getClaim("peran").asString())
        assertEquals(session.jti.toString(), decoded.id)
        assertEquals(now.plusSeconds(7L * 24 * 60 * 60).toEpochMilli(), session.expiresAt)
    }
}
