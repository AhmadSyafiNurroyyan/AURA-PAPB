package com.aura.server.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AppConfigTest {
    @Test
    fun `parses Railway PostgreSQL URL and decodes credentials`() {
        val connection = AppConfig.parseDatabaseUrl(
            "postgresql://aura_user:p%40ss+word@db.railway.internal:5432/railway?sslmode=require",
        )

        assertEquals(
            "jdbc:postgresql://db.railway.internal:5432/railway?sslmode=require",
            connection.jdbcUrl,
        )
        assertEquals("aura_user", connection.username)
        assertEquals("p@ss+word", connection.password)
    }

    @Test
    fun `parses JDBC URL and removes credentials from JDBC address`() {
        val connection = AppConfig.parseDatabaseUrl(
            "jdbc:postgresql://localhost:5432/aura_dev?user=postgres&password=p%40ss&sslmode=disable",
        )

        assertEquals("jdbc:postgresql://localhost:5432/aura_dev?sslmode=disable", connection.jdbcUrl)
        assertEquals("postgres", connection.username)
        assertEquals("p@ss", connection.password)
    }

    @Test
    fun `rejects missing credentials without echoing URL`() {
        val exception = assertFailsWith<IllegalStateException> {
            AppConfig.parseDatabaseUrl("postgresql://localhost:5432/aura_dev")
        }

        kotlin.test.assertFalse(exception.message.orEmpty().contains("localhost"))
    }

    @Test
    fun `rejects malformed database URL without echoing its value`() {
        val exception = assertFailsWith<IllegalStateException> {
            AppConfig.parseDatabaseUrl("postgresql://user:secret with spaces@localhost/aura")
        }

        kotlin.test.assertFalse(exception.message.orEmpty().contains("secret"))
    }

    @Test
    fun `rejects a nonnumeric port instead of silently using the default`() {
        val environment = mapOf(
            "PORT" to "not-a-port",
            "DATABASE_URL" to "postgresql://user:password@localhost:5432/aura_dev",
            "JWT_SECRET" to "01234567890123456789012345678901",
        )

        assertFailsWith<IllegalStateException> {
            AppConfig.fromEnvironment(environment)
        }
    }

    @Test
    fun `requires secure JWT secret`() {
        val environment = mapOf(
            "DATABASE_URL" to "postgresql://user:password@localhost:5432/aura_dev",
            "JWT_SECRET" to "too-short",
        )

        assertFailsWith<IllegalArgumentException> {
            AppConfig.fromEnvironment(environment)
        }
    }
}
