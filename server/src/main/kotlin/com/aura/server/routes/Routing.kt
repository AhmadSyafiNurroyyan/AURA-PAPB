package com.aura.server.routes

import com.aura.server.auth.AuthComponents
import com.aura.server.catalog.CatalogComponents
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

internal data class HealthResponse(val status: String)

internal fun Application.configureRouting(
    auth: AuthComponents? = null,
    catalog: CatalogComponents? = null,
    additionalRoutes: Routing.() -> Unit = {},
) {
    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok"))
        }
        auth?.let { authRoutes(it) }
        if (auth != null && catalog != null) catalogRoutes(auth, catalog)
        additionalRoutes()
    }
}
