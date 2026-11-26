package com.aura.server.routes

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.aura.server.auth.AuthComponents
import com.aura.server.auth.AuthRepository
import com.aura.server.auth.InMemoryAuthRepository
import com.aura.server.auth.authComponents
import com.aura.server.configureHttp
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthRoutesTest {

    private val gson = Gson()
    private val testSecret = "test-secret-local-at-least-32-chars-long"

    private fun withAuthApp(
        repository: InMemoryAuthRepository = InMemoryAuthRepository(),
        block: suspend ApplicationTestBuilder.(auth: AuthComponents, repo: InMemoryAuthRepository) -> Unit,
    ) = testApplication {
        val auth = authComponents(testSecret, repository)
        application { configureHttp(auth) }
        block(auth, repository)
    }

    private suspend fun HttpClient.registerOwner(
        email: String = "owner@toko.test",
        password: String = "TestSandi123",
        storeName: String = "Toko AURA",
        openingBalance: Long = 100_000L,
    ): JsonObject {
        val response = post("/api/v1/auth/owner/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","kataSandi":"$password","namaToko":"$storeName","saldoAwal":$openingBalance}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        return gson.fromJson(response.bodyAsText(), JsonObject::class.java)
    }

    private suspend fun HttpClient.loginOwner(email: String, password: String = "TestSandi123"): JsonObject {
        val response = post("/api/v1/auth/owner/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","kataSandi":"$password"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        return gson.fromJson(response.bodyAsText(), JsonObject::class.java)
    }

    private suspend fun HttpClient.createCashier(
        ownerToken: String,
        name: String = "kasir1",
        password: String = "SandiKasir123",
    ): JsonObject {
        val response = post("/api/v1/kasir") {
            header("Authorization", "Bearer $ownerToken")
            contentType(ContentType.Application.Json)
            setBody("""{"id":"${UUID.randomUUID()}","namaAkun":"$name","kataSandiAwal":"$password"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        return gson.fromJson(response.bodyAsText(), JsonObject::class.java)
    }

    private suspend fun HttpClient.loginCashierReady(
        idToko: String,
        name: String = "kasir1",
        initialPassword: String = "SandiKasir123",
        newPassword: String = "SandiKasirBaru123",
    ): String {
        val firstLogin = post("/api/v1/auth/kasir/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"idToko":"$idToko","namaAkun":"$name","kataSandi":"$initialPassword"}""")
        }
        assertEquals(HttpStatusCode.OK, firstLogin.status)
        val token = gson.fromJson(firstLogin.bodyAsText(), JsonObject::class.java).get("token").asString

        val changeResp = post("/api/v1/auth/change-password") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody("""{"kataSandiSaatIni":"$initialPassword","kataSandiBaru":"$newPassword"}""")
        }
        assertEquals(HttpStatusCode.NoContent, changeResp.status)
        return token
    }

    // ============================
    // Registration & Validation
    // ============================

    @Test
    fun `owner registration creates store and owner token`() = withAuthApp { _, repo ->
        val body = client.registerOwner("Owner@Toko.test")

        assertTrue(body.has("token"))
        val akun = body.getAsJsonObject("akun")
        assertEquals("OWNER", akun.get("peran").asString)
        assertEquals("owner@toko.test", akun.get("email").asString)
        assertTrue(akun.get("terakhirLogin").asLong > 0L)

        val toko = body.getAsJsonObject("toko")
        assertEquals(100_000L, toko.get("saldoAwal").asLong)
        assertEquals(100_000L, toko.get("saldoKumulatif").asLong)
        assertTrue(toko.get("idToko").asString.matches(Regex("[A-Z0-9]{8}")))
        assertTrue(repo.auditLogs.any { it.kategori == "AKUN" && it.aksi == "OWNER_DIBUAT" })
        assertTrue(repo.auditLogs.any { it.kategori == "LOGIN" && it.aksi == "LOGIN_BERHASIL" })
    }

    @Test
    fun `owner registration rejects duplicate email case-insensitively with 409`() = withAuthApp { _, _ ->
        client.registerOwner("owner@toko.test")

        val dup = client.post("/api/v1/auth/owner/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"OWNER@toko.test","kataSandi":"TestSandi123","namaToko":"Toko B","saldoAwal":0}""")
        }
        assertEquals(HttpStatusCode.Conflict, dup.status)
        val err = gson.fromJson(dup.bodyAsText(), JsonObject::class.java)
        assertEquals("EMAIL_SUDAH_DIPAKAI", err.get("kode").asString)
    }

    @Test
    fun `owner registration rejects short password and negative balance and malformed json with 400`() = withAuthApp { _, _ ->
        val shortPass = client.post("/api/v1/auth/owner/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"a@b.test","kataSandi":"1234567","namaToko":"Toko","saldoAwal":0}""")
        }
        assertEquals(HttpStatusCode.BadRequest, shortPass.status)

        val negativeBalance = client.post("/api/v1/auth/owner/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"a@b.test","kataSandi":"TestSandi123","namaToko":"Toko","saldoAwal":-100}""")
        }
        assertEquals(HttpStatusCode.BadRequest, negativeBalance.status)

        val badJson = client.post("/api/v1/auth/owner/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"a@b.test","saldoAwal":"bukan-angka"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, badJson.status)
        assertTrue(badJson.bodyAsText().contains("INPUT_TIDAK_VALID"))
    }

    // ============================
    // Login & Lockout
    // ============================

    @Test
    fun `owner login succeeds and wrong credentials return 401 with identical generic message`() = withAuthApp { _, _ ->
        client.registerOwner("owner@toko.test")

        val ok = client.loginOwner("owner@toko.test")
        assertTrue(ok.has("token"))

        val wrongPass = client.post("/api/v1/auth/owner/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"owner@toko.test","kataSandi":"salah1234"}""")
        }
        val wrongEmail = client.post("/api/v1/auth/owner/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"tidakada@toko.test","kataSandi":"salah1234"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, wrongPass.status)
        assertEquals(HttpStatusCode.Unauthorized, wrongEmail.status)

        val err1 = gson.fromJson(wrongPass.bodyAsText(), JsonObject::class.java)
        val err2 = gson.fromJson(wrongEmail.bodyAsText(), JsonObject::class.java)
        assertEquals("KREDENSIAL_TIDAK_VALID", err1.get("kode").asString)
        assertEquals(err1.get("pesan").asString, err2.get("pesan").asString)
        assertFalse(err1.get("pesan").asString.contains("owner@toko.test"))
    }

    @Test
    fun `five failed logins lock account for 5 minutes`() = withAuthApp { _, _ ->
        client.registerOwner("owner@toko.test")

        repeat(AuthRepository.MAX_FAILED_ATTEMPTS) {
            val resp = client.post("/api/v1/auth/owner/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"owner@toko.test","kataSandi":"salah1234"}""")
            }
            assertEquals(HttpStatusCode.Unauthorized, resp.status)
        }

        // Even with the right password, login is locked now
        val lockedResp = client.post("/api/v1/auth/owner/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"owner@toko.test","kataSandi":"TestSandi123"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, lockedResp.status)
        val err = gson.fromJson(lockedResp.bodyAsText(), JsonObject::class.java)
        assertEquals("LOGIN_DIKUNCI", err.get("kode").asString)
    }

    @Test
    fun `cashier login works and deactivated cashier gets 401 on login and existing token`() = withAuthApp { _, _ ->
        val reg = client.registerOwner()
        val ownerToken = reg.get("token").asString
        val idToko = reg.getAsJsonObject("toko").get("idToko").asString

        val created = client.createCashier(ownerToken)
        val cashierId = created.getAsJsonObject("akun").get("id").asString
        val cashierToken = client.loginCashierReady(idToko)

        // Existing token works before deactivation
        val meOk = client.get("/api/v1/auth/me") { header("Authorization", "Bearer $cashierToken") }
        assertEquals(HttpStatusCode.OK, meOk.status)

        // Deactivate cashier
        val deactivate = client.patch("/api/v1/kasir/$cashierId") {
            header("Authorization", "Bearer $ownerToken")
            contentType(ContentType.Application.Json)
            setBody("""{"status":"NONAKTIF"}""")
        }
        assertEquals(HttpStatusCode.OK, deactivate.status)

        // Existing token immediately returns 401
        val meAfterDeactivate = client.get("/api/v1/auth/me") { header("Authorization", "Bearer $cashierToken") }
        assertEquals(HttpStatusCode.Unauthorized, meAfterDeactivate.status)

        // New login also returns 401
        val loginAfterDeactivate = client.post("/api/v1/auth/kasir/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"idToko":"$idToko","namaAkun":"kasir1","kataSandi":"SandiKasirBaru123"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, loginAfterDeactivate.status)
    }

    // ============================
    // RBAC & wajibGantiSandi
    // ============================

    @Test
    fun `cashier calling owner endpoints receives 403`() = withAuthApp { _, _ ->
        val reg = client.registerOwner()
        val ownerToken = reg.get("token").asString
        val idToko = reg.getAsJsonObject("toko").get("idToko").asString

        client.createCashier(ownerToken)
        val cashierToken = client.loginCashierReady(idToko)

        val forbiddenGets = listOf("/api/v1/kasir", "/api/v1/toko")
        forbiddenGets.forEach { path ->
            val resp = client.get(path) { header("Authorization", "Bearer $cashierToken") }
            assertEquals(HttpStatusCode.Forbidden, resp.status, "Expected 403 on GET $path")
            assertEquals("AKSES_DITOLAK", gson.fromJson(resp.bodyAsText(), JsonObject::class.java).get("kode").asString)
        }

        val revokeOthers = client.post("/api/v1/auth/sessions/revoke-others") {
            header("Authorization", "Bearer $cashierToken")
        }
        assertEquals(HttpStatusCode.Forbidden, revokeOthers.status)

        val deleteOwner = client.delete("/api/v1/auth/owner/account") {
            header("Authorization", "Bearer $cashierToken")
            contentType(ContentType.Application.Json)
            setBody("""{"kataSandiSaatIni":"SandiKasirBaru123","konfirmasi":"HAPUS"}""")
        }
        assertEquals(HttpStatusCode.Forbidden, deleteOwner.status)

        val patchStore = client.patch("/api/v1/toko") {
            header("Authorization", "Bearer $cashierToken")
            contentType(ContentType.Application.Json)
            setBody("""{"namaToko":"Dibajak"}""")
        }
        assertEquals(HttpStatusCode.Forbidden, patchStore.status)
    }

    @Test
    fun `cashier with wajibGantiSandi is restricted until password changed and keeps current session after change`() = withAuthApp { _, _ ->
        val reg = client.registerOwner()
        val ownerToken = reg.get("token").asString
        val idToko = reg.getAsJsonObject("toko").get("idToko").asString
        client.createCashier(ownerToken)

        val loginResp = client.post("/api/v1/auth/kasir/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"idToko":"$idToko","namaAkun":"kasir1","kataSandi":"SandiKasir123"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResp.status)
        val loginBody = gson.fromJson(loginResp.bodyAsText(), JsonObject::class.java)
        assertTrue(loginBody.get("wajibGantiSandi").asBoolean)
        val cashierToken = loginBody.get("token").asString

        // Blocked on /profil before changing password
        val blocked = client.patch("/api/v1/profil") {
            header("Authorization", "Bearer $cashierToken")
            contentType(ContentType.Application.Json)
            setBody("""{"nama":"Kasir Baru"}""")
        }
        assertEquals(HttpStatusCode.Forbidden, blocked.status)
        assertEquals("WAJIB_GANTI_SANDI", gson.fromJson(blocked.bodyAsText(), JsonObject::class.java).get("kode").asString)

        // Change password keeps current session usable
        val changed = client.post("/api/v1/auth/change-password") {
            header("Authorization", "Bearer $cashierToken")
            contentType(ContentType.Application.Json)
            setBody("""{"kataSandiSaatIni":"SandiKasir123","kataSandiBaru":"SandiBaru123"}""")
        }
        assertEquals(HttpStatusCode.NoContent, changed.status)

        // Now /profil succeeds with the same token
        val allowed = client.patch("/api/v1/profil") {
            header("Authorization", "Bearer $cashierToken")
            contentType(ContentType.Application.Json)
            setBody("""{"nama":"Kasir Updated"}""")
        }
        assertEquals(HttpStatusCode.OK, allowed.status)
        val updatedAkun = gson.fromJson(allowed.bodyAsText(), JsonObject::class.java).getAsJsonObject("akun")
        assertFalse(updatedAkun.get("wajibGantiSandi").asBoolean)
        assertEquals("Kasir Updated", updatedAkun.get("namaAkun").asString)
    }

    // ============================
    // Store Isolation & Cashier Uniqueness
    // ============================

    @Test
    fun `store A cannot access or modify store B cashiers`() = withAuthApp { _, _ ->
        val tokenA = client.registerOwner("a@toko.test", storeName = "Toko A").get("token").asString
        val tokenB = client.registerOwner("b@toko.test", storeName = "Toko B").get("token").asString

        val cashierB = client.createCashier(tokenB, name = "kasirB")
        val cashierBId = cashierB.getAsJsonObject("akun").get("id").asString

        // Store A lists cashiers -> empty
        val listA = client.get("/api/v1/kasir") { header("Authorization", "Bearer $tokenA") }
        assertEquals(HttpStatusCode.OK, listA.status)
        assertEquals(0, gson.fromJson(listA.bodyAsText(), JsonObject::class.java).getAsJsonArray("data").size())

        // Store A tries to PATCH, reset password, and DELETE Store B's cashier -> 404
        val patchResp = client.patch("/api/v1/kasir/$cashierBId") {
            header("Authorization", "Bearer $tokenA")
            contentType(ContentType.Application.Json)
            setBody("""{"status":"NONAKTIF"}""")
        }
        assertEquals(HttpStatusCode.NotFound, patchResp.status)

        val resetResp = client.post("/api/v1/kasir/$cashierBId/reset-password") {
            header("Authorization", "Bearer $tokenA")
            contentType(ContentType.Application.Json)
            setBody("""{"kataSandiAwal":"ResetSandi123"}""")
        }
        assertEquals(HttpStatusCode.NotFound, resetResp.status)

        val deleteResp = client.delete("/api/v1/kasir/$cashierBId") {
            header("Authorization", "Bearer $tokenA")
        }
        assertEquals(HttpStatusCode.NotFound, deleteResp.status)
    }

    @Test
    fun `cashier name must be unique per store case-insensitively and reusable after soft delete`() = withAuthApp { _, _ ->
        val tokenA = client.registerOwner("a@toko.test", storeName = "Toko A").get("token").asString
        val tokenB = client.registerOwner("b@toko.test", storeName = "Toko B").get("token").asString

        val created = client.createCashier(tokenA, name = "Budi")
        val budiId = created.getAsJsonObject("akun").get("id").asString

        // Same store, different case -> 409
        val duplicate = client.post("/api/v1/kasir") {
            header("Authorization", "Bearer $tokenA")
            contentType(ContentType.Application.Json)
            setBody("""{"id":"${UUID.randomUUID()}","namaAkun":"budi","kataSandiAwal":"SandiKasir123"}""")
        }
        assertEquals(HttpStatusCode.Conflict, duplicate.status)
        assertEquals("NAMA_AKUN_SUDAH_DIPAKAI", gson.fromJson(duplicate.bodyAsText(), JsonObject::class.java).get("kode").asString)

        // Different store -> 201
        val otherStore = client.createCashier(tokenB, name = "Budi")
        assertEquals("Budi", otherStore.getAsJsonObject("akun").get("namaAkun").asString)

        // Soft delete Budi in Store A
        val deleted = client.delete("/api/v1/kasir/$budiId") { header("Authorization", "Bearer $tokenA") }
        assertEquals(HttpStatusCode.OK, deleted.status)

        // Soft-deleted cashier cannot be reactivated via PATCH -> 404
        val reactivate = client.patch("/api/v1/kasir/$budiId") {
            header("Authorization", "Bearer $tokenA")
            contentType(ContentType.Application.Json)
            setBody("""{"status":"AKTIF"}""")
        }
        assertEquals(HttpStatusCode.NotFound, reactivate.status)

        // Same name in Store A can now be reused -> 201
        val recreated = client.createCashier(tokenA, name = "Budi")
        assertEquals("Budi", recreated.getAsJsonObject("akun").get("namaAkun").asString)
    }

    // ============================
    // Profile, Sessions & Deletion
    // ============================

    @Test
    fun `profile enforces role rules for email and name conflict`() = withAuthApp { _, _ ->
        val reg = client.registerOwner("owner@toko.test")
        val ownerToken = reg.get("token").asString
        val idToko = reg.getAsJsonObject("toko").get("idToko").asString

        client.createCashier(ownerToken, name = "kasir1")
        client.createCashier(ownerToken, name = "kasir2")
        val cashierToken = client.loginCashierReady(idToko, name = "kasir1")

        // Kasir cannot set email (K-31)
        val emailDenied = client.patch("/api/v1/profil") {
            header("Authorization", "Bearer $cashierToken")
            contentType(ContentType.Application.Json)
            setBody("""{"email":"kasir@toko.test"}""")
        }
        assertEquals(HttpStatusCode.Forbidden, emailDenied.status)

        // Kasir renaming to existing cashier name in same store -> 409 NAMA_AKUN_SUDAH_DIPAKAI
        val nameConflict = client.patch("/api/v1/profil") {
            header("Authorization", "Bearer $cashierToken")
            contentType(ContentType.Application.Json)
            setBody("""{"nama":"KASIR2"}""")
        }
        assertEquals(HttpStatusCode.Conflict, nameConflict.status)
        assertEquals("NAMA_AKUN_SUDAH_DIPAKAI", gson.fromJson(nameConflict.bodyAsText(), JsonObject::class.java).get("kode").asString)
    }

    @Test
    fun `logout and revoke-others manage sessions accurately`() = withAuthApp { _, _ ->
        client.registerOwner("owner@toko.test")
        val token1 = client.loginOwner("owner@toko.test").get("token").asString
        val token2 = client.loginOwner("owner@toko.test").get("token").asString
        val token3 = client.loginOwner("owner@toko.test").get("token").asString

        // Logout token2
        val logoutResp = client.post("/api/v1/auth/logout") { header("Authorization", "Bearer $token2") }
        assertEquals(HttpStatusCode.NoContent, logoutResp.status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/v1/auth/me") { header("Authorization", "Bearer $token2") }.status)

        // Revoke others from token3 -> revokes token1 (and initial register session)
        val revokeResp = client.post("/api/v1/auth/sessions/revoke-others") { header("Authorization", "Bearer $token3") }
        assertEquals(HttpStatusCode.OK, revokeResp.status)
        assertTrue(gson.fromJson(revokeResp.bodyAsText(), JsonObject::class.java).get("jumlahSesiDiakhiri").asInt >= 2)

        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/v1/auth/me") { header("Authorization", "Bearer $token1") }.status)
        assertEquals(HttpStatusCode.OK, client.get("/api/v1/auth/me") { header("Authorization", "Bearer $token3") }.status)
    }

    @Test
    fun `owner deletion schedules 14-day grace period and login cancels it and purge removes expired store`() = withAuthApp { _, repo ->
        val reg = client.registerOwner("owner@toko.test")
        val token = reg.get("token").asString
        val idToko = reg.getAsJsonObject("toko").get("idToko").asString

        // Schedule deletion
        val delResp = client.delete("/api/v1/auth/owner/account") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody("""{"kataSandiSaatIni":"TestSandi123","konfirmasi":"HAPUS"}""")
        }
        assertEquals(HttpStatusCode.Accepted, delResp.status)
        val delBody = gson.fromJson(delResp.bodyAsText(), JsonObject::class.java)
        val scheduledAt = delBody.get("penghapusanDijadwalkanPada").asLong
        val deleteAt = delBody.get("dapatDipulihkanSampai").asLong
        assertEquals(AuthRepository.OWNER_DELETION_GRACE_MILLIS, deleteAt - scheduledAt)

        // Existing token is revoked
        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/v1/auth/me") { header("Authorization", "Bearer $token") }.status)

        // Login during grace period cancels deletion
        val recoveredToken = client.loginOwner("owner@toko.test").get("token").asString
        assertEquals(HttpStatusCode.OK, client.get("/api/v1/auth/me") { header("Authorization", "Bearer $recoveredToken") }.status)

        // Schedule deletion again and verify purge after grace period
        client.delete("/api/v1/auth/owner/account") {
            header("Authorization", "Bearer $recoveredToken")
            contentType(ContentType.Application.Json)
            setBody("""{"kataSandiSaatIni":"TestSandi123","konfirmasi":"HAPUS"}""")
        }
        val purged = repo.purgeExpiredStores(System.currentTimeMillis() + AuthRepository.OWNER_DELETION_GRACE_MILLIS + 1)
        assertEquals(1, purged)
        assertEquals(null, repo.findStore(idToko))
    }

    @Test
    fun `opening balance update shifts cumulative balance`() = withAuthApp { _, repo ->
        val token = client.registerOwner(openingBalance = 100_000L).get("token").asString

        val resp = client.patch("/api/v1/toko/saldo-awal") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody("""{"saldoAwalBaru":250000}""")
        }
        assertEquals(HttpStatusCode.OK, resp.status)
        val body = gson.fromJson(resp.bodyAsText(), JsonObject::class.java)
        assertEquals(250_000L, body.get("saldoAwal").asLong)
        assertEquals(250_000L, body.get("saldoKumulatif").asLong)
        assertTrue(repo.auditLogs.any { it.kategori == "KEUANGAN" && it.aksi == "SALDO_AWAL_DIUBAH" })
    }

    // ============================
    // Password & Token Never in Response or Log
    // ============================

    @Test
    fun `passwords hashes and bearer tokens never appear in responses or logs`() = withAuthApp { _, _ ->
        val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        val appender = ListAppender<ILoggingEvent>().apply { start() }
        rootLogger.addAppender(appender)
        try {
            val ownerPass = "SandiRahasiaOwner99!"
            val cashierPass = "SandiRahasiaKasir88!"

            val regResp = client.post("/api/v1/auth/owner/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"owner@toko.test","kataSandi":"$ownerPass","namaToko":"Toko Aman","saldoAwal":0}""")
            }
            val regBody = regResp.bodyAsText()
            assertFalse(regBody.contains(ownerPass))
            assertFalse(regBody.contains("sandiHash"))
            assertFalse(regBody.contains("sandiSalt"))
            val ownerToken = gson.fromJson(regBody, JsonObject::class.java).get("token").asString

            val createCashierResp = client.post("/api/v1/kasir") {
                header("Authorization", "Bearer $ownerToken")
                contentType(ContentType.Application.Json)
                setBody("""{"id":"${UUID.randomUUID()}","namaAkun":"kasirAman","kataSandiAwal":"$cashierPass"}""")
            }
            val createCashierBody = createCashierResp.bodyAsText()
            assertFalse(createCashierBody.contains(cashierPass))
            assertFalse(createCashierBody.contains("sandiHash"))

            val loggedText = appender.list.joinToString("\n") { it.formattedMessage }
            assertFalse(loggedText.contains(ownerPass))
            assertFalse(loggedText.contains(cashierPass))
            assertFalse(loggedText.contains(ownerToken))
        } finally {
            rootLogger.detachAppender(appender)
            appender.stop()
        }
    }
}
