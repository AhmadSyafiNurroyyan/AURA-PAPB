package com.aura.server.routes

import com.aura.server.auth.AuthComponents
import com.aura.server.catalog.BarangCreateRequest
import com.aura.server.catalog.BarangUpdateRequest
import com.aura.server.catalog.CatalogComponents
import com.aura.server.catalog.CatalogFileService
import com.aura.server.catalog.KategoriCreateRequest
import com.aura.server.catalog.KategoriUpdateRequest
import com.aura.server.catalog.SupplierCreateRequest
import com.aura.server.catalog.SupplierStatusRequest
import com.aura.server.catalog.SupplierUpdateRequest
import com.aura.server.plugins.ApiException
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

internal fun Route.catalogRoutes(auth: AuthComponents, catalog: CatalogComponents) {
    route("/api/v1") {
        authenticate("auth-jwt") {
            route("/barang") {
                get {
                    val account = call.authenticatedAccount(auth)
                    val categoryId = call.request.queryParameters["kategoriId"]?.let {
                        parseId(it, "ID kategori tidak valid.")
                    }
                    val response = withContext(Dispatchers.IO) {
                        catalog.service.listBarang(
                            account = account,
                            query = call.request.queryParameters["q"],
                            categoryId = categoryId,
                            status = call.request.queryParameters["status"],
                            sort = call.request.queryParameters["sort"],
                            limit = call.request.queryParameters["limit"]?.let(::parseLimit),
                            cursor = call.request.queryParameters["cursor"],
                        )
                    }
                    call.respond(response)
                }
                post {
                    val account = call.authenticatedAccount(auth)
                    val request = call.receive<BarangCreateRequest>()
                    val response = withContext(Dispatchers.IO) { catalog.service.createBarang(account, request) }
                    call.respond(HttpStatusCode.Created, response)
                }
                get("/{barangId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["barangId"]?.let { parseId(it, "ID Barang tidak valid.") }
                        ?: invalidId("ID Barang wajib diisi.")
                    val response = withContext(Dispatchers.IO) { catalog.service.detailBarang(account, id) }
                    call.respond(response)
                }
                put("/{barangId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["barangId"]?.let { parseId(it, "ID Barang tidak valid.") }
                        ?: invalidId("ID Barang wajib diisi.")
                    val request = call.receive<BarangUpdateRequest>()
                    val barang = withContext(Dispatchers.IO) { catalog.service.updateBarang(account, id, request) }
                    call.respond(mapOf("barang" to barang))
                }
                delete("/{barangId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["barangId"]?.let { parseId(it, "ID Barang tidak valid.") }
                        ?: invalidId("ID Barang wajib diisi.")
                    val response = withContext(Dispatchers.IO) { catalog.service.deleteBarang(account, id) }
                    call.respond(response)
                }
            }

            route("/kategori") {
                get {
                    val account = call.authenticatedAccount(auth)
                    val response = withContext(Dispatchers.IO) { catalog.service.listKategori(account) }
                    call.respond(response)
                }
                post {
                    val account = call.authenticatedAccount(auth)
                    val request = call.receive<KategoriCreateRequest>()
                    val category = withContext(Dispatchers.IO) { catalog.service.createKategori(account, request) }
                    call.respond(HttpStatusCode.Created, mapOf("kategori" to category))
                }
                put("/{kategoriId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["kategoriId"]?.let { parseId(it, "ID kategori tidak valid.") }
                        ?: invalidId("ID kategori wajib diisi.")
                    val request = call.receive<KategoriUpdateRequest>()
                    val category = withContext(Dispatchers.IO) { catalog.service.updateKategori(account, id, request) }
                    call.respond(mapOf("kategori" to category))
                }
                delete("/{kategoriId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["kategoriId"]?.let { parseId(it, "ID kategori tidak valid.") }
                        ?: invalidId("ID kategori wajib diisi.")
                    withContext(Dispatchers.IO) { catalog.service.deleteKategori(account, id) }
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            route("/supplier") {
                get {
                    val account = call.authenticatedAccount(auth)
                    val response = withContext(Dispatchers.IO) {
                        catalog.service.listSupplier(
                            account,
                            call.request.queryParameters["q"],
                            call.request.queryParameters["status"],
                            call.request.queryParameters["limit"]?.let(::parseLimit),
                            call.request.queryParameters["cursor"],
                        )
                    }
                    call.respond(response)
                }
                post {
                    val account = call.authenticatedAccount(auth)
                    val supplier = withContext(Dispatchers.IO) {
                        catalog.service.createSupplier(account, call.receive<SupplierCreateRequest>())
                    }
                    call.respond(HttpStatusCode.Created, mapOf("supplier" to supplier))
                }
                get("/{supplierId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["supplierId"]?.let { parseId(it, "ID Supplier tidak valid.") }
                        ?: invalidId("ID Supplier wajib diisi.")
                    val supplier = withContext(Dispatchers.IO) { catalog.service.detailSupplier(account, id) }
                    call.respond(supplier)
                }
                put("/{supplierId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["supplierId"]?.let { parseId(it, "ID Supplier tidak valid.") }
                        ?: invalidId("ID Supplier wajib diisi.")
                    val request = call.receive<SupplierUpdateRequest>()
                    val supplier = withContext(Dispatchers.IO) { catalog.service.updateSupplier(account, id, request) }
                    call.respond(mapOf("supplier" to supplier))
                }
                patch("/{supplierId}/status") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["supplierId"]?.let { parseId(it, "ID Supplier tidak valid.") }
                        ?: invalidId("ID Supplier wajib diisi.")
                    val request = call.receive<SupplierStatusRequest>()
                    val supplier = withContext(Dispatchers.IO) { catalog.service.setSupplierStatus(account, id, request) }
                    call.respond(mapOf("supplier" to supplier))
                }
                delete("/{supplierId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["supplierId"]?.let { parseId(it, "ID Supplier tidak valid.") }
                        ?: invalidId("ID Supplier wajib diisi.")
                    withContext(Dispatchers.IO) { catalog.service.deleteSupplier(account, id) }
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            route("/file") {
                post {
                    val account = call.authenticatedAccount(auth)
                    val multipart = call.receiveMultipart()
                    var fileContent: ByteArray? = null
                    var mimeType: String? = null
                    multipart.forEachPart { part ->
                        if (part is PartData.FileItem && part.name == "file") {
                            if (fileContent != null) {
                                part.release()
                                throw ApiException(HttpStatusCode.BadRequest, "FILE_TIDAK_VALID", "Permintaan hanya boleh berisi satu file.")
                            }
                            mimeType = part.contentType?.toString()?.substringBefore(';')?.lowercase()
                            fileContent = part.provider().toInputStream().use {
                                it.readNBytes((CatalogFileService.MAX_FILE_BYTES + 1).toInt())
                            }
                        }
                        part.release()
                    }
                    val content = fileContent
                        ?: throw ApiException(HttpStatusCode.BadRequest, "FILE_TIDAK_VALID", "File wajib diisi.")
                    val type = mimeType
                        ?: throw ApiException(HttpStatusCode.UnsupportedMediaType, "TIPE_FILE_TIDAK_DIDUKUNG", "Tipe file harus JPEG, PNG, atau WebP.")
                    val response = withContext(Dispatchers.IO) { catalog.fileService.upload(account, type, content) }
                    call.respond(HttpStatusCode.Created, response)
                }
                get("/{fileId}") {
                    val account = call.authenticatedAccount(auth)
                    val id = call.parameters["fileId"]?.let { parseId(it, "ID file tidak valid.") }
                        ?: invalidId("ID file wajib diisi.")
                    val (metadata, bytes) = withContext(Dispatchers.IO) { catalog.fileService.download(account, id) }
                    call.respondBytes(bytes, ContentType.parse(metadata.mimeType))
                }
            }
        }
    }
}

private fun parseId(value: String, message: String): UUID = runCatching { UUID.fromString(value) }.getOrNull()
    ?: invalidId(message)

private fun parseLimit(value: String): Int = value.toIntOrNull()
    ?: throw ApiException(HttpStatusCode.BadRequest, "LIMIT_TIDAK_VALID", "Jumlah data per halaman tidak valid.")

private fun invalidId(message: String): Nothing =
    throw ApiException(HttpStatusCode.BadRequest, "ID_TIDAK_VALID", message)
