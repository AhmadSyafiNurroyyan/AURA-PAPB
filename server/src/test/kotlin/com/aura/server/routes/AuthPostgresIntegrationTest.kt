package com.aura.server.routes

import com.aura.server.auth.authComponents
import com.aura.server.config.AppConfig
import com.aura.server.configureHttp
import com.aura.server.db.DatabaseFactory
import com.aura.server.db.TokoTable
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.Assume.assumeFalse
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Optional PostgreSQL + Flyway integration smoke test (K-26).
 * Skipped cleanly via JUnit Assume when DATABASE_URL is not configured.
 */
class AuthPostgresIntegrationTest {
    private val gson = Gson()

    @Test
    fun `flyway migration V2 and ExposedAuthRepository persist owner and cashier flow`() {
        val dbUrl = System.getenv("DATABASE_URL")
        assumeFalse("DATABASE_URL not set; skipping PostgreSQL integration test", dbUrl.isNullOrBlank())

        val secret = System.getenv("JWT_SECRET") ?: "postgres-integration-test-secret-32"
        val config = AppConfig.fromEnvironment(
            mapOf(
                "DATABASE_URL" to dbUrl,
                "JWT_SECRET" to secret,
                "UPLOAD_DIR" to "./var/test-uploads",
            ),
        )
        val dataSource = DatabaseFactory.initialize(config)
        val createdStoreIds = mutableListOf<String>()
        try {
            testApplication {
                application { configureHttp(authComponents(secret)) }

                val email = "pg-${UUID.randomUUID().toString().take(8)}@example.test"
                val regResp = client.post("/api/v1/auth/owner/register") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"email":"$email","kataSandi":"TestSandi123","namaToko":"Toko PG","saldoAwal":50000}""")
                }
                assertEquals(HttpStatusCode.Created, regResp.status)
                val regBody = gson.fromJson(regResp.bodyAsText(), JsonObject::class.java)
                val token = regBody.get("token").asString
                val idToko = regBody.getAsJsonObject("toko").get("idToko").asString
                createdStoreIds += idToko

                val kasirResp = client.post("/api/v1/kasir") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"id":"${UUID.randomUUID()}","namaAkun":"kasirPg","kataSandiAwal":"SandiKasir123"}""")
                }
                assertEquals(HttpStatusCode.Created, kasirResp.status)

                val meResp = client.get("/api/v1/auth/me") {
                    header("Authorization", "Bearer $token")
                }
                assertEquals(HttpStatusCode.OK, meResp.status)
                assertTrue(meResp.bodyAsText().contains(idToko))
            }
        } finally {
            transaction {
                createdStoreIds.forEach { storeId ->
                    TokoTable.deleteWhere { idToko eq storeId }
                }
            }
            dataSource.close()
        }
    }
}
