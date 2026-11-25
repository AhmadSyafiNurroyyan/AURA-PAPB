package com.aura.server.plugins

import com.aura.server.auth.AuthComponents
import com.aura.server.auth.AkunPrincipal
import com.aura.server.auth.Peran
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

internal fun Application.configureAuthentication(auth: AuthComponents) {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(auth.jwt.verifier)
            validate { credential ->
                val accountId = credential.payload.subject?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                val sessionId = credential.payload.id?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                val storeId = credential.payload.getClaim("idToko").asString()
                val role = credential.payload.getClaim("peran").asString()
                val parsedRole = role?.let { runCatching { Peran.valueOf(it) }.getOrNull() }
                if (accountId == null || sessionId == null || storeId.isNullOrBlank() || parsedRole == null) return@validate null
                val active = withContext(Dispatchers.IO) {
                    auth.repository.isSessionActive(sessionId, accountId, storeId, parsedRole.name)
                }
                if (active) AkunPrincipal(accountId, storeId, parsedRole, sessionId) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("SESI_TIDAK_VALID", "Sesi tidak valid atau telah berakhir."))
            }
        }
    }
}
