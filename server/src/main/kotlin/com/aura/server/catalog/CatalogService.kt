package com.aura.server.catalog

import com.aura.server.auth.Akun
import com.aura.server.auth.Peran
import com.aura.server.plugins.ApiException
import io.ktor.http.HttpStatusCode
import java.net.URI
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.UUID

internal class CatalogService(
    private val repository: CatalogRepository,
) {
    fun listBarang(
        account: Akun,
        query: String?,
        categoryId: UUID?,
        status: String?,
        sort: String?,
        limit: Int?,
        cursor: String?,
    ): BarangListResponse {
        val requestedStatus = status ?: STATUS_AKTIF
        if (requestedStatus !in setOf(STATUS_AKTIF, STATUS_DIARSIPKAN)) {
            invalid("STATUS_BARANG_TIDAK_VALID", "Status Barang tidak valid.")
        }
        if (account.peran == Peran.KASIR && requestedStatus != STATUS_AKTIF) {
            throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Kasir hanya dapat melihat Barang aktif.")
        }
        val requestedSort = sort ?: SORT_NAMA
        if (requestedSort !in setOf(SORT_NAMA, SORT_STOK_TERENDAH, SORT_TERLARIS)) {
            invalid("URUTAN_TIDAK_VALID", "Urutan Barang tidak valid.")
        }
        val pageSize = limit ?: DEFAULT_PAGE_SIZE
        if (pageSize !in 1..MAX_PAGE_SIZE) invalid("LIMIT_TIDAK_VALID", "Jumlah data per halaman tidak valid.")
        val decodedCursor = cursor?.let { decodeBarangCursor(it, requestedSort) }
        val page = repository.listBarang(
            storeId = account.idToko,
            query = query?.trim()?.takeIf(String::isNotEmpty),
            categoryId = categoryId,
            status = requestedStatus,
            sort = requestedSort,
            limit = pageSize,
            cursor = decodedCursor,
        )
        return BarangListResponse(page.data.map { it.toResponse() }, page.nextCursor?.let { encodeBarangCursor(it, requestedSort) })
    }

    fun detailBarang(account: Akun, barangId: UUID): BarangDetailResponse {
        val detail = repository.findBarangDetail(account.idToko, barangId)
            ?: notFound("BARANG_TIDAK_DITEMUKAN", "Barang tidak ditemukan.")
        if (account.peran == Peran.KASIR && detail.barang.status != STATUS_AKTIF) {
            notFound("BARANG_TIDAK_DITEMUKAN", "Barang tidak ditemukan.")
        }
        return BarangDetailResponse(
            barang = detail.barang.toResponse(),
            hargaBeliSupplier = detail.hargaBeliSupplier.map { it.toResponse() },
            kartuStokRingkas = detail.kartuStokRingkas,
        )
    }

    fun createBarang(account: Akun, request: BarangCreateRequest): BarangWriteResponse {
        requireOwner(account)
        validateName(request.nama, MAX_NAMA_BARANG, "NAMA_BARANG_TIDAK_VALID", "Nama Barang")
        validateName(request.satuan, MAX_SATUAN, "SATUAN_TIDAK_VALID", "Satuan")
        if ((request.sku?.length ?: 0) > MAX_SKU) invalid("SKU_TIDAK_VALID", "SKU tidak valid.")
        if (request.hargaJual < 0 || request.stokMinimum < 0 || request.stokAwal < 0) {
            invalid("DATA_BARANG_TIDAK_VALID", "Harga dan jumlah stok tidak boleh negatif.")
        }
        if (request.hargaBeliAwal != null && request.hargaBeliAwal < 0) {
            invalid("HARGA_BELI_TIDAK_VALID", "Harga beli awal tidak boleh negatif.")
        }
        validateFile(account, request.fotoFileId)
        validateCategories(account, request.kategoriIds.orEmpty())
        if (request.supplierId != null && request.hargaBeliAwal == null) {
            invalid("DATA_HARGA_BELI_TIDAK_VALID", "Harga beli awal wajib diisi saat Supplier dipilih.")
        }
        request.supplierId?.let { supplierId ->
            val supplier = repository.findSupplier(account.idToko, supplierId)?.supplier
                ?: notFound("SUPPLIER_TIDAK_DITEMUKAN", "Supplier tidak ditemukan.")
            if (supplier.status != STATUS_AKTIF) {
                throw ApiException(HttpStatusCode.Conflict, "SUPPLIER_NONAKTIF", "Supplier tidak aktif.")
            }
        }
        val result = onDuplicate("BARANG_SUDAH_ADA", "Barang dengan ID tersebut sudah ada.") {
            repository.createBarang(account, request)
        }
        return BarangWriteResponse(
            barang = result.barang.toResponse(),
            pergerakan = result.pergerakan,
            saldoKumulatif = result.saldoKumulatif,
        )
    }

    fun updateBarang(account: Akun, barangId: UUID, request: BarangUpdateRequest): BarangResponse {
        requireOwner(account)
        if (listOf(request.nama, request.satuan, request.sku, request.hargaJual, request.stokMinimum, request.fotoFileId, request.kategoriIds).all { it == null }) {
            invalid("PERUBAHAN_KOSONG", "Isi minimal satu data Barang.")
        }
        request.nama?.let { validateName(it, MAX_NAMA_BARANG, "NAMA_BARANG_TIDAK_VALID", "Nama Barang") }
        request.satuan?.let { validateName(it, MAX_SATUAN, "SATUAN_TIDAK_VALID", "Satuan") }
        if ((request.sku?.length ?: 0) > MAX_SKU) invalid("SKU_TIDAK_VALID", "SKU tidak valid.")
        if (request.hargaJual != null && request.hargaJual < 0) invalid("HARGA_JUAL_TIDAK_VALID", "Harga jual tidak boleh negatif.")
        if (request.stokMinimum != null && request.stokMinimum < 0) invalid("STOK_MINIMUM_TIDAK_VALID", "Stok minimum tidak boleh negatif.")
        validateFile(account, request.fotoFileId)
        request.kategoriIds?.let { validateCategories(account, it) }
        return repository.updateBarang(account, barangId, request)?.toResponse()
            ?: notFound("BARANG_TIDAK_DITEMUKAN", "Barang tidak ditemukan.")
    }

    fun deleteBarang(account: Akun, barangId: UUID): BarangDeleteResponse {
        requireOwner(account)
        val status = repository.deleteBarang(account, barangId)
            ?: notFound("BARANG_TIDAK_DITEMUKAN", "Barang tidak ditemukan.")
        return BarangDeleteResponse(barangId, status)
    }

    fun listKategori(account: Akun): KategoriListResponse {
        requireOwner(account)
        return KategoriListResponse(repository.listKategori(account.idToko).map { it.toResponse() })
    }

    fun createKategori(account: Akun, request: KategoriCreateRequest): KategoriResponse {
        requireOwner(account)
        validateName(request.nama, MAX_NAMA_KATEGORI, "NAMA_KATEGORI_TIDAK_VALID", "Nama kategori")
        return onDuplicate("NAMA_KATEGORI_SUDAH_DIPAKAI", "Nama kategori sudah digunakan di toko ini.") {
            repository.createKategori(account, request)
        }.toResponse()
    }

    fun updateKategori(account: Akun, kategoriId: UUID, request: KategoriUpdateRequest): KategoriResponse {
        requireOwner(account)
        validateName(request.nama, MAX_NAMA_KATEGORI, "NAMA_KATEGORI_TIDAK_VALID", "Nama kategori")
        return onDuplicate("NAMA_KATEGORI_SUDAH_DIPAKAI", "Nama kategori sudah digunakan di toko ini.") {
            repository.updateKategori(account, kategoriId, request)
        }?.toResponse()
            ?: notFound("KATEGORI_TIDAK_DITEMUKAN", "Kategori tidak ditemukan.")
    }

    fun deleteKategori(account: Akun, kategoriId: UUID) {
        requireOwner(account)
        if (!repository.deleteKategori(account, kategoriId)) {
            notFound("KATEGORI_TIDAK_DITEMUKAN", "Kategori tidak ditemukan.")
        }
    }

    fun listSupplier(
        account: Akun,
        query: String?,
        status: String?,
        limit: Int?,
        cursor: String?,
    ): SupplierListResponse {
        requireOwner(account)
        if (status != null && status !in setOf(STATUS_AKTIF, STATUS_NONAKTIF)) {
            invalid("STATUS_SUPPLIER_TIDAK_VALID", "Status Supplier tidak valid.")
        }
        val pageSize = limit ?: DEFAULT_PAGE_SIZE
        if (pageSize !in 1..MAX_PAGE_SIZE) invalid("LIMIT_TIDAK_VALID", "Jumlah data per halaman tidak valid.")
        val decodedCursor = cursor?.let(::decodeUuidCursor)
        val page = repository.listSupplier(account.idToko, query, status, pageSize, decodedCursor)
        return SupplierListResponse(page.data.map { it.toResponse() }, page.nextCursor?.let(::encodeUuidCursor))
    }

    fun detailSupplier(account: Akun, supplierId: UUID): SupplierDetailResponse {
        requireOwner(account)
        val detail = repository.findSupplier(account.idToko, supplierId)
            ?: notFound("SUPPLIER_TIDAK_DITEMUKAN", "Supplier tidak ditemukan.")
        val prices = detail.hargaBeli.map { it.toResponse() }
        return SupplierDetailResponse(
            supplier = detail.supplier.toResponse(),
            barang = detail.barang.map { it.toResponse() },
            hargaBeliTerakhir = prices,
            riwayatPembelian = emptyList(),
        )
    }

    fun createSupplier(account: Akun, request: SupplierCreateRequest): SupplierResponse {
        requireOwner(account)
        validateName(request.nama, MAX_NAMA_SUPPLIER, "NAMA_SUPPLIER_TIDAK_VALID", "Nama Supplier")
        validateSupplierFields(request.link, request.catatan)
        return onDuplicate("SUPPLIER_SUDAH_ADA", "Supplier dengan ID tersebut sudah ada.") {
            repository.createSupplier(account, request)
        }.toResponse()
    }

    fun updateSupplier(account: Akun, supplierId: UUID, request: SupplierUpdateRequest): SupplierResponse {
        requireOwner(account)
        if (listOf(request.nama, request.link, request.catatan).all { it == null }) {
            invalid("PERUBAHAN_KOSONG", "Isi minimal satu data Supplier.")
        }
        request.nama?.let { validateName(it, MAX_NAMA_SUPPLIER, "NAMA_SUPPLIER_TIDAK_VALID", "Nama Supplier") }
        validateSupplierFields(request.link, request.catatan)
        return repository.updateSupplier(account, supplierId, request)?.toResponse()
            ?: notFound("SUPPLIER_TIDAK_DITEMUKAN", "Supplier tidak ditemukan.")
    }

    fun setSupplierStatus(account: Akun, supplierId: UUID, status: SupplierStatusRequest): SupplierResponse {
        requireOwner(account)
        if (status.status !in setOf(STATUS_AKTIF, STATUS_NONAKTIF)) {
            invalid("STATUS_SUPPLIER_TIDAK_VALID", "Status Supplier tidak valid.")
        }
        return repository.setSupplierStatus(account, supplierId, status.status)?.toResponse()
            ?: notFound("SUPPLIER_TIDAK_DITEMUKAN", "Supplier tidak ditemukan.")
    }

    fun deleteSupplier(account: Akun, supplierId: UUID) {
        requireOwner(account)
        when (repository.deleteSupplier(account, supplierId)) {
            null -> notFound("SUPPLIER_TIDAK_DITEMUKAN", "Supplier tidak ditemukan.")
            "IN_USE" -> throw ApiException(HttpStatusCode.Conflict, "SUPPLIER_SUDAH_DIPAKAI", "Supplier sudah dipakai. Nonaktifkan Supplier untuk mempertahankan riwayat.")
        }
    }

    fun registerFile(metadata: FileMetadata) = repository.createFile(metadata)

    fun findFile(account: Akun, fileId: UUID): FileMetadata =
        repository.findFile(account.idToko, fileId)
            ?: notFound("FILE_TIDAK_DITEMUKAN", "File tidak ditemukan.")

    private fun validateFile(account: Akun, fileId: UUID?) {
        if (fileId != null && repository.findFile(account.idToko, fileId) == null) {
            notFound("FILE_TIDAK_DITEMUKAN", "File tidak ditemukan.")
        }
    }

    private fun validateCategories(account: Akun, categoryIds: List<UUID>) {
        val knownIds = repository.listKategori(account.idToko).mapTo(mutableSetOf()) { it.id }
        if (categoryIds.any { it !in knownIds }) {
            notFound("KATEGORI_TIDAK_DITEMUKAN", "Kategori tidak ditemukan.")
        }
    }

    private fun validateSupplierFields(link: String?, note: String?) {
        if ((link?.length ?: 0) > MAX_LINK) invalid("LINK_SUPPLIER_TIDAK_VALID", "Link Supplier tidak valid.")
        if ((note?.length ?: 0) > MAX_CATATAN) invalid("CATATAN_SUPPLIER_TIDAK_VALID", "Catatan Supplier terlalu panjang.")
        if (link != null && link.isNotBlank()) {
            val uri = try {
                URI(link.trim())
            } catch (_: URISyntaxException) {
                null
            }
            if (uri == null || uri.scheme?.lowercase() !in setOf("http", "https") || uri.host.isNullOrBlank() || uri.userInfo != null) {
                invalid("LINK_SUPPLIER_TIDAK_VALID", "Link Supplier harus berupa URL HTTP atau HTTPS yang valid.")
            }
        }
    }

    private fun validateName(value: String, maxLength: Int, code: String, label: String) {
        val normalized = value.trim()
        if (normalized.isEmpty() || normalized.length > maxLength) {
            invalid(code, "$label wajib diisi dan maksimal $maxLength karakter.")
        }
    }

    private inline fun <T> onDuplicate(code: String, message: String, operation: () -> T): T = try {
        operation()
    } catch (error: Exception) {
        if (error.findSqlState() == "23505") {
            throw ApiException(HttpStatusCode.Conflict, code, message)
        }
        throw error
    }

    private fun Throwable.findSqlState(): String? = generateSequence(this) { it.cause }
        .filterIsInstance<java.sql.SQLException>()
        .firstOrNull()
        ?.sqlState

    private fun requireOwner(account: Akun) {
        if (account.peran != Peran.OWNER) {
            throw ApiException(HttpStatusCode.Forbidden, "AKSES_DITOLAK", "Hanya Owner yang dapat mengubah data ini.")
        }
    }

    private fun Barang.toResponse() = BarangResponse(
        id = id,
        nama = nama,
        satuan = satuan,
        sku = sku,
        hargaJual = hargaJual,
        stokMinimum = stokMinimum,
        stokSaatIni = stokSaatIni,
        hppRataRata = hppRataRata,
        status = status,
        fotoFileId = fotoFileId,
        kategori = kategori.map { it.toResponse() },
        dibuatOleh = dibuatOleh,
        dibuatPada = dibuatPada,
        diubahPada = diubahPada,
    )

    private fun Kategori.toResponse() = KategoriResponse(id, nama, jumlahBarang)

    private fun Supplier.toResponse() = SupplierResponse(id, nama, link, catatan, status, jumlahBarang)

    private fun HargaBeliSupplier.toResponse() =
        HargaBeliSupplierResponse(barangId, supplierId, namaSupplier, hargaPerItem, diperbaruiPada)

    private fun decodeBarangCursor(value: String, sort: String): BarangCursor {
        val decoded = decode(value).split('|', limit = 2)
        if (decoded.size != 2) invalid("CURSOR_TIDAK_VALID", "Cursor tidak valid.")
        val id = runCatching { UUID.fromString(decoded[1]) }.getOrNull()
            ?: invalid("CURSOR_TIDAK_VALID", "Cursor tidak valid.")
        val sortAndKey = decoded[0].split('~', limit = 2)
        if (sortAndKey.size != 2 || sortAndKey[0] != sort) {
            invalid("CURSOR_TIDAK_VALID", "Cursor tidak valid.")
        }
        if (sort == SORT_STOK_TERENDAH && sortAndKey[1].toIntOrNull() == null) {
            invalid("CURSOR_TIDAK_VALID", "Cursor tidak valid.")
        }
        if (sort == SORT_TERLARIS &&
            (sortAndKey[1].substringBefore(':').toIntOrNull() == null || sortAndKey[1].substringAfter(':', "").isEmpty())
        ) {
            invalid("CURSOR_TIDAK_VALID", "Cursor tidak valid.")
        }
        return BarangCursor(sortAndKey[1], id)
    }

    private fun encodeBarangCursor(cursor: BarangCursor, sort: String): String {
        val key = if (sort == SORT_STOK_TERENDAH) cursor.sortKey else cursor.sortKey.lowercase()
        return encode("$sort~$key|${cursor.id}")
    }

    private fun decodeUuidCursor(value: String): UUID = runCatching { UUID.fromString(decode(value)) }.getOrNull()
        ?: invalid("CURSOR_TIDAK_VALID", "Cursor tidak valid.")

    private fun encodeUuidCursor(value: UUID) = encode(value.toString())

    private fun encode(value: String): String = Base64.getUrlEncoder().withoutPadding()
        .encodeToString(value.toByteArray(StandardCharsets.UTF_8))

    private fun decode(value: String): String = try {
        String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8)
    } catch (_: IllegalArgumentException) {
        invalid("CURSOR_TIDAK_VALID", "Cursor tidak valid.")
    }

    private fun invalid(code: String, message: String): Nothing =
        throw ApiException(HttpStatusCode.BadRequest, code, message)

    private fun notFound(code: String, message: String): Nothing =
        throw ApiException(HttpStatusCode.NotFound, code, message)

    companion object {
        private const val DEFAULT_PAGE_SIZE = 50
        private const val MAX_PAGE_SIZE = 100
        private const val MAX_NAMA_BARANG = 120
        private const val MAX_SATUAN = 30
        private const val MAX_SKU = 100
        private const val MAX_NAMA_KATEGORI = 80
        private const val MAX_NAMA_SUPPLIER = 120
        private const val MAX_LINK = 2048
        private const val MAX_CATATAN = 500
        private const val STATUS_AKTIF = "AKTIF"
        private const val STATUS_DIARSIPKAN = "DIARSIPKAN"
        private const val STATUS_NONAKTIF = "NONAKTIF"
        private const val SORT_NAMA = "NAMA"
        private const val SORT_STOK_TERENDAH = "STOK_TERENDAH"
        private const val SORT_TERLARIS = "TERLARIS"
    }
}
