package com.aura.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.request.path
import org.slf4j.event.Level

internal fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}

internal fun Application.configureDefaultHeaders() {
    install(DefaultHeaders) {
        header("X-Content-Type-Options", "nosniff")
    }
}
