package com.aura.server.db

import com.aura.server.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseFactory {
    fun initialize(config: AppConfig): HikariDataSource {
        val pool = HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = config.database.jdbcUrl
                username = config.database.username
                password = config.database.password
                driverClassName = "org.postgresql.Driver"
                poolName = "aura-postgresql"
                maximumPoolSize = 10
                minimumIdle = 1
                connectionTimeout = 30_000
            },
        )

        try {
            Flyway.configure()
                .dataSource(pool)
                .locations("classpath:db/migration")
                .load()
                .migrate()
            Database.connect(pool)
            return pool
        } catch (exception: Exception) {
            pool.close()
            throw IllegalStateException(
                "Gagal menghubungkan PostgreSQL atau menjalankan migrasi database.",
                exception,
            )
        }
    }
}
