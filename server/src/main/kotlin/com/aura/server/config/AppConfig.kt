package com.aura.server.config

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class AppConfig private constructor(
    val port: Int,
    val database: DatabaseConnection,
    val jwtSecret: String,
    val uploadDirectory: Path,
) {
    companion object {
        fun fromEnvironment(environment: Map<String, String> = System.getenv()): AppConfig {
            val rawPort = environment["PORT"]?.takeIf(String::isNotBlank)
            val port = rawPort?.toIntOrNull() ?: if (rawPort == null) 8080 else {
                error("PORT harus berupa angka antara 1 dan 65535.")
            }
            require(port in 1..65535) { "PORT harus berupa angka antara 1 dan 65535." }

            val databaseUrl = environment["DATABASE_URL"]
                ?.takeIf(String::isNotBlank)
                ?: error("Environment DATABASE_URL wajib diisi.")
            val jwtSecret = environment["JWT_SECRET"]
                ?.takeIf(String::isNotBlank)
                ?: error("Environment JWT_SECRET wajib diisi.")
            require(jwtSecret.length >= MIN_JWT_SECRET_LENGTH) {
                "JWT_SECRET harus memiliki minimal $MIN_JWT_SECRET_LENGTH karakter."
            }

            val uploadDirectory = environment["UPLOAD_DIR"]
                ?.takeIf(String::isNotBlank)
                ?: "./var/uploads"

            return AppConfig(
                port = port,
                database = parseDatabaseUrl(databaseUrl),
                jwtSecret = jwtSecret,
                uploadDirectory = Path.of(uploadDirectory),
            )
        }

        internal fun parseDatabaseUrl(value: String): DatabaseConnection {
            require(value.isNotBlank()) { "DATABASE_URL wajib diisi." }
            return when {
                value.startsWith("postgresql://") || value.startsWith("postgres://") ->
                    parseRailwayUrl(parseUri(value))

                value.startsWith("jdbc:postgresql://") -> parseJdbcUrl(value)
                else -> error("DATABASE_URL harus memakai skema postgresql:// atau jdbc:postgresql://.")
            }
        }

        private fun parseRailwayUrl(uri: URI): DatabaseConnection {
            require(uri.host != null && uri.rawPath.orEmpty().length > 1) {
                "DATABASE_URL PostgreSQL harus memuat host dan nama database."
            }
            val rawUserInfo = uri.rawUserInfo
                ?: error("DATABASE_URL PostgreSQL harus memuat user dan kata sandi.")
            val separator = rawUserInfo.indexOf(':')
            require(separator > 0 && separator < rawUserInfo.lastIndex) {
                "DATABASE_URL PostgreSQL harus memuat user dan kata sandi."
            }

            val authority = uri.rawAuthority.substringAfterLast('@')
            val jdbcUrl = buildString {
                append("jdbc:postgresql://")
                append(authority)
                append(uri.rawPath)
                uri.rawQuery?.let { append('?').append(it) }
            }
            return DatabaseConnection(
                jdbcUrl = jdbcUrl,
                username = decode(rawUserInfo.substring(0, separator)),
                password = decode(rawUserInfo.substring(separator + 1)),
            )
        }

        private fun parseJdbcUrl(value: String): DatabaseConnection {
            val uri = parseUri(value.removePrefix("jdbc:"))
            require(uri.host != null && uri.rawPath.orEmpty().length > 1) {
                "DATABASE_URL JDBC harus memuat host dan nama database."
            }

            val rawUserInfo = uri.rawUserInfo
            val queryValues = uri.rawQuery.orEmpty()
                .split('&')
                .filter(String::isNotEmpty)
                .mapNotNull { parameter ->
                    val separator = parameter.indexOf('=')
                    if (separator < 0) null else {
                        decode(parameter.substring(0, separator)) to parameter.substring(separator + 1)
                    }
                }
                .toMap()
            val userInfoParts = rawUserInfo?.split(':', limit = 2)
            val username = queryValues["user"]?.let(::decode)
                ?: userInfoParts?.getOrNull(0)?.let(::decode)
            val password = queryValues["password"]?.let(::decode)
                ?: userInfoParts?.getOrNull(1)?.let(::decode)
            require(!username.isNullOrBlank() && !password.isNullOrBlank()) {
                "DATABASE_URL JDBC harus menyertakan user dan kata sandi di query atau user-info."
            }

            val safeQuery = uri.rawQuery.orEmpty()
                .split('&')
                .filter(String::isNotEmpty)
                .filterNot { parameter ->
                    val key = parameter.substringBefore('=').let(::decode)
                    key.equals("user", ignoreCase = true) || key.equals("password", ignoreCase = true)
                }
                .joinToString("&")
            val authority = uri.rawAuthority.substringAfterLast('@')
            val jdbcUrl = buildString {
                append("jdbc:postgresql://")
                append(authority)
                append(uri.rawPath)
                if (safeQuery.isNotEmpty()) append('?').append(safeQuery)
            }
            return DatabaseConnection(jdbcUrl, username, password)
        }

        private fun parseUri(value: String): URI = try {
            URI(value)
        } catch (_: Exception) {
            error("DATABASE_URL tidak valid.")
        }

        private fun decode(value: String): String = URLDecoder.decode(
            value.replace("+", "%2B"),
            StandardCharsets.UTF_8,
        )

        private const val MIN_JWT_SECRET_LENGTH = 32
    }
}

class DatabaseConnection internal constructor(
    val jdbcUrl: String,
    val username: String,
    val password: String,
)
