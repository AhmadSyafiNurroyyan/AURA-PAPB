package com.aura.server.plugins

import com.google.gson.JsonParseException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

internal data class ErrorResponse(
    val kode: String,
    val pesan: String,
)

internal class ApiException(
    val status: HttpStatusCode,
    val kode: String,
    override val message: String,
) : RuntimeException(message)

internal fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respond(cause.status, ErrorResponse(cause.kode, cause.message))
        }
        exception<BadRequestException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("INPUT_TIDAK_VALID", "Format permintaan tidak valid."))
        }
        exception<JsonParseException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("INPUT_TIDAK_VALID", "Format JSON tidak valid."))
        }
        exception<Throwable> { call, cause ->
            // Avoid logging exception messages or stack traces that may contain secrets.
            call.application.log.error("Unhandled request failure (${cause::class.simpleName ?: "unknown"}).")
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("KESALAHAN_INTERNAL", "Terjadi kesalahan pada server."),
            )
        }
    }
}
