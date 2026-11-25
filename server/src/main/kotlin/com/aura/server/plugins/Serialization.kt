package com.aura.server.plugins

import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.application.install

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson()
    }
}
