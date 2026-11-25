package com.aura.server

import com.aura.server.config.AppConfig
import com.aura.server.db.DatabaseFactory
import com.aura.server.plugins.configureDefaultHeaders
import com.aura.server.plugins.configureLogging
import com.aura.server.plugins.configureSerialization
import com.aura.server.plugins.configureStatusPages
import com.aura.server.routes.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.Routing

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val config = AppConfig.fromEnvironment()
    val dataSource = DatabaseFactory.initialize(config)
    this.monitor.subscribe(ApplicationStopped) {
        dataSource.close()
    }
    configureHttp()
}

internal fun Application.configureHttp(
    additionalRoutes: Routing.() -> Unit = {},
) {
    configureSerialization()
    configureStatusPages()
    configureLogging()
    configureDefaultHeaders()
    configureRouting(additionalRoutes)
}
