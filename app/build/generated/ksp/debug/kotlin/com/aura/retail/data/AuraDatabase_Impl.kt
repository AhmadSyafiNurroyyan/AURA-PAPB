package com.aura.retail.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AuraDatabase_Impl : AuraDatabase() {
  private val _auraDao: Lazy<AuraDao> = lazy {
    AuraDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "515d3f74c857c095b0588d71bbb3121f", "74ecb75cb43f4031acd4b8a5df298722") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (`id` TEXT NOT NULL, `storeId` TEXT NOT NULL, `name` TEXT NOT NULL, `email` TEXT NOT NULL, `username` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `role` TEXT NOT NULL, `active` INTEGER NOT NULL, `phone` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_accounts_storeId_username` ON `accounts` (`storeId`, `username`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `stores` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `businessType` TEXT NOT NULL, `balance` INTEGER NOT NULL, `bankInfo` TEXT NOT NULL, `qrisImageUri` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `sessions` (`id` INTEGER NOT NULL, `accountId` TEXT NOT NULL, `role` TEXT NOT NULL, `name` TEXT NOT NULL, `storeId` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` TEXT NOT NULL, `sku` TEXT NOT NULL, `name` TEXT NOT NULL, `categories` TEXT NOT NULL, `unit` TEXT NOT NULL, `sellPrice` INTEGER NOT NULL, `hpp` INTEGER NOT NULL, `stock` INTEGER NOT NULL, `minStock` INTEGER NOT NULL, `sold30Days` INTEGER NOT NULL, `archived` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_sku` ON `products` (`sku`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `suppliers` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, `address` TEXT NOT NULL, `note` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`invoiceNo` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `cashierName` TEXT NOT NULL, `paymentMethod` TEXT NOT NULL, `total` INTEGER NOT NULL, `status` TEXT NOT NULL, `cancelReason` TEXT NOT NULL, `cancelRequestReason` TEXT NOT NULL, `balancePosted` INTEGER NOT NULL, PRIMARY KEY(`invoiceNo`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `transaction_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `invoiceNo` TEXT NOT NULL, `productId` TEXT NOT NULL, `productName` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `unitPrice` INTEGER NOT NULL, `unitCost` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_items_invoiceNo` ON `transaction_items` (`invoiceNo`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cash_records` (`id` TEXT NOT NULL, `kind` TEXT NOT NULL, `category` TEXT NOT NULL, `amount` INTEGER NOT NULL, `note` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isPrive` INTEGER NOT NULL, `isCapital` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_cash_records_kind` ON `cash_records` (`kind`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_cash_records_createdAt` ON `cash_records` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `restocks` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `supplierId` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `purchasePrice` INTEGER NOT NULL, `total` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `note` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `stock_adjustments` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `beforeStock` INTEGER NOT NULL, `afterStock` INTEGER NOT NULL, `difference` INTEGER NOT NULL, `reason` TEXT NOT NULL, `note` TEXT NOT NULL, `lossValue` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `stock_movements` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `productName` TEXT NOT NULL, `type` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `beforeStock` INTEGER NOT NULL, `afterStock` INTEGER NOT NULL, `reference` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_movements_createdAt` ON `stock_movements` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `activity_logs` (`id` TEXT NOT NULL, `actor` TEXT NOT NULL, `action` TEXT NOT NULL, `detail` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_activity_logs_createdAt` ON `activity_logs` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `type` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_notifications_createdAt` ON `notifications` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '515d3f74c857c095b0588d71bbb3121f')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `accounts`")
        connection.execSQL("DROP TABLE IF EXISTS `stores`")
        connection.execSQL("DROP TABLE IF EXISTS `sessions`")
        connection.execSQL("DROP TABLE IF EXISTS `products`")
        connection.execSQL("DROP TABLE IF EXISTS `suppliers`")
        connection.execSQL("DROP TABLE IF EXISTS `transactions`")
        connection.execSQL("DROP TABLE IF EXISTS `transaction_items`")
        connection.execSQL("DROP TABLE IF EXISTS `cash_records`")
        connection.execSQL("DROP TABLE IF EXISTS `restocks`")
        connection.execSQL("DROP TABLE IF EXISTS `stock_adjustments`")
        connection.execSQL("DROP TABLE IF EXISTS `stock_movements`")
        connection.execSQL("DROP TABLE IF EXISTS `activity_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `notifications`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsAccounts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAccounts.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("storeId", TableInfo.Column("storeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("email", TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("username", TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("passwordHash", TableInfo.Column("passwordHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("role", TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("active", TableInfo.Column("active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAccounts.put("phone", TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAccounts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAccounts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAccounts.add(TableInfo.Index("index_accounts_storeId_username", true, listOf("storeId", "username"), listOf("ASC", "ASC")))
        val _infoAccounts: TableInfo = TableInfo("accounts", _columnsAccounts, _foreignKeysAccounts, _indicesAccounts)
        val _existingAccounts: TableInfo = read(connection, "accounts")
        if (!_infoAccounts.equals(_existingAccounts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |accounts(com.aura.retail.data.AccountEntity).
              | Expected:
              |""".trimMargin() + _infoAccounts + """
              |
              | Found:
              |""".trimMargin() + _existingAccounts)
        }
        val _columnsStores: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStores.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStores.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStores.put("businessType", TableInfo.Column("businessType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStores.put("balance", TableInfo.Column("balance", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStores.put("bankInfo", TableInfo.Column("bankInfo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStores.put("qrisImageUri", TableInfo.Column("qrisImageUri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStores: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStores: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoStores: TableInfo = TableInfo("stores", _columnsStores, _foreignKeysStores, _indicesStores)
        val _existingStores: TableInfo = read(connection, "stores")
        if (!_infoStores.equals(_existingStores)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |stores(com.aura.retail.data.StoreEntity).
              | Expected:
              |""".trimMargin() + _infoStores + """
              |
              | Found:
              |""".trimMargin() + _existingStores)
        }
        val _columnsSessions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSessions.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessions.put("accountId", TableInfo.Column("accountId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessions.put("role", TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessions.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSessions.put("storeId", TableInfo.Column("storeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSessions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSessions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSessions: TableInfo = TableInfo("sessions", _columnsSessions, _foreignKeysSessions, _indicesSessions)
        val _existingSessions: TableInfo = read(connection, "sessions")
        if (!_infoSessions.equals(_existingSessions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |sessions(com.aura.retail.data.SessionEntity).
              | Expected:
              |""".trimMargin() + _infoSessions + """
              |
              | Found:
              |""".trimMargin() + _existingSessions)
        }
        val _columnsProducts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProducts.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("sku", TableInfo.Column("sku", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("categories", TableInfo.Column("categories", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("unit", TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("sellPrice", TableInfo.Column("sellPrice", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("hpp", TableInfo.Column("hpp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("stock", TableInfo.Column("stock", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("minStock", TableInfo.Column("minStock", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("sold30Days", TableInfo.Column("sold30Days", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("archived", TableInfo.Column("archived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProducts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProducts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProducts.add(TableInfo.Index("index_products_sku", false, listOf("sku"), listOf("ASC")))
        val _infoProducts: TableInfo = TableInfo("products", _columnsProducts, _foreignKeysProducts, _indicesProducts)
        val _existingProducts: TableInfo = read(connection, "products")
        if (!_infoProducts.equals(_existingProducts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |products(com.aura.retail.data.ProductEntity).
              | Expected:
              |""".trimMargin() + _infoProducts + """
              |
              | Found:
              |""".trimMargin() + _existingProducts)
        }
        val _columnsSuppliers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSuppliers.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSuppliers.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSuppliers.put("phone", TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSuppliers.put("address", TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSuppliers.put("note", TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSuppliers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSuppliers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSuppliers: TableInfo = TableInfo("suppliers", _columnsSuppliers, _foreignKeysSuppliers, _indicesSuppliers)
        val _existingSuppliers: TableInfo = read(connection, "suppliers")
        if (!_infoSuppliers.equals(_existingSuppliers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |suppliers(com.aura.retail.data.SupplierEntity).
              | Expected:
              |""".trimMargin() + _infoSuppliers + """
              |
              | Found:
              |""".trimMargin() + _existingSuppliers)
        }
        val _columnsTransactions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransactions.put("invoiceNo", TableInfo.Column("invoiceNo", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("cashierName", TableInfo.Column("cashierName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("paymentMethod", TableInfo.Column("paymentMethod", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("total", TableInfo.Column("total", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("status", TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("cancelReason", TableInfo.Column("cancelReason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("cancelRequestReason", TableInfo.Column("cancelRequestReason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("balancePosted", TableInfo.Column("balancePosted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransactions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTransactions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTransactions: TableInfo = TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions)
        val _existingTransactions: TableInfo = read(connection, "transactions")
        if (!_infoTransactions.equals(_existingTransactions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |transactions(com.aura.retail.data.TransactionEntity).
              | Expected:
              |""".trimMargin() + _infoTransactions + """
              |
              | Found:
              |""".trimMargin() + _existingTransactions)
        }
        val _columnsTransactionItems: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransactionItems.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactionItems.put("invoiceNo", TableInfo.Column("invoiceNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactionItems.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactionItems.put("productName", TableInfo.Column("productName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactionItems.put("quantity", TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactionItems.put("unitPrice", TableInfo.Column("unitPrice", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactionItems.put("unitCost", TableInfo.Column("unitCost", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransactionItems: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTransactionItems: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTransactionItems.add(TableInfo.Index("index_transaction_items_invoiceNo", false, listOf("invoiceNo"), listOf("ASC")))
        val _infoTransactionItems: TableInfo = TableInfo("transaction_items", _columnsTransactionItems, _foreignKeysTransactionItems, _indicesTransactionItems)
        val _existingTransactionItems: TableInfo = read(connection, "transaction_items")
        if (!_infoTransactionItems.equals(_existingTransactionItems)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |transaction_items(com.aura.retail.data.TransactionItemEntity).
              | Expected:
              |""".trimMargin() + _infoTransactionItems + """
              |
              | Found:
              |""".trimMargin() + _existingTransactionItems)
        }
        val _columnsCashRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCashRecords.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCashRecords.put("kind", TableInfo.Column("kind", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCashRecords.put("category", TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCashRecords.put("amount", TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCashRecords.put("note", TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCashRecords.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCashRecords.put("isPrive", TableInfo.Column("isPrive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCashRecords.put("isCapital", TableInfo.Column("isCapital", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCashRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCashRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCashRecords.add(TableInfo.Index("index_cash_records_kind", false, listOf("kind"), listOf("ASC")))
        _indicesCashRecords.add(TableInfo.Index("index_cash_records_createdAt", false, listOf("createdAt"), listOf("ASC")))
        val _infoCashRecords: TableInfo = TableInfo("cash_records", _columnsCashRecords, _foreignKeysCashRecords, _indicesCashRecords)
        val _existingCashRecords: TableInfo = read(connection, "cash_records")
        if (!_infoCashRecords.equals(_existingCashRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cash_records(com.aura.retail.data.CashRecordEntity).
              | Expected:
              |""".trimMargin() + _infoCashRecords + """
              |
              | Found:
              |""".trimMargin() + _existingCashRecords)
        }
        val _columnsRestocks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRestocks.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRestocks.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRestocks.put("supplierId", TableInfo.Column("supplierId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRestocks.put("quantity", TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRestocks.put("purchasePrice", TableInfo.Column("purchasePrice", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRestocks.put("total", TableInfo.Column("total", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRestocks.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRestocks.put("note", TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRestocks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRestocks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRestocks: TableInfo = TableInfo("restocks", _columnsRestocks, _foreignKeysRestocks, _indicesRestocks)
        val _existingRestocks: TableInfo = read(connection, "restocks")
        if (!_infoRestocks.equals(_existingRestocks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |restocks(com.aura.retail.data.RestockEntity).
              | Expected:
              |""".trimMargin() + _infoRestocks + """
              |
              | Found:
              |""".trimMargin() + _existingRestocks)
        }
        val _columnsStockAdjustments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStockAdjustments.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockAdjustments.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockAdjustments.put("beforeStock", TableInfo.Column("beforeStock", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockAdjustments.put("afterStock", TableInfo.Column("afterStock", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockAdjustments.put("difference", TableInfo.Column("difference", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockAdjustments.put("reason", TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockAdjustments.put("note", TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockAdjustments.put("lossValue", TableInfo.Column("lossValue", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockAdjustments.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStockAdjustments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStockAdjustments: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoStockAdjustments: TableInfo = TableInfo("stock_adjustments", _columnsStockAdjustments, _foreignKeysStockAdjustments, _indicesStockAdjustments)
        val _existingStockAdjustments: TableInfo = read(connection, "stock_adjustments")
        if (!_infoStockAdjustments.equals(_existingStockAdjustments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |stock_adjustments(com.aura.retail.data.StockAdjustmentEntity).
              | Expected:
              |""".trimMargin() + _infoStockAdjustments + """
              |
              | Found:
              |""".trimMargin() + _existingStockAdjustments)
        }
        val _columnsStockMovements: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStockMovements.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMovements.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMovements.put("productName", TableInfo.Column("productName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMovements.put("type", TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMovements.put("quantity", TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMovements.put("beforeStock", TableInfo.Column("beforeStock", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMovements.put("afterStock", TableInfo.Column("afterStock", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMovements.put("reference", TableInfo.Column("reference", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMovements.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStockMovements: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStockMovements: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesStockMovements.add(TableInfo.Index("index_stock_movements_createdAt", false, listOf("createdAt"), listOf("ASC")))
        val _infoStockMovements: TableInfo = TableInfo("stock_movements", _columnsStockMovements, _foreignKeysStockMovements, _indicesStockMovements)
        val _existingStockMovements: TableInfo = read(connection, "stock_movements")
        if (!_infoStockMovements.equals(_existingStockMovements)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |stock_movements(com.aura.retail.data.StockMovementEntity).
              | Expected:
              |""".trimMargin() + _infoStockMovements + """
              |
              | Found:
              |""".trimMargin() + _existingStockMovements)
        }
        val _columnsActivityLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsActivityLogs.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsActivityLogs.put("actor", TableInfo.Column("actor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsActivityLogs.put("action", TableInfo.Column("action", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsActivityLogs.put("detail", TableInfo.Column("detail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsActivityLogs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysActivityLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesActivityLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesActivityLogs.add(TableInfo.Index("index_activity_logs_createdAt", false, listOf("createdAt"), listOf("ASC")))
        val _infoActivityLogs: TableInfo = TableInfo("activity_logs", _columnsActivityLogs, _foreignKeysActivityLogs, _indicesActivityLogs)
        val _existingActivityLogs: TableInfo = read(connection, "activity_logs")
        if (!_infoActivityLogs.equals(_existingActivityLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |activity_logs(com.aura.retail.data.ActivityLogEntity).
              | Expected:
              |""".trimMargin() + _infoActivityLogs + """
              |
              | Found:
              |""".trimMargin() + _existingActivityLogs)
        }
        val _columnsNotifications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNotifications.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("message", TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("type", TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("isRead", TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNotifications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesNotifications: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesNotifications.add(TableInfo.Index("index_notifications_createdAt", false, listOf("createdAt"), listOf("ASC")))
        val _infoNotifications: TableInfo = TableInfo("notifications", _columnsNotifications, _foreignKeysNotifications, _indicesNotifications)
        val _existingNotifications: TableInfo = read(connection, "notifications")
        if (!_infoNotifications.equals(_existingNotifications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |notifications(com.aura.retail.data.NotificationEntity).
              | Expected:
              |""".trimMargin() + _infoNotifications + """
              |
              | Found:
              |""".trimMargin() + _existingNotifications)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "accounts", "stores", "sessions", "products", "suppliers", "transactions", "transaction_items", "cash_records", "restocks", "stock_adjustments", "stock_movements", "activity_logs", "notifications")
  }

  public override fun clearAllTables() {
    super.performClear(false, "accounts", "stores", "sessions", "products", "suppliers", "transactions", "transaction_items", "cash_records", "restocks", "stock_adjustments", "stock_movements", "activity_logs", "notifications")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(AuraDao::class, AuraDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun dao(): AuraDao = _auraDao.value
}
