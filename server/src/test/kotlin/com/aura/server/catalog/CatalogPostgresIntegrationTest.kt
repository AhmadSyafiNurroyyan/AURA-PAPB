package com.aura.server.catalog

import com.aura.server.auth.authComponents
import com.aura.server.config.AppConfig
import com.aura.server.configureHttp
import com.aura.server.db.DatabaseFactory
import com.aura.server.db.TokoTable
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
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

class CatalogPostgresIntegrationTest {
    private val gson = Gson()

    @Test
    fun `catalog operations persist stock history and enforce deletion rules`() {
        val databaseUrl = System.getenv("DATABASE_URL")
        assumeFalse("DATABASE_URL not set; skipping PostgreSQL integration test", databaseUrl.isNullOrBlank())

        val secret = System.getenv("JWT_SECRET") ?: "catalog-integration-test-secret-32"
        val config = AppConfig.fromEnvironment(
            mapOf(
                "DATABASE_URL" to databaseUrl,
                "JWT_SECRET" to secret,
                "UPLOAD_DIR" to "./var/test-uploads",
            ),
        )
        val dataSource = DatabaseFactory.initialize(config)
        val storeIds = mutableListOf<String>()
        try {
            testApplication {
                application {
                    configureHttp(
                        auth = authComponents(secret),
                        catalog = catalogComponents(config.uploadDirectory),
                    )
                }

                val ownerResponse = client.post("/api/v1/auth/owner/register") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"email":"catalog-${UUID.randomUUID()}@example.test","kataSandi":"SandiOwner123","namaToko":"Toko Katalog","saldoAwal":0}""")
                }
                assertEquals(HttpStatusCode.Created, ownerResponse.status)
                val owner = gson.fromJson(ownerResponse.bodyAsText(), JsonObject::class.java)
                val token = owner.get("token").asString
                storeIds += owner.getAsJsonObject("toko").get("idToko").asString

                val categoryId = UUID.randomUUID()
                val categoryResponse = client.post("/api/v1/kategori") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"id":"$categoryId","nama":"Minuman"}""")
                }
                assertEquals(HttpStatusCode.Created, categoryResponse.status)

                val supplierId = UUID.randomUUID()
                val supplierResponse = client.post("/api/v1/supplier") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"id":"$supplierId","nama":"Pemasok A","link":"https://supplier.example.test"}""")
                }
                assertEquals(HttpStatusCode.Created, supplierResponse.status)

                val barangId = UUID.randomUUID()
                val createResponse = client.post("/api/v1/barang") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(
                        """{"id":"$barangId","nama":"Air Mineral","satuan":"botol","hargaJual":5000,"stokMinimum":2,"stokAwal":5,"kategoriIds":["$categoryId"],"hargaBeliAwal":3000,"supplierId":"$supplierId"}""",
                    )
                }
                assertEquals(HttpStatusCode.Created, createResponse.status, createResponse.bodyAsText())
                val created = gson.fromJson(createResponse.bodyAsText(), JsonObject::class.java)
                assertEquals(5, created.getAsJsonObject("barang").get("stokSaatIni").asInt)
                assertEquals(3000, created.getAsJsonObject("barang").get("hppRataRata").asInt)
                assertEquals("STOK_AWAL", created.getAsJsonObject("pergerakan").get("sumber").asString)

                val detailResponse = client.get("/api/v1/barang/$barangId") {
                    header("Authorization", "Bearer $token")
                }
                assertEquals(HttpStatusCode.OK, detailResponse.status)
                val detail = gson.fromJson(detailResponse.bodyAsText(), JsonObject::class.java)
                assertEquals(1, detail.getAsJsonArray("hargaBeliSupplier").size())
                assertEquals(1, detail.getAsJsonObject("kartuStokRingkas").get("jumlahPergerakan").asInt)

                val otherOwnerResponse = client.post("/api/v1/auth/owner/register") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"email":"other-${UUID.randomUUID()}@example.test","kataSandi":"SandiOwner123","namaToko":"Toko Lain","saldoAwal":0}""")
                }
                assertEquals(HttpStatusCode.Created, otherOwnerResponse.status)
                val otherOwner = gson.fromJson(otherOwnerResponse.bodyAsText(), JsonObject::class.java)
                val otherToken = otherOwner.get("token").asString
                storeIds += otherOwner.getAsJsonObject("toko").get("idToko").asString
                val foreignBarang = client.get("/api/v1/barang/$barangId") {
                    header("Authorization", "Bearer $otherToken")
                }
                assertEquals(HttpStatusCode.NotFound, foreignBarang.status)

                val editResponse = client.put("/api/v1/barang/$barangId") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"hargaJual":6000}""")
                }
                assertEquals(HttpStatusCode.OK, editResponse.status)
                val edited = gson.fromJson(editResponse.bodyAsText(), JsonObject::class.java).getAsJsonObject("barang")
                assertEquals(5, edited.get("stokSaatIni").asInt)
                assertEquals(3000, edited.get("hppRataRata").asInt)

                val deleteUsed = client.delete("/api/v1/barang/$barangId") {
                    header("Authorization", "Bearer $token")
                }
                assertEquals(HttpStatusCode.OK, deleteUsed.status)
                assertEquals("DIARSIPKAN", gson.fromJson(deleteUsed.bodyAsText(), JsonObject::class.java).get("status").asString)

                val archivedList = client.get("/api/v1/barang?status=DIARSIPKAN") {
                    header("Authorization", "Bearer $token")
                }
                assertEquals(HttpStatusCode.OK, archivedList.status)
                assertEquals(1, gson.fromJson(archivedList.bodyAsText(), JsonObject::class.java).getAsJsonArray("data").size())

                val unusedId = UUID.randomUUID()
                val unused = client.post("/api/v1/barang") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"id":"$unusedId","nama":"Barang Baru","satuan":"pcs","hargaJual":1000,"stokMinimum":0,"stokAwal":0}""")
                }
                assertEquals(HttpStatusCode.Created, unused.status)
                val deleteUnused = client.delete("/api/v1/barang/$unusedId") {
                    header("Authorization", "Bearer $token")
                }
                assertEquals("DIHAPUS_PERMANEN", gson.fromJson(deleteUnused.bodyAsText(), JsonObject::class.java).get("status").asString)

                val deleteUsedSupplier = client.delete("/api/v1/supplier/$supplierId") {
                    header("Authorization", "Bearer $token")
                }
                assertEquals(HttpStatusCode.Conflict, deleteUsedSupplier.status)

                val deleteCategory = client.delete("/api/v1/kategori/$categoryId") {
                    header("Authorization", "Bearer $token")
                }
                assertEquals(HttpStatusCode.NoContent, deleteCategory.status)
                val detailAfterCategoryDelete = client.get("/api/v1/barang/$barangId") {
                    header("Authorization", "Bearer $token")
                }
                val categories = gson.fromJson(detailAfterCategoryDelete.bodyAsText(), JsonObject::class.java)
                    .getAsJsonObject("barang").getAsJsonArray("kategori")
                assertEquals(0, categories.size())
            }
        } finally {
            transaction {
                storeIds.forEach { storeId -> TokoTable.deleteWhere { idToko eq storeId } }
            }
            dataSource.close()
        }
    }
}
