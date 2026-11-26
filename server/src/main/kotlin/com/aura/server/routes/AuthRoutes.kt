package com.aura.server.routes

import com.aura.server.auth.*
import com.aura.server.plugins.ApiException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

internal fun Route.authRoutes(auth: AuthComponents) {
    route("/api/v1") {
        route("/auth") {
            post("/owner/register") {
                val request = call.receive<OwnerRegisterRequest>()
                val result = withContext(Dispatchers.IO) { auth.service.registerOwner(request) }
                call.respond(HttpStatusCode.Created, SessionResponse(result.account.toResponse(), result.store.toResponse(), result.token.token, result.token.expiresAt))
            }
            post("/owner/login") {
                val result = withContext(Dispatchers.IO) { auth.service.loginOwner(call.receive()) }
                call.respond(SessionResponse(result.account.toResponse(), result.store.toResponse(), result.token.token, result.token.expiresAt))
            }
            post("/kasir/login") {
                val result = withContext(Dispatchers.IO) { auth.service.loginCashier(call.receive()) }
                call.respond(SessionResponse(result.account.toResponse(), result.store.toResponse(), result.token.token, result.token.expiresAt, result.account.wajibGantiSandi))
            }
            authenticate("auth-jwt") {
                get("/me") {
                    val account = call.authenticatedAccount(auth)
                    val store = withContext(Dispatchers.IO) { auth.repository.findStore(account.idToko) }
                        ?: throw ApiException(HttpStatusCode.NotFound, "TOKO_TIDAK_DITEMUKAN", "Toko tidak ditemukan.")
                    call.respond(MeResponse(account.toResponse(), store.toResponse()))
                }
                post("/logout") {
                    val (account, jti) = call.authenticatedAccountAndJti(auth)
                    withContext(Dispatchers.IO) { auth.repository.logout(account, jti) }
                    call.respond(HttpStatusCode.NoContent)
                }
                post("/change-password") {
                    val (account, jti) = call.authenticatedAccountAndJti(auth)
                    withContext(Dispatchers.IO) { auth.service.changePassword(account, call.receive(), jti) }
                    call.respond(HttpStatusCode.NoContent)
                }
                post("/sessions/revoke-others") {
                    val (account, jti) = call.authenticatedAccountAndJti(auth)
                    if (account.peran != Peran.OWNER) throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Hanya Owner yang dapat mengubah data ini.")
                    val count = withContext(Dispatchers.IO) { auth.repository.revokeOtherSessions(account, jti) }
                    call.respond(RevokeSessionsResponse(count))
                }
                delete("/owner/account") {
                    val account = call.authenticatedAccount(auth)
                    val result = withContext(Dispatchers.IO) { auth.service.deleteOwner(account, call.receive()) }
                    call.respond(HttpStatusCode.Accepted, result)
                }
            }
        }

        authenticate("auth-jwt") {
            route("/kasir") {
                get {
                    val owner = call.authenticatedAccount(auth)
                    if (owner.peran != Peran.OWNER) throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Hanya Owner yang dapat mengubah data ini.")
                    val accounts = withContext(Dispatchers.IO) { auth.repository.listCashiers(owner.idToko) }
                    call.respond(KasirListResponse(accounts.map { KasirListItem(it.id, it.nama, it.status.name, it.terakhirLogin) }))
                }
                post {
                    val owner = call.authenticatedAccount(auth)
                    val cashier = withContext(Dispatchers.IO) { auth.service.createCashier(owner, call.receive()) }
                    call.respond(HttpStatusCode.Created, AccountEnvelope(cashier.toResponse()))
                }
                patch("/{akunId}") {
                    val owner = call.authenticatedAccount(auth)
                    val id = call.parameters["akunId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                        ?: throw ApiException(HttpStatusCode.BadRequest, "ID_TIDAK_VALID", "ID akun tidak valid.")
                    val cashier = withContext(Dispatchers.IO) { auth.service.setCashierStatus(owner, id, call.receive()) }
                    call.respond(AccountEnvelope(cashier.toResponse()))
                }
                post("/{akunId}/reset-password") {
                    val owner = call.authenticatedAccount(auth)
                    val id = call.parameters["akunId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                        ?: throw ApiException(HttpStatusCode.BadRequest, "ID_TIDAK_VALID", "ID akun tidak valid.")
                    val cashier = withContext(Dispatchers.IO) { auth.service.resetCashierPassword(owner, id, call.receive()) }
                    call.respond(AccountEnvelope(cashier.toResponse()))
                }
                delete("/{akunId}") {
                    val owner = call.authenticatedAccount(auth)
                    val id = call.parameters["akunId"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                        ?: throw ApiException(HttpStatusCode.BadRequest, "ID_TIDAK_VALID", "ID akun tidak valid.")
                    val cashier = withContext(Dispatchers.IO) { auth.service.deleteCashier(owner, id) }
                    call.respond(AccountEnvelope(cashier.toResponse()))
                }
            }
            patch("/profil") {
                val account = call.authenticatedAccount(auth)
                val request = call.receive<UpdateProfileRequest>()
                if (account.peran == Peran.KASIR && request.email != null) {
                    throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Kasir tidak dapat mengubah email.")
                }
                val email = request.email?.trim()?.lowercase()
                if (email != null && !EMAIL_PATTERN.matches(email)) {
                    throw ApiException(HttpStatusCode.BadRequest, "EMAIL_TIDAK_VALID", "Format email tidak valid.")
                }
                if (request.nama != null && (request.nama.isBlank() || request.nama.length > 80)) {
                    throw ApiException(HttpStatusCode.BadRequest, "NAMA_TIDAK_VALID", "Nama akun tidak valid.")
                }
                if (request.nomorHp != null && request.nomorHp.length > 30) {
                    throw ApiException(HttpStatusCode.BadRequest, "NOMOR_HP_TIDAK_VALID", "Nomor HP tidak valid.")
                }
                val updated = try {
                    withContext(Dispatchers.IO) { auth.repository.updateProfile(account, request.nama?.trim(), request.nomorHp?.trim(), request.fotoFileId, email) }
                } catch (error: Exception) {
                    if (error.findSqlState() == "23505") {
                        if (account.peran == Peran.KASIR) {
                            throw ApiException(HttpStatusCode.Conflict, "NAMA_AKUN_SUDAH_DIPAKAI", "Nama akun sudah digunakan di toko ini.")
                        }
                        throw ApiException(HttpStatusCode.Conflict, "EMAIL_SUDAH_DIPAKAI", "Email sudah digunakan.")
                    }
                    throw error
                }
                call.respond(AccountEnvelope(updated.toResponse()))
            }
            get("/toko") {
                val account = call.authenticatedAccount(auth)
                if (account.peran != Peran.OWNER) throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Hanya Owner yang dapat melihat data ini.")
                val store = withContext(Dispatchers.IO) { auth.repository.findStore(account.idToko) }
                    ?: throw ApiException(HttpStatusCode.NotFound, "TOKO_TIDAK_DITEMUKAN", "Toko tidak ditemukan.")
                call.respond(store.toResponse())
            }
            patch("/toko") {
                val account = call.authenticatedAccount(auth)
                if (account.peran != Peran.OWNER) throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Hanya Owner yang dapat mengubah data ini.")
                val request = call.receive<UpdateStoreRequest>()
                if (listOf(request.namaToko, request.jenisUsaha, request.alamat).all { it == null } && request.logoFileId == null) {
                    throw ApiException(HttpStatusCode.BadRequest, "PERUBAHAN_KOSONG", "Isi minimal satu data toko.")
                }
                if ((request.namaToko?.let { it.isBlank() || it.length > 120 } == true) ||
                    (request.jenisUsaha?.length ?: 0) > 80 || (request.alamat?.length ?: 0) > 500
                ) throw ApiException(HttpStatusCode.BadRequest, "DATA_TOKO_TIDAK_VALID", "Data toko tidak valid.")
                val store = withContext(Dispatchers.IO) { auth.repository.updateStore(account, request.namaToko?.trim(), request.jenisUsaha?.trim(), request.alamat?.trim(), request.logoFileId) }
                call.respond(mapOf("toko" to store.toResponse()))
            }
            patch("/toko/saldo-awal") {
                val account = call.authenticatedAccount(auth)
                if (account.peran != Peran.OWNER) throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Hanya Owner yang dapat mengubah data ini.")
                val amount = call.receive<UpdateOpeningBalanceRequest>().saldoAwalBaru
                    ?: throw ApiException(HttpStatusCode.BadRequest, "SALDO_TIDAK_VALID", "Saldo awal wajib diisi.")
                if (amount < 0) throw ApiException(HttpStatusCode.BadRequest, "SALDO_TIDAK_VALID", "Saldo awal tidak boleh negatif.")
                val store = withContext(Dispatchers.IO) { auth.repository.updateOpeningBalance(account, amount) }
                call.respond(mapOf("saldoAwal" to store.saldoAwal, "saldoKumulatif" to store.saldoKumulatif))
            }
        }
    }
}

private data class MeResponse(val akun: AkunResponse, val tokoRingkas: TokoResponse)
private data class KasirListItem(val id: UUID, val namaAkun: String, val status: String, val terakhirLogin: Long?)
private data class KasirListResponse(val data: List<KasirListItem>)
private data class RevokeSessionsResponse(val jumlahSesiDiakhiri: Int)

internal suspend fun ApplicationCall.authenticatedAccount(auth: AuthComponents): Akun = authenticatedAccountAndJti(auth).first

private suspend fun ApplicationCall.authenticatedAccountAndJti(auth: AuthComponents): Pair<Akun, UUID> {
    val principal = principal<AkunPrincipal>() ?: throw ApiException(HttpStatusCode.Unauthorized, "SESI_TIDAK_VALID", "Sesi tidak valid atau telah berakhir.")
    val account = withContext(Dispatchers.IO) { auth.repository.findAccount(principal.akunId) }
        ?.takeIf { it.status == StatusAkun.AKTIF }
        ?: throw ApiException(HttpStatusCode.Unauthorized, "SESI_TIDAK_VALID", "Sesi tidak valid atau telah berakhir.")
    val permittedDuringRequiredChange = request.path() in setOf("/api/v1/auth/me", "/api/v1/auth/logout", "/api/v1/auth/change-password")
    if (account.wajibGantiSandi && !permittedDuringRequiredChange) {
        throw ApiException(HttpStatusCode.Forbidden, "WAJIB_GANTI_SANDI", "Ganti kata sandi sebelum menggunakan fitur lain.")
    }
    return account to principal.jti
}

private val EMAIL_PATTERN = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
private fun Throwable.findSqlState(): String? = generateSequence(this) { it.cause }
    .filterIsInstance<java.sql.SQLException>().firstOrNull()?.sqlState
