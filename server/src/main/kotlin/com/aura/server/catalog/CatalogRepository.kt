package com.aura.server.catalog

import com.aura.server.auth.Akun
import java.util.UUID

internal data class BarangCursor(
    val sortKey: String,
    val id: UUID,
)

internal data class BarangPage(
    val data: List<Barang>,
    val nextCursor: BarangCursor?,
)

internal data class SupplierPage(
    val data: List<Supplier>,
    val nextCursor: UUID?,
)

internal data class BarangDetail(
    val barang: Barang,
    val hargaBeliSupplier: List<HargaBeliSupplier>,
    val kartuStokRingkas: PergerakanStokRingkas,
)

internal data class BarangWriteResult(
    val barang: Barang,
    val pergerakan: PergerakanStokResponse?,
    val saldoKumulatif: Long,
)

internal data class SupplierDetail(
    val supplier: Supplier,
    val barang: List<Barang>,
    val hargaBeli: List<HargaBeliSupplier>,
)

internal interface CatalogRepository {
    fun listBarang(
        storeId: String,
        query: String?,
        categoryId: UUID?,
        status: String,
        sort: String,
        limit: Int,
        cursor: BarangCursor?,
    ): BarangPage

    fun findBarang(storeId: String, barangId: UUID): Barang?
    fun findBarangDetail(storeId: String, barangId: UUID): BarangDetail?
    fun createBarang(account: Akun, request: BarangCreateRequest): BarangWriteResult
    fun updateBarang(account: Akun, barangId: UUID, request: BarangUpdateRequest): Barang?
    fun deleteBarang(account: Akun, barangId: UUID): String?
    fun findFile(storeId: String, fileId: UUID): FileMetadata?

    fun listKategori(storeId: String): List<Kategori>
    fun createKategori(account: Akun, request: KategoriCreateRequest): Kategori
    fun updateKategori(account: Akun, kategoriId: UUID, request: KategoriUpdateRequest): Kategori?
    fun deleteKategori(account: Akun, kategoriId: UUID): Boolean

    fun listSupplier(storeId: String, query: String?, status: String?, limit: Int, cursor: UUID?): SupplierPage
    fun findSupplier(storeId: String, supplierId: UUID): SupplierDetail?
    fun createSupplier(account: Akun, request: SupplierCreateRequest): Supplier
    fun updateSupplier(account: Akun, supplierId: UUID, request: SupplierUpdateRequest): Supplier?
    fun setSupplierStatus(account: Akun, supplierId: UUID, status: String): Supplier?
    fun deleteSupplier(account: Akun, supplierId: UUID): String?

    fun createFile(metadata: FileMetadata)
}
