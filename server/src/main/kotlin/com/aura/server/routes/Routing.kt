package com.aura.server.routes

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

internal data class HealthResponse(val status: String)

internal fun Application.configureRouting(
    additionalRoutes: Routing.() -> Unit = {},
) {
    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok"))
        }
        additionalRoutes()
    }
}
