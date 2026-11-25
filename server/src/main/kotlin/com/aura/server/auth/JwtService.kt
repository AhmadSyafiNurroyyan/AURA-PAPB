package com.aura.server.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.util.Date
import java.util.UUID

internal data class TokenSession(val token: String, val jti: UUID, val expiresAt: Long)

internal class JwtService(secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    fun issue(account: Akun, now: Instant = Instant.now()): TokenSession {
        val jti = UUID.randomUUID()
        val expiresAt = now.plusSeconds(SESSION_SECONDS)
        val token = JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withJWTId(jti.toString())
            .withSubject(account.id.toString())
            .withClaim("idToko", account.idToko)
            .withClaim("peran", account.peran.name)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiresAt))
            .sign(algorithm)
        return TokenSession(token, jti, expiresAt.toEpochMilli())
    }

    companion object {
        const val SESSION_SECONDS = 7L * 24 * 60 * 60
        private const val ISSUER = "aura-server"
        private const val AUDIENCE = "aura-app"
    }
}
