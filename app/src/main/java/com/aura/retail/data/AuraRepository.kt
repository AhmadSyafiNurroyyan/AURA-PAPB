package com.aura.retail.data

import androidx.room.withTransaction
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AuraRepository(private val db: AuraDatabase) {
    private val dao = db.dao()
    private fun id() = UUID.randomUUID().toString()
    private fun hash(value: String): String = MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray()).joinToString("") { "%02x".format(it) }
    private fun todayCode(): String = SimpleDateFormat("yyMMdd", Locale.US).format(Date())

    suspend fun seed() = db.withTransaction {
        if (dao.anyStore() != null) return@withTransaction
        val storeId = "AB12CD34"
        dao.putStore(StoreEntity(storeId, "Toko Berkah Jaya", "Retail / Sembako", 4_850_000L,
            "BCA 1234567890 a.n. Budi Santoso\nMandiri 0987654321 a.n. Budi Santoso"))
        dao.putAccount(AccountEntity("owner-1", storeId, "Budi Santoso", "owner@tokoberkah.com",
            "owner", hash("123456"), "owner", true, "0812-3456-7890"))
        dao.putAccount(AccountEntity("cashier-1", storeId, "Siti Aminah", "",
            "siti.aminah", hash("123456"), "kasir", true, "0813-1111-2222"))
        dao.putAccount(AccountEntity("cashier-2", storeId, "Andi Pratama", "",
            "andi.pratama", hash("123456"), "kasir", true, "0813-3333-4444"))
        listOf(
            ProductEntity("BRG-001","IDM-ST-001","Indomie Soto Kuah","Makanan,Instan","pcs",3500,2800,50,10,142),
            ProductEntity("BRG-002","NBT-KJ-002","Nabati Keju 100g","Makanan","pcs",2000,1500,15,20,88),
            ProductEntity("BRG-003","MNY-BM-003","Minyak Goreng Bimoli 1L","Sembako,Dapur","pouch",18500,15500,0,5,64),
            ProductEntity("BRG-004","TLR-AY-004","Telur Ayam Ras 1kg","Sembako","kg",28000,24500,4,10,55),
            ProductEntity("BRG-005","GLP-KR-005","Gula Pasir Kristal 1kg","Sembako,Dapur","kg",16000,14000,3,8,47),
            ProductEntity("BRG-006","KPK-SP-006","Kopi Kapal Api Spesial","Minuman","pcs",1500,1100,42,10,110)
        ).forEach { dao.putProduct(it) }
        listOf(
            SupplierEntity("SUP-001","Toko Grosir Maju","0812-3456-7890","Pasar Induk Blok B"),
            SupplierEntity("SUP-002","Agen Sembako Jaya","0857-9988-1122","Sembako & Minyak"),
            SupplierEntity("SUP-003","CV Sumber Pangan","0819-0011-2233","Malang")
        ).forEach { dao.putSupplier(it) }
        dao.putAccount(AccountEntity("cashier-3", storeId, "Rina Wati", "",
            "rina.wati", hash("123456"), "kasir", false, "0812-0000-0000"))
        val now = System.currentTimeMillis()
        val initial = listOf(
            TransactionEntity("INV-261117-0003",now-86_400_000,"Siti Aminah","Tunai",17_500,"Selesai"),
            TransactionEntity("INV-261117-0002",now-90_000_000,"Andi Pratama","QRIS",52_500,"Perlu Ditinjau","Konflik stok offline",balancePosted=false),
            TransactionEntity("INV-261117-0001",now-100_000_000,"Siti Aminah","Tunai",35_000,"Dibatalkan","Salah input jumlah barang",balancePosted=false)
        )
        initial.forEach { dao.putTransaction(it) }
        dao.putTransactionItems(listOf(
            TransactionItemEntity(invoiceNo="INV-261117-0003",productId="BRG-001",productName="Indomie Soto Kuah",quantity=3,unitPrice=3500,unitCost=2800),
            TransactionItemEntity(invoiceNo="INV-261117-0003",productId="BRG-002",productName="Nabati Keju 100g",quantity=2,unitPrice=2000,unitCost=1500),
            TransactionItemEntity(invoiceNo="INV-261117-0003",productId="BRG-006",productName="Kopi Kapal Api Spesial",quantity=2,unitPrice=1500,unitCost=1100)
        ))
        // Riwayat contoh dari prototype HTML agar layar audit tidak kosong pada first launch.
        dao.putRestock(RestockEntity("RST-261117-0001","BRG-001","SUP-001",40,2750,110000,now-7*86_400_000L,"Restock awal demo"))
        dao.putAdjustment(StockAdjustmentEntity("ADJ-261116-0001","BRG-002",18,15,-3,"Rusak","Kemasan rusak",4500,now-8*86_400_000L))
        dao.putMovement(StockMovementEntity("MOV-001","BRG-001","Indomie Soto Kuah","Masuk - Restock",40,5,45,"RST-261117-0001",now-7*86_400_000L))
        dao.putMovement(StockMovementEntity("MOV-002","BRG-001","Indomie Soto Kuah","Keluar - Penjualan",-2,45,43,"INV-261117-0003",now-7*86_400_000L+4*60*60*1000))
        dao.putMovement(StockMovementEntity("MOV-003","BRG-002","Nabati Keju 100g","Penyesuaian - Rusak",-3,18,15,"ADJ-261116-0001",now-8*86_400_000L))
        dao.putMovement(StockMovementEntity("MOV-004","BRG-002","Nabati Keju 100g","Masuk - Pembatalan",3,12,15,"INV-261116-0045",now-9*86_400_000L))
        dao.putCashRecord(CashRecordEntity("EXP-002","expense","Kustom",350000,"Tagihan Listrik Bulan November",now-8*86_400_000L))
        dao.putCashRecord(CashRecordEntity("EXP-003","expense","Kustom",65000,"Perlengkapan Kebersihan & Kresek",now-8*86_400_000L+3600000))
        dao.putCashRecord(CashRecordEntity("INC-001","income","Modal",500000,"Tambahan Modal Kasir",now-9*86_400_000L,isCapital=true))
        dao.putNotification(NotificationEntity(id(), "Stok Nabati Keju menipis", "Sisa stok berada di bawah batas minimum.", "stock", now))
        dao.putNotification(NotificationEntity(id(), "Pengajuan pembatalan transaksi", "INV-261117-0002 menunggu persetujuan Owner.", "transaction", now-1000))
        log("system", "Inisialisasi data", "Data demo AURA berhasil disiapkan")
    }

    private suspend fun log(actor: String, action: String, detail: String) {
        dao.putActivity(ActivityLogEntity(id(), actor, action, detail, System.currentTimeMillis()))
    }
    suspend fun loginOwner(email: String, password: String): Boolean = db.withTransaction {
        val account = dao.ownerLogin(email.trim(), hash(password)) ?: return@withTransaction false
        dao.putSession(SessionEntity(1, account.id, account.role, account.name, account.storeId))
        log(account.name, "Login", "Owner masuk ke aplikasi")
        true
    }
    suspend fun loginCashier(storeId: String, username: String, password: String): Boolean = db.withTransaction {
        val account = dao.cashierLogin(storeId.trim(), username.trim(), hash(password)) ?: return@withTransaction false
        dao.putSession(SessionEntity(1, account.id, account.role, account.name, account.storeId))
        log(account.name, "Login", "Kasir masuk ke aplikasi")
        true
    }
    suspend fun registerOwner(name: String, email: String, phone: String, password: String,
                              storeName: String, businessType: String, openingBalance: Long): Boolean {
        if (email.isBlank() || password.length < 6 || name.isBlank() || storeName.isBlank()) return false
        // Satu instalasi offline merepresentasikan satu toko. Pendaftaran baru memulai workspace bersih.
        db.clearAllTables()
        return db.withTransaction {
        val storeId = "AU" + (100000..999999).random().toString()
        dao.putStore(StoreEntity(storeId, storeName, businessType, openingBalance))
        val account = AccountEntity("owner-$storeId", storeId, name, email.trim(), "owner",
            hash(password), "owner", true, phone)
        dao.putAccount(account)
        dao.putSession(SessionEntity(1, account.id, "owner", name, storeId))
        log(name, "Registrasi toko", "Membuat toko $storeName")
        true
        }
    }
    suspend fun logout() = db.withTransaction {
        dao.session()?.let { log(it.name, "Logout", "Keluar dari aplikasi") }
        dao.clearSession()
    }
    suspend fun switchAccount(emailOrUsername: String, password: String, storeId: String): Boolean = db.withTransaction {
        val h = hash(password)
        val account = dao.ownerLogin(emailOrUsername, h)
            ?: dao.cashierLogin(storeId, emailOrUsername, h)
            ?: return@withTransaction false
        dao.putSession(SessionEntity(1, account.id, account.role, account.name, account.storeId))
        log(account.name, "Ganti akun", "Sesi berpindah ke ${account.role}")
        true
    }

    suspend fun saveProduct(id: String?, sku: String, name: String, categories: String, unit: String,
                            sellPrice: Long, hpp: Long, stock: Int, minStock: Int) = db.withTransaction {
        require(name.isNotBlank() && sku.isNotBlank() && sellPrice >= 0 && stock >= 0)
        val key = id ?: "BRG-${UUID.randomUUID().toString().take(8).uppercase()}"
        val old = id?.let { dao.product(it) }
        dao.putProduct(ProductEntity(key, sku, name, categories, unit, sellPrice, hpp, stock, minStock,
            old?.sold30Days ?: 0))
        log(dao.session()?.name ?: "Owner", if (old == null) "Tambah barang" else "Edit barang", name)
    }
    suspend fun archiveProduct(productId: String) = db.withTransaction {
        dao.archiveProduct(productId)
        log(dao.session()?.name ?: "Owner", "Arsip barang", productId)
    }
    suspend fun restock(productId: String, quantity: Int, purchasePrice: Long, supplierId: String, note: String) = db.withTransaction {
        require(quantity > 0 && purchasePrice >= 0)
        val p = dao.product(productId) ?: error("Barang tidak ditemukan")
        val store = dao.store() ?: error("Toko tidak ditemukan")
        val newStock = p.stock + quantity
        val newHpp = if (newStock == 0) purchasePrice else
            ((p.stock.toLong() * p.hpp) + (quantity.toLong() * purchasePrice)) / newStock
        val total = quantity.toLong() * purchasePrice
        dao.putProduct(p.copy(stock = newStock, hpp = newHpp))
        val now = System.currentTimeMillis()
        val ref = "RST-${todayCode()}-${now.toString().takeLast(5)}"
        dao.putRestock(RestockEntity(ref, productId, supplierId, quantity, purchasePrice, total, now, note))
        dao.changeBalance(store.id, -total)
        dao.putMovement(StockMovementEntity(id(), productId, p.name, "Restock", quantity, p.stock, newStock, ref, now))
        log(dao.session()?.name ?: "Owner", "Restock barang", "${p.name}: +$quantity")
        pruneRestocks()
    }
    suspend fun adjustStock(productId: String, newStock: Int, reason: String, note: String) = db.withTransaction {
        require(newStock >= 0 && reason.isNotBlank())
        val p = dao.product(productId) ?: error("Barang tidak ditemukan")
        val diff = newStock - p.stock
        val loss = if (diff < 0 && reason in listOf("Rusak", "Hilang", "Kadaluarsa")) (-diff).toLong() * p.hpp else 0L
        val now = System.currentTimeMillis()
        dao.putProduct(p.copy(stock = newStock))
        dao.putAdjustment(StockAdjustmentEntity(id(), productId, p.stock, newStock, diff, reason, note, loss, now))
        dao.putMovement(StockMovementEntity(id(), productId, p.name, "Penyesuaian: $reason", diff, p.stock, newStock, "ADJ-${now}", now))
        log(dao.session()?.name ?: "Owner", "Penyesuaian stok", "${p.name}: ${p.stock} → $newStock ($reason)")
        val store = dao.store()
        if (newStock <= p.minStock) dao.putNotification(NotificationEntity(id(), "Stok ${p.name} perlu diperhatikan",
            "Stok saat ini $newStock ${p.unit}.", "stock", now))
        pruneAdjustments()
    }
    suspend fun recordExpense(category: String, amount: Long, note: String, isPrive: Boolean) = db.withTransaction {
        require(category.isNotBlank() && amount > 0)
        val store = dao.store() ?: error("Toko tidak ditemukan")
        val now = System.currentTimeMillis()
        dao.putCashRecord(CashRecordEntity(id(), "expense", category, amount, note, now, isPrive = isPrive))
        dao.changeBalance(store.id, -amount)
        log(dao.session()?.name ?: "Owner", "Catat pengeluaran", "$category: $amount")
        pruneCash("expense")
    }
    suspend fun recordIncome(category: String, amount: Long, note: String, isCapital: Boolean) = db.withTransaction {
        require(category.isNotBlank() && amount > 0)
        val store = dao.store() ?: error("Toko tidak ditemukan")
        val now = System.currentTimeMillis()
        dao.putCashRecord(CashRecordEntity(id(), "income", category, amount, note, now, isCapital = isCapital))
        dao.changeBalance(store.id, amount)
        log(dao.session()?.name ?: "Owner", "Catat pemasukan", "$category: $amount")
        pruneCash("income")
    }
    suspend fun checkout(cart: List<CartLineData>, method: String, received: Long): String = db.withTransaction {
        require(cart.isNotEmpty())
        val store = dao.store() ?: error("Toko tidak ditemukan")
        val current = cart.map { line ->
            val p = dao.product(line.productId) ?: error("Barang ${line.name} tidak ditemukan")
            require(p.stock >= line.quantity) { "Stok ${p.name} tidak mencukupi. Tersedia ${p.stock}." }
            p
        }
        val total = cart.sumOf { it.unitPrice * it.quantity }
        if (method == "Tunai") require(received >= total) { "Uang diterima kurang dari total tagihan." }
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now
            set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val sequence = dao.transactionCount(startOfDay, calendar.timeInMillis) + 1
        val invoice = "INV-${todayCode()}-${sequence.toString().padStart(4,'0')}"
        val cashier = dao.session()?.name ?: "Kasir"
        dao.putTransaction(TransactionEntity(invoice, now, cashier, method, total, "Selesai"))
        dao.putTransactionItems(cart.map { line ->
            val p = current.first { it.id == line.productId }
            TransactionItemEntity(invoiceNo=invoice, productId=p.id, productName=p.name,
                quantity=line.quantity, unitPrice=line.unitPrice, unitCost=p.hpp)
        })
        cart.forEach { line ->
            val p = dao.product(line.productId)!!
            val after = p.stock - line.quantity
            dao.putProduct(p.copy(stock = after, sold30Days = p.sold30Days + line.quantity))
            dao.putMovement(StockMovementEntity(id(), p.id, p.name, "Penjualan", -line.quantity, p.stock, after, invoice, now))
            if (after <= p.minStock) dao.putNotification(NotificationEntity(id(), "Stok ${p.name} menipis",
                "Sisa stok $after ${p.unit}. Segera lakukan restock.", "stock", now))
        }
        dao.changeBalance(store.id, total)
        log(cashier, "Transaksi berhasil", "$invoice • $total")
        pruneTransactions()
        invoice
    }
    suspend fun requestCancel(invoice: String, reason: String) = db.withTransaction {
        require(reason.isNotBlank())
        val tx = dao.transaction(invoice) ?: error("Transaksi tidak ditemukan")
        require(tx.status == "Selesai") { "Transaksi tidak dapat diajukan." }
        dao.putTransaction(tx.copy(status = "Perlu Ditinjau", cancelRequestReason = reason))
        dao.putNotification(NotificationEntity(id(), "Pengajuan pembatalan transaksi",
            "$invoice: $reason", "transaction", System.currentTimeMillis()))
        log(dao.session()?.name ?: "Kasir", "Ajukan pembatalan", invoice)
    }
    suspend fun cancelTransaction(invoice: String, reason: String) = db.withTransaction {
        require(reason.isNotBlank())
        val tx = dao.transaction(invoice) ?: error("Transaksi tidak ditemukan")
        require(tx.status == "Selesai" || tx.status == "Perlu Ditinjau") { "Transaksi sudah dibatalkan." }
        val store = dao.store() ?: error("Toko tidak ditemukan")
        dao.transactionItems(invoice).forEach { item ->
            val p = dao.product(item.productId) ?: return@forEach
            val after = p.stock + item.quantity
            dao.putProduct(p.copy(stock = after, sold30Days = (p.sold30Days-item.quantity).coerceAtLeast(0)))
            dao.putMovement(StockMovementEntity(id(), p.id, p.name, "Pembatalan transaksi", item.quantity,
                p.stock, after, invoice, System.currentTimeMillis()))
        }
        dao.putTransaction(tx.copy(status = "Dibatalkan", cancelReason = reason))
        if (tx.balancePosted) dao.changeBalance(store.id, -tx.total)
        log(dao.session()?.name ?: "Owner", "Batalkan transaksi", "$invoice • $reason")
    }
    suspend fun saveSupplier(id: String?, name: String, phone: String, address: String, note: String) = db.withTransaction {
        require(name.isNotBlank())
        val key = id ?: "SUP-${UUID.randomUUID().toString().take(8)}"
        dao.putSupplier(SupplierEntity(key, name, phone, address, note))
        log(dao.session()?.name ?: "Owner", "Simpan supplier", name)
    }
    suspend fun deleteSupplier(id: String) = db.withTransaction {
        dao.deleteSupplier(id)
        log(dao.session()?.name ?: "Owner", "Hapus supplier", id)
    }
    suspend fun saveCashier(id: String?, name: String, username: String, password: String, phone: String) = db.withTransaction {
        require(name.isNotBlank() && username.isNotBlank())
        val storeId = dao.store()?.id ?: error("Toko tidak ditemukan")
        val existing = id?.let { dao.account(it) }
        val key = id ?: "cashier-${UUID.randomUUID()}"
        val passHash = if (password.isNotBlank()) hash(password) else existing?.passwordHash ?: hash("123456")
        dao.putAccount(AccountEntity(key, storeId, name, "", username, passHash, "kasir", true, phone))
        log(dao.session()?.name ?: "Owner", if (id == null) "Tambah kasir" else "Ubah akun kasir", name)
    }
    suspend fun setCashierActive(id: String, active: Boolean) = db.withTransaction {
        dao.setAccountActive(id, active)
        log(dao.session()?.name ?: "Owner", "Ubah status akun kasir", "$id: $active")
    }
    suspend fun resetCashierPassword(id: String, password: String) = db.withTransaction {
        require(password.length >= 6)
        dao.resetPassword(id, hash(password))
        log(dao.session()?.name ?: "Owner", "Reset kata sandi kasir", id)
    }
    suspend fun updateBankInfo(info: String) = db.withTransaction {
        val store = dao.store() ?: return@withTransaction
        dao.updateBankInfo(store.id, info)
        log(dao.session()?.name ?: "Owner", "Ubah rekening bank", "Informasi rekening diperbarui")
    }
    suspend fun updateQris(uri: String) = db.withTransaction {
        val store = dao.store() ?: return@withTransaction
        dao.updateQris(store.id, uri)
        log(dao.session()?.name ?: "Owner", "Ubah QRIS", "Gambar QRIS diperbarui")
    }
    suspend fun markNotificationRead(id: String) = dao.readNotification(id)
    suspend fun markAllNotificationsRead() = dao.readAllNotifications()
    suspend fun notificationRead(id: String) = dao.readNotification(id)

    private suspend fun pruneCash(kind: String) {
        val ids = dao.oldCashIds(kind); if (ids.isNotEmpty()) dao.deleteCashRecords(ids)
    }
    private suspend fun pruneTransactions() {
        val ids = dao.oldTransactionIds()
        if (ids.isNotEmpty()) { dao.deleteTransactionItems(ids); dao.deleteTransactions(ids) }
    }
    private suspend fun pruneRestocks() { val ids=dao.oldRestockIds(); if(ids.isNotEmpty()) dao.deleteRestocks(ids) }
    private suspend fun pruneAdjustments() { val ids=dao.oldAdjustmentIds(); if(ids.isNotEmpty()) dao.deleteAdjustments(ids) }

    fun products() = dao.observeProducts()
    fun suppliers() = dao.observeSuppliers()
    fun transactions() = dao.observeTransactions()
    fun transactionItems() = dao.observeAllTransactionItems()
    fun cashRecords() = dao.observeCashRecords()
    fun restocks() = dao.observeRestocks()
    fun adjustments() = dao.observeAdjustments()
    fun movements() = dao.observeMovements()
    fun activities() = dao.observeActivities()
    fun notifications() = dao.observeNotifications()
    fun accounts() = dao.observeAccounts()
    fun store() = dao.observeStore()
    fun session() = dao.observeSession()
    suspend fun items(invoice: String) = dao.transactionItems(invoice)
}

data class CartLineData(val productId: String, val name: String, val unitPrice: Long, val quantity: Int)
