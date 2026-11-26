package com.aura.server.catalog

import com.aura.server.auth.Akun
import com.aura.server.db.BarangKategoriTable
import com.aura.server.db.BarangTable
import com.aura.server.db.FileTable
import com.aura.server.db.HargaBeliSupplierTable
import com.aura.server.db.KategoriTable
import com.aura.server.db.LogAktivitasTable
import com.aura.server.db.PergerakanStokTable
import com.aura.server.db.SupplierTable
import com.aura.server.db.TokoTable
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.util.UUID
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

internal class ExposedCatalogRepository : CatalogRepository {
    override fun listBarang(
        storeId: String,
        query: String?,
        categoryId: UUID?,
        status: String,
        sort: String,
        limit: Int,
        cursor: BarangCursor?,
    ): BarangPage = transaction {
        var condition: Op<Boolean> = (BarangTable.idToko eq storeId) and (BarangTable.status eq status)
        if (!query.isNullOrBlank()) {
            val pattern = "%${query.trim().lowercase()}%"
            condition = condition and (
                (BarangTable.nama.lowerCase() like pattern) or
                    (BarangTable.sku.lowerCase() like pattern)
                )
        }
        if (categoryId != null) {
            val barangIds = BarangKategoriTable.selectAll().where {
                (BarangKategoriTable.idToko eq storeId) and (BarangKategoriTable.kategoriId eq categoryId.toKotlinUuid())
            }.map { it[BarangKategoriTable.barangId] }
            if (barangIds.isEmpty()) return@transaction BarangPage(emptyList(), null)
            condition = condition and (BarangTable.id inList barangIds)
        }
        if (cursor != null && sort != SORT_TERLARIS) {
            val id = cursor.id.toKotlinUuid()
            condition = condition and if (sort == SORT_STOK_TERENDAH) {
                val stock = cursor.sortKey.toIntOrNull() ?: return@transaction BarangPage(emptyList(), null)
                (BarangTable.stokSaatIni greater stock) or
                    ((BarangTable.stokSaatIni eq stock) and (BarangTable.id greater id))
            } else {
                val name = BarangTable.nama.lowerCase()
                (name greater cursor.sortKey) or ((name eq cursor.sortKey) and (BarangTable.id greater id))
            }
        }

        if (sort == SORT_TERLARIS) {
            val movements = PergerakanStokTable.selectAll().where {
                (PergerakanStokTable.idToko eq storeId) and
                    (PergerakanStokTable.jenis eq MOVEMENT_KELUAR) and
                    (PergerakanStokTable.sumber eq SOURCE_PENJUALAN)
            }.toList()
            val soldCounts = movements.groupBy { it[PergerakanStokTable.barangId] }
                .mapValues { (_, rows) -> rows.sumOf { it[PergerakanStokTable.jumlah] } }
            val allRows = BarangTable.selectAll().where { condition }.toList()
                .sortedWith(
                    compareByDescending<ResultRow> { soldCounts[it[BarangTable.id]] ?: 0 }
                        .thenBy { it[BarangTable.nama].lowercase() }
                        .thenBy { it[BarangTable.id] },
                )
            val afterCursor = cursor?.let { current ->
                val count = current.sortKey.substringBefore(':').toIntOrNull()
                    ?: return@transaction BarangPage(emptyList(), null)
                val name = current.sortKey.substringAfter(':', "")
                allRows.filter { row ->
                    val sold = soldCounts[row[BarangTable.id]] ?: 0
                    val rowName = row[BarangTable.nama].lowercase()
                    sold < count ||
                        (sold == count && rowName > name) ||
                        (sold == count && rowName == name && row[BarangTable.id].toJavaUuid() > current.id)
                }
            } ?: allRows
            val rows = afterCursor.take(limit + 1)
            val pageRows = rows.take(limit)
            val page = pageRows.map { it.toBarang().withCategories(storeId) }
            val last = pageRows.lastOrNull()
            val next = if (rows.size > limit && last != null) {
                val sold = soldCounts[last[BarangTable.id]] ?: 0
                BarangCursor("$sold:${last[BarangTable.nama].lowercase()}", last[BarangTable.id].toJavaUuid())
            } else {
                null
            }
            return@transaction BarangPage(page, next)
        }

        val ordering = if (sort == SORT_STOK_TERENDAH) {
            arrayOf(BarangTable.stokSaatIni to SortOrder.ASC, BarangTable.id to SortOrder.ASC)
        } else {
            arrayOf(BarangTable.nama.lowerCase() to SortOrder.ASC, BarangTable.id to SortOrder.ASC)
        }
        val rows = BarangTable.selectAll().where { condition }.orderBy(*ordering).limit(limit + 1).toList()
        val hasMore = rows.size > limit
        val page = rows.take(limit).map { it.toBarang().withCategories(storeId) }
        val last = page.lastOrNull()
        val next = if (hasMore && last != null) {
            BarangCursor(
                sortKey = if (sort == SORT_STOK_TERENDAH) last.stokSaatIni.toString() else last.nama.lowercase(),
                id = last.id,
            )
        } else {
            null
        }
        BarangPage(page, next)
    }

    override fun findBarang(storeId: String, barangId: UUID): Barang? = transaction {
        BarangTable.selectAll().where {
            (BarangTable.idToko eq storeId) and (BarangTable.id eq barangId.toKotlinUuid())
        }.singleOrNull()?.toBarang()?.withCategories(storeId)
    }

    override fun findBarangDetail(storeId: String, barangId: UUID): BarangDetail? = transaction {
        val row = BarangTable.selectAll().where {
            (BarangTable.idToko eq storeId) and (BarangTable.id eq barangId.toKotlinUuid())
        }.singleOrNull() ?: return@transaction null
        val barang = row.toBarang().withCategories(storeId)
        val prices = HargaBeliSupplierTable.selectAll().where {
            (HargaBeliSupplierTable.idToko eq storeId) and
                (HargaBeliSupplierTable.barangId eq barangId.toKotlinUuid())
        }.mapNotNull { it.toHargaBeliSupplier() }
        val movements = PergerakanStokTable.selectAll().where {
            (PergerakanStokTable.idToko eq storeId) and
                (PergerakanStokTable.barangId eq barangId.toKotlinUuid())
        }.toList()
        BarangDetail(
            barang = barang,
            hargaBeliSupplier = prices,
            kartuStokRingkas = PergerakanStokRingkas(
                jumlahPergerakan = movements.size,
                stokMasuk = movements.filter { it[PergerakanStokTable.jenis] == MOVEMENT_MASUK }
                    .sumOf { it[PergerakanStokTable.jumlah] },
                stokKeluar = movements.filter { it[PergerakanStokTable.jenis] == MOVEMENT_KELUAR }
                    .sumOf { it[PergerakanStokTable.jumlah] },
            ),
        )
    }

    override fun createBarang(account: Akun, request: BarangCreateRequest): BarangWriteResult = transaction {
        val now = System.currentTimeMillis()
        val initialHpp = if (request.stokAwal > 0) request.hargaBeliAwal ?: 0L else 0L
        BarangTable.insert {
            it[id] = request.id.toKotlinUuid()
            it[idToko] = account.idToko
            it[nama] = request.nama.trim()
            it[satuan] = request.satuan.trim()
            it[sku] = request.sku?.trim()?.takeIf(String::isNotEmpty)
            it[hargaJual] = request.hargaJual
            it[stokMinimum] = request.stokMinimum
            it[stokSaatIni] = request.stokAwal
            it[hppRataRata] = initialHpp
            it[status] = STATUS_AKTIF
            it[fotoFileId] = request.fotoFileId?.toKotlinUuid()
            it[dibuatOleh] = account.id.toKotlinUuid()
            it[dibuatPada] = now
            it[diubahPada] = now
        }
        request.kategoriIds.orEmpty().distinct().forEach { categoryId ->
            BarangKategoriTable.insert {
                it[idToko] = account.idToko
                it[barangId] = request.id.toKotlinUuid()
                it[kategoriId] = categoryId.toKotlinUuid()
            }
        }
        if (request.supplierId != null && request.hargaBeliAwal != null) {
            HargaBeliSupplierTable.insert {
                it[idToko] = account.idToko
                it[barangId] = request.id.toKotlinUuid()
                it[supplierId] = request.supplierId.toKotlinUuid()
                it[hargaPerItem] = request.hargaBeliAwal
                it[diperbaruiPada] = now
            }
        }
        val movement = if (request.stokAwal > 0) {
            val movementId = UUID.randomUUID()
            PergerakanStokTable.insert {
                it[id] = movementId.toKotlinUuid()
                it[idToko] = account.idToko
                it[barangId] = request.id.toKotlinUuid()
                it[akunId] = account.id.toKotlinUuid()
                it[jenis] = MOVEMENT_MASUK
                it[sumber] = SOURCE_STOK_AWAL
                it[jumlah] = request.stokAwal
                it[stokSebelum] = 0
                it[stokSesudah] = request.stokAwal
                it[referensiId] = request.id.toKotlinUuid()
                it[jenisReferensi] = REFERENCE_BARANG
                it[dibuatPada] = now
            }
            PergerakanStokResponse(
                id = movementId,
                jenis = MOVEMENT_MASUK,
                sumber = SOURCE_STOK_AWAL,
                jumlah = request.stokAwal,
                stokSebelum = 0,
                stokSesudah = request.stokAwal,
                dibuatPada = now,
            )
        } else {
            null
        }
        log(account, "BARANG_DIBUAT", request.id, now)
        val storeBalance = TokoTable.selectAll().where { TokoTable.idToko eq account.idToko }
            .single()[TokoTable.saldoKumulatif]
        BarangWriteResult(
            barang = findBarang(account.idToko, request.id)!!,
            pergerakan = movement,
            saldoKumulatif = storeBalance,
        )
    }

    override fun updateBarang(account: Akun, barangId: UUID, request: BarangUpdateRequest): Barang? = transaction {
        val existing = BarangTable.selectAll().where {
            (BarangTable.idToko eq account.idToko) and (BarangTable.id eq barangId.toKotlinUuid())
        }.singleOrNull() ?: return@transaction null
        if (existing[BarangTable.status] == STATUS_DIARSIPKAN) return@transaction null
        BarangTable.update({
            (BarangTable.idToko eq account.idToko) and (BarangTable.id eq barangId.toKotlinUuid())
        }) {
            request.nama?.let { value -> it[nama] = value.trim() }
            request.satuan?.let { value -> it[satuan] = value.trim() }
            request.sku?.let { value -> it[sku] = value.trim().takeIf(String::isNotEmpty) }
            request.hargaJual?.let { value -> it[hargaJual] = value }
            request.stokMinimum?.let { value -> it[stokMinimum] = value }
            request.fotoFileId?.let { value -> it[fotoFileId] = value.toKotlinUuid() }
            it[diubahPada] = System.currentTimeMillis()
        }
        request.kategoriIds?.let { ids ->
            BarangKategoriTable.deleteWhere {
                (BarangKategoriTable.idToko eq account.idToko) and
                    (BarangKategoriTable.barangId eq barangId.toKotlinUuid())
            }
            ids.distinct().forEach { categoryId ->
                BarangKategoriTable.insert {
                    it[idToko] = account.idToko
                    it[BarangKategoriTable.barangId] = barangId.toKotlinUuid()
                    it[kategoriId] = categoryId.toKotlinUuid()
                }
            }
        }
        log(account, "BARANG_DIUBAH", barangId, System.currentTimeMillis())
        findBarang(account.idToko, barangId)
    }

    override fun deleteBarang(account: Akun, barangId: UUID): String? = transaction {
        val existing = BarangTable.selectAll().where {
            (BarangTable.idToko eq account.idToko) and (BarangTable.id eq barangId.toKotlinUuid())
        }.singleOrNull() ?: return@transaction null
        if (existing[BarangTable.status] == STATUS_DIARSIPKAN) return@transaction STATUS_DIARSIPKAN
        val hasHistory = PergerakanStokTable.selectAll().where {
            (PergerakanStokTable.idToko eq account.idToko) and
                (PergerakanStokTable.barangId eq barangId.toKotlinUuid())
        }.limit(1).count() > 0
        val now = System.currentTimeMillis()
        if (hasHistory) {
            BarangTable.update({
                (BarangTable.idToko eq account.idToko) and (BarangTable.id eq barangId.toKotlinUuid())
            }) {
                it[status] = STATUS_DIARSIPKAN
                it[diubahPada] = now
            }
            log(account, "BARANG_DIARSIPKAN", barangId, now)
            STATUS_DIARSIPKAN
        } else {
            log(account, "BARANG_DIHAPUS_PERMANEN", barangId, now)
            BarangTable.deleteWhere {
                (BarangTable.idToko eq account.idToko) and (BarangTable.id eq barangId.toKotlinUuid())
            }
            STATUS_DIHAPUS_PERMANEN
        }
    }

    override fun listKategori(storeId: String): List<Kategori> = transaction {
        KategoriTable.selectAll().where { KategoriTable.idToko eq storeId }
            .orderBy(KategoriTable.nama to SortOrder.ASC)
            .map { row ->
                val id = row[KategoriTable.id]
                val count = BarangKategoriTable.selectAll().where {
                    (BarangKategoriTable.idToko eq storeId) and (BarangKategoriTable.kategoriId eq id)
                }.count().toInt()
                row.toKategori(count)
            }
    }

    override fun createKategori(account: Akun, request: KategoriCreateRequest): Kategori = transaction {
        val now = System.currentTimeMillis()
        KategoriTable.insert {
            it[id] = request.id.toKotlinUuid()
            it[idToko] = account.idToko
            it[nama] = request.nama.trim()
            it[dibuatPada] = now
        }
        log(account, "KATEGORI_DIBUAT", request.id, now, REFERENCE_KATEGORI)
        KategoriTable.selectAll().where {
            (KategoriTable.idToko eq account.idToko) and (KategoriTable.id eq request.id.toKotlinUuid())
        }
            .single().toKategori(0)
    }

    override fun updateKategori(account: Akun, kategoriId: UUID, request: KategoriUpdateRequest): Kategori? = transaction {
        val updated = KategoriTable.update({
            (KategoriTable.idToko eq account.idToko) and (KategoriTable.id eq kategoriId.toKotlinUuid())
        }) {
            it[nama] = request.nama.trim()
        }
        if (updated == 0) return@transaction null
        log(account, "KATEGORI_DIUBAH", kategoriId, System.currentTimeMillis(), REFERENCE_KATEGORI)
        val count = BarangKategoriTable.selectAll().where {
            (BarangKategoriTable.idToko eq account.idToko) and (BarangKategoriTable.kategoriId eq kategoriId.toKotlinUuid())
        }.count().toInt()
        KategoriTable.selectAll().where {
            (KategoriTable.idToko eq account.idToko) and (KategoriTable.id eq kategoriId.toKotlinUuid())
        }.single().toKategori(count)
    }

    override fun deleteKategori(account: Akun, kategoriId: UUID): Boolean = transaction {
        val deleted = KategoriTable.deleteWhere {
            (KategoriTable.idToko eq account.idToko) and (KategoriTable.id eq kategoriId.toKotlinUuid())
        }
        if (deleted > 0) log(account, "KATEGORI_DIHAPUS", kategoriId, System.currentTimeMillis(), REFERENCE_KATEGORI)
        deleted > 0
    }

    override fun listSupplier(
        storeId: String,
        query: String?,
        status: String?,
        limit: Int,
        cursor: UUID?,
    ): SupplierPage = transaction {
        var condition: Op<Boolean> = SupplierTable.idToko eq storeId
        status?.let { condition = condition and (SupplierTable.status eq it) }
        if (!query.isNullOrBlank()) {
            condition = condition and (SupplierTable.nama.lowerCase() like "%${query.trim().lowercase()}%")
        }
        cursor?.let { condition = condition and (SupplierTable.id greater it.toKotlinUuid()) }
        val rows = SupplierTable.selectAll().where { condition }
            .orderBy(SupplierTable.id to SortOrder.ASC)
            .limit(limit + 1)
            .toList()
        val page = rows.take(limit).map { it.toSupplier().withBarangCount(storeId) }
        SupplierPage(page, if (rows.size > limit) page.lastOrNull()?.id else null)
    }

    override fun findSupplier(storeId: String, supplierId: UUID): SupplierDetail? = transaction {
        val supplier = SupplierTable.selectAll().where {
            (SupplierTable.idToko eq storeId) and (SupplierTable.id eq supplierId.toKotlinUuid())
        }.singleOrNull()?.toSupplier() ?: return@transaction null
        val prices = HargaBeliSupplierTable.selectAll().where {
            (HargaBeliSupplierTable.idToko eq storeId) and
                (HargaBeliSupplierTable.supplierId eq supplierId.toKotlinUuid())
        }.mapNotNull { it.toHargaBeliSupplier() }
        val barang = prices.mapNotNull { findBarang(storeId, it.barangId) }
        SupplierDetail(supplier.withBarangCount(storeId), barang, prices)
    }

    override fun createSupplier(account: Akun, request: SupplierCreateRequest): Supplier = transaction {
        val now = System.currentTimeMillis()
        SupplierTable.insert {
            it[id] = request.id.toKotlinUuid()
            it[idToko] = account.idToko
            it[nama] = request.nama.trim()
            it[link] = request.link?.trim()
            it[catatan] = request.catatan?.trim()
            it[status] = STATUS_AKTIF
            it[dibuatPada] = now
        }
        log(account, "SUPPLIER_DIBUAT", request.id, now, REFERENCE_SUPPLIER)
        SupplierTable.selectAll().where {
            (SupplierTable.idToko eq account.idToko) and (SupplierTable.id eq request.id.toKotlinUuid())
        }.single()
            .toSupplier().withBarangCount(account.idToko)
    }

    override fun updateSupplier(account: Akun, supplierId: UUID, request: SupplierUpdateRequest): Supplier? = transaction {
        val updated = SupplierTable.update({
            (SupplierTable.idToko eq account.idToko) and (SupplierTable.id eq supplierId.toKotlinUuid())
        }) {
            request.nama?.let { value -> it[nama] = value.trim() }
            request.link?.let { value -> it[link] = value.trim() }
            request.catatan?.let { value -> it[catatan] = value.trim() }
        }
        if (updated == 0) return@transaction null
        log(account, "SUPPLIER_DIUBAH", supplierId, System.currentTimeMillis(), REFERENCE_SUPPLIER)
        SupplierTable.selectAll().where {
            (SupplierTable.idToko eq account.idToko) and (SupplierTable.id eq supplierId.toKotlinUuid())
        }.single()
            .toSupplier().withBarangCount(account.idToko)
    }

    override fun setSupplierStatus(account: Akun, supplierId: UUID, status: String): Supplier? = transaction {
        val updated = SupplierTable.update({
            (SupplierTable.idToko eq account.idToko) and (SupplierTable.id eq supplierId.toKotlinUuid())
        }) {
            it[SupplierTable.status] = status
        }
        if (updated == 0) return@transaction null
        log(
            account,
            if (status == STATUS_AKTIF) "SUPPLIER_DIAKTIFKAN" else "SUPPLIER_DINONAKTIFKAN",
            supplierId,
            System.currentTimeMillis(),
            REFERENCE_SUPPLIER,
        )
        SupplierTable.selectAll().where {
            (SupplierTable.idToko eq account.idToko) and (SupplierTable.id eq supplierId.toKotlinUuid())
        }.single()
            .toSupplier().withBarangCount(account.idToko)
    }

    override fun deleteSupplier(account: Akun, supplierId: UUID): String? = transaction {
        val supplier = SupplierTable.selectAll().where {
            (SupplierTable.idToko eq account.idToko) and (SupplierTable.id eq supplierId.toKotlinUuid())
        }.singleOrNull() ?: return@transaction null
        val inUse = HargaBeliSupplierTable.selectAll().where {
            (HargaBeliSupplierTable.idToko eq account.idToko) and
                (HargaBeliSupplierTable.supplierId eq supplierId.toKotlinUuid())
        }.limit(1).count() > 0
        if (inUse) return@transaction "IN_USE"
        log(account, "SUPPLIER_DIHAPUS", supplier[SupplierTable.id].toJavaUuid(), System.currentTimeMillis(), REFERENCE_SUPPLIER)
        SupplierTable.deleteWhere {
            (SupplierTable.idToko eq account.idToko) and (SupplierTable.id eq supplierId.toKotlinUuid())
        }
        "DELETED"
    }

    override fun createFile(metadata: FileMetadata) {
        transaction {
            FileTable.insert {
                it[id] = metadata.id.toKotlinUuid()
                it[idToko] = metadata.idToko
                it[mimeType] = metadata.mimeType
                it[ukuran] = metadata.ukuran
                it[dibuatPada] = metadata.dibuatPada
            }
        }
    }

    override fun findFile(storeId: String, fileId: UUID): FileMetadata? = transaction {
        FileTable.selectAll().where {
            (FileTable.idToko eq storeId) and (FileTable.id eq fileId.toKotlinUuid())
        }.singleOrNull()?.toFileMetadata()
    }

    private fun ResultRow.toBarang(): Barang = Barang(
        id = this[BarangTable.id].toJavaUuid(),
        idToko = this[BarangTable.idToko],
        nama = this[BarangTable.nama],
        satuan = this[BarangTable.satuan],
        sku = this[BarangTable.sku],
        hargaJual = this[BarangTable.hargaJual],
        stokMinimum = this[BarangTable.stokMinimum],
        stokSaatIni = this[BarangTable.stokSaatIni],
        hppRataRata = this[BarangTable.hppRataRata],
        status = this[BarangTable.status],
        fotoFileId = this[BarangTable.fotoFileId]?.toJavaUuid(),
        dibuatOleh = this[BarangTable.dibuatOleh].toJavaUuid(),
        dibuatPada = this[BarangTable.dibuatPada],
        diubahPada = this[BarangTable.diubahPada],
    )

    private fun Barang.withCategories(storeId: String): Barang {
        val categoryIds = BarangKategoriTable.selectAll().where {
            (BarangKategoriTable.idToko eq storeId) and
                (BarangKategoriTable.barangId eq id.toKotlinUuid())
        }.map { it[BarangKategoriTable.kategoriId] }
        if (categoryIds.isEmpty()) return this
        val categories = KategoriTable.selectAll().where {
            (KategoriTable.idToko eq storeId) and (KategoriTable.id inList categoryIds)
        }.orderBy(KategoriTable.nama to SortOrder.ASC).map { it.toKategori(0) }
        return copy(kategori = categories)
    }

    private fun ResultRow.toKategori(count: Int): Kategori = Kategori(
        id = this[KategoriTable.id].toJavaUuid(),
        idToko = this[KategoriTable.idToko],
        nama = this[KategoriTable.nama],
        jumlahBarang = count,
        dibuatPada = this[KategoriTable.dibuatPada],
    )

    private fun ResultRow.toSupplier(): Supplier = Supplier(
        id = this[SupplierTable.id].toJavaUuid(),
        idToko = this[SupplierTable.idToko],
        nama = this[SupplierTable.nama],
        link = this[SupplierTable.link],
        catatan = this[SupplierTable.catatan],
        status = this[SupplierTable.status],
        dibuatPada = this[SupplierTable.dibuatPada],
    )

    private fun Supplier.withBarangCount(storeId: String): Supplier = copy(
        jumlahBarang = HargaBeliSupplierTable.selectAll().where {
            (HargaBeliSupplierTable.idToko eq storeId) and
                (HargaBeliSupplierTable.supplierId eq id.toKotlinUuid())
        }.count().toInt(),
    )

    private fun ResultRow.toHargaBeliSupplier(): HargaBeliSupplier? {
        val supplier = SupplierTable.selectAll().where {
            (SupplierTable.idToko eq this[HargaBeliSupplierTable.idToko]) and
                (SupplierTable.id eq this[HargaBeliSupplierTable.supplierId])
        }.singleOrNull() ?: return null
        return HargaBeliSupplier(
            barangId = this[HargaBeliSupplierTable.barangId].toJavaUuid(),
            supplierId = this[HargaBeliSupplierTable.supplierId].toJavaUuid(),
            namaSupplier = supplier[SupplierTable.nama],
            hargaPerItem = this[HargaBeliSupplierTable.hargaPerItem],
            diperbaruiPada = this[HargaBeliSupplierTable.diperbaruiPada],
        )
    }

    private fun ResultRow.toFileMetadata(): FileMetadata = FileMetadata(
        id = this[FileTable.id].toJavaUuid(),
        idToko = this[FileTable.idToko],
        mimeType = this[FileTable.mimeType],
        ukuran = this[FileTable.ukuran],
        dibuatPada = this[FileTable.dibuatPada],
    )

    private fun log(
        account: Akun,
        action: String,
        referenceId: UUID,
        at: Long,
        referenceType: String = REFERENCE_BARANG,
    ) {
        LogAktivitasTable.insert {
            it[id] = UUID.randomUUID().toKotlinUuid()
            it[idToko] = account.idToko
            it[akunId] = account.id.toKotlinUuid()
            it[namaAkunSnapshot] = account.nama.take(80)
            it[kategori] = "BARANG"
            it[aksi] = action
            it[referensiId] = referenceId.toKotlinUuid()
            it[jenisReferensi] = referenceType
            it[dibuatPada] = at
        }
    }

    companion object {
        const val SORT_STOK_TERENDAH = "STOK_TERENDAH"
        const val SORT_TERLARIS = "TERLARIS"
        const val STATUS_AKTIF = "AKTIF"
        const val STATUS_DIARSIPKAN = "DIARSIPKAN"
        const val STATUS_DIHAPUS_PERMANEN = "DIHAPUS_PERMANEN"
        const val MOVEMENT_MASUK = "MASUK"
        const val MOVEMENT_KELUAR = "KELUAR"
        const val SOURCE_STOK_AWAL = "STOK_AWAL"
        const val SOURCE_PENJUALAN = "PENJUALAN"
        const val REFERENCE_BARANG = "BARANG"
        const val REFERENCE_KATEGORI = "KATEGORI"
        const val REFERENCE_SUPPLIER = "SUPPLIER"
    }
}

private const val SORT_STOK_TERENDAH = ExposedCatalogRepository.SORT_STOK_TERENDAH
private const val SORT_TERLARIS = ExposedCatalogRepository.SORT_TERLARIS
private const val STATUS_AKTIF = ExposedCatalogRepository.STATUS_AKTIF
private const val STATUS_DIARSIPKAN = ExposedCatalogRepository.STATUS_DIARSIPKAN
private const val STATUS_DIHAPUS_PERMANEN = ExposedCatalogRepository.STATUS_DIHAPUS_PERMANEN
private const val MOVEMENT_MASUK = ExposedCatalogRepository.MOVEMENT_MASUK
private const val MOVEMENT_KELUAR = ExposedCatalogRepository.MOVEMENT_KELUAR
private const val SOURCE_STOK_AWAL = ExposedCatalogRepository.SOURCE_STOK_AWAL
private const val SOURCE_PENJUALAN = ExposedCatalogRepository.SOURCE_PENJUALAN
private const val REFERENCE_BARANG = ExposedCatalogRepository.REFERENCE_BARANG
