package com.aura.server.catalog

import com.aura.server.auth.Akun
import com.aura.server.auth.Peran
import com.aura.server.auth.StatusAkun
import com.aura.server.plugins.ApiException
import io.ktor.http.HttpStatusCode
import java.nio.file.Files
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CatalogServiceTest {
    @Test
    fun `cashier cannot request archived goods or change catalog`() {
        val repository = TestCatalogRepository()
        val service = CatalogService(repository)
        val cashier = account(Peran.KASIR)

        assertEquals(
            HttpStatusCode.Forbidden,
            assertFailsWith<ApiException> {
                service.listBarang(cashier, null, null, "DIARSIPKAN", null, null, null)
            }.status,
        )
        assertEquals(
            HttpStatusCode.Forbidden,
            assertFailsWith<ApiException> {
                service.createSupplier(cashier, SupplierCreateRequest(UUID.randomUUID(), "Supplier"))
            }.status,
        )
        assertEquals(0, repository.calls)
    }

    @Test
    fun `supplier link must be a valid http url`() {
        val repository = TestCatalogRepository()
        val service = CatalogService(repository)

        val error = assertFailsWith<ApiException> {
            service.createSupplier(
                account(Peran.OWNER),
                SupplierCreateRequest(UUID.randomUUID(), "Supplier", "javascript:alert(1)"),
            )
        }

        assertEquals(HttpStatusCode.BadRequest, error.status)
        assertEquals("LINK_SUPPLIER_TIDAK_VALID", error.kode)
        assertEquals(0, repository.calls)
    }

    @Test
    fun `uploaded file is restricted to its store and bounded by the contract size`() {
        val repository = TestCatalogRepository()
        val service = CatalogService(repository)
        val directory = Files.createTempDirectory("aura-catalog-test")
        val fileService = CatalogFileService(service, CatalogFileStore(directory))
        val owner = account(Peran.OWNER)
        val content = byteArrayOf(1, 2, 3, 4)

        try {
            val uploaded = fileService.upload(owner, "image/png", content)
            val downloaded = fileService.download(owner, uploaded.fileId)
            assertEquals(content.toList(), downloaded.second.toList())

            val otherStore = owner.copy(idToko = "STORE002")
            assertEquals(
                HttpStatusCode.NotFound,
                assertFailsWith<ApiException> { fileService.download(otherStore, uploaded.fileId) }.status,
            )
            assertEquals(
                HttpStatusCode.PayloadTooLarge,
                assertFailsWith<ApiException> {
                    fileService.upload(owner, "image/png", ByteArray((CatalogFileService.MAX_FILE_BYTES + 1).toInt()))
                }.status,
            )
        } finally {
            Files.list(directory).use { paths -> paths.forEach(Files::deleteIfExists) }
            Files.deleteIfExists(directory)
        }
    }

    private fun account(role: Peran) = Akun(
        id = UUID.randomUUID(),
        idToko = "STORE001",
        email = if (role == Peran.OWNER) "owner@example.test" else null,
        nama = if (role == Peran.OWNER) "Owner" else "Kasir",
        namaNormalisasi = if (role == Peran.OWNER) "owner" else "kasir",
        peran = role,
        hash = "hash",
        salt = "salt",
        status = StatusAkun.AKTIF,
        wajibGantiSandi = false,
        nomorHp = null,
        fotoFileId = null,
        gagalLogin = 0,
        terkunciSampai = null,
        terakhirLogin = null,
    )

    private class TestCatalogRepository : CatalogRepository {
        var calls = 0
        private val files = mutableMapOf<UUID, FileMetadata>()

        override fun listBarang(storeId: String, query: String?, categoryId: UUID?, status: String, sort: String, limit: Int, cursor: BarangCursor?): BarangPage =
            unused()

        override fun findBarang(storeId: String, barangId: UUID): Barang? = unused()

        override fun findBarangDetail(storeId: String, barangId: UUID): BarangDetail? = unused()

        override fun createBarang(account: Akun, request: BarangCreateRequest): BarangWriteResult = unused()

        override fun updateBarang(account: Akun, barangId: UUID, request: BarangUpdateRequest): Barang? = unused()

        override fun deleteBarang(account: Akun, barangId: UUID): String? = unused()

        override fun findFile(storeId: String, fileId: UUID): FileMetadata? {
            calls++
            return files[fileId]?.takeIf { it.idToko == storeId }
        }

        override fun listKategori(storeId: String): List<Kategori> = unused()

        override fun createKategori(account: Akun, request: KategoriCreateRequest): Kategori = unused()

        override fun updateKategori(account: Akun, kategoriId: UUID, request: KategoriUpdateRequest): Kategori? = unused()

        override fun deleteKategori(account: Akun, kategoriId: UUID): Boolean = unused()

        override fun listSupplier(storeId: String, query: String?, status: String?, limit: Int, cursor: UUID?): SupplierPage =
            unused()

        override fun findSupplier(storeId: String, supplierId: UUID): SupplierDetail? = unused()

        override fun createSupplier(account: Akun, request: SupplierCreateRequest): Supplier = unused()

        override fun updateSupplier(account: Akun, supplierId: UUID, request: SupplierUpdateRequest): Supplier? = unused()

        override fun setSupplierStatus(account: Akun, supplierId: UUID, status: String): Supplier? = unused()

        override fun deleteSupplier(account: Akun, supplierId: UUID): String? = unused()

        override fun createFile(metadata: FileMetadata) {
            calls++
            files[metadata.id] = metadata
        }

        private fun <T> unused(): T {
            calls++
            error("Unexpected repository call in this test.")
        }
    }
}
