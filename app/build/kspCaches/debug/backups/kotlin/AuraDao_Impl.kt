import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AuraDao_Impl(
  __db: RoomDatabase,
) : AuraDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAccountEntity: EntityInsertAdapter<AccountEntity>

  private val __insertAdapterOfStoreEntity: EntityInsertAdapter<StoreEntity>

  private val __insertAdapterOfSessionEntity: EntityInsertAdapter<SessionEntity>

  private val __insertAdapterOfProductEntity: EntityInsertAdapter<ProductEntity>

  private val __insertAdapterOfSupplierEntity: EntityInsertAdapter<SupplierEntity>

  private val __insertAdapterOfTransactionEntity: EntityInsertAdapter<TransactionEntity>

  private val __insertAdapterOfTransactionItemEntity: EntityInsertAdapter<TransactionItemEntity>

  private val __insertAdapterOfCashRecordEntity: EntityInsertAdapter<CashRecordEntity>

  private val __insertAdapterOfRestockEntity: EntityInsertAdapter<RestockEntity>

  private val __insertAdapterOfStockAdjustmentEntity: EntityInsertAdapter<StockAdjustmentEntity>

  private val __insertAdapterOfStockMovementEntity: EntityInsertAdapter<StockMovementEntity>

  private val __insertAdapterOfActivityLogEntity: EntityInsertAdapter<ActivityLogEntity>

  private val __insertAdapterOfNotificationEntity: EntityInsertAdapter<NotificationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAccountEntity = object : EntityInsertAdapter<AccountEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `accounts` (`id`,`storeId`,`name`,`email`,`username`,`passwordHash`,`role`,`active`,`phone`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AccountEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.storeId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.email)
        statement.bindText(5, entity.username)
        statement.bindText(6, entity.passwordHash)
        statement.bindText(7, entity.role)
        val _tmp: Int = if (entity.active) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        statement.bindText(9, entity.phone)
      }
    }
    this.__insertAdapterOfStoreEntity = object : EntityInsertAdapter<StoreEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `stores` (`id`,`name`,`businessType`,`balance`,`bankInfo`,`qrisImageUri`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StoreEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.businessType)
        statement.bindLong(4, entity.balance)
        statement.bindText(5, entity.bankInfo)
        statement.bindText(6, entity.qrisImageUri)
      }
    }
    this.__insertAdapterOfSessionEntity = object : EntityInsertAdapter<SessionEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `sessions` (`id`,`accountId`,`role`,`name`,`storeId`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SessionEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.accountId)
        statement.bindText(3, entity.role)
        statement.bindText(4, entity.name)
        statement.bindText(5, entity.storeId)
      }
    }
    this.__insertAdapterOfProductEntity = object : EntityInsertAdapter<ProductEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `products` (`id`,`sku`,`name`,`categories`,`unit`,`sellPrice`,`hpp`,`stock`,`minStock`,`sold30Days`,`archived`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProductEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.sku)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.categories)
        statement.bindText(5, entity.unit)
        statement.bindLong(6, entity.sellPrice)
        statement.bindLong(7, entity.hpp)
        statement.bindLong(8, entity.stock.toLong())
        statement.bindLong(9, entity.minStock.toLong())
        statement.bindLong(10, entity.sold30Days.toLong())
        val _tmp: Int = if (entity.archived) 1 else 0
        statement.bindLong(11, _tmp.toLong())
      }
    }
    this.__insertAdapterOfSupplierEntity = object : EntityInsertAdapter<SupplierEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `suppliers` (`id`,`name`,`phone`,`address`,`note`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SupplierEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.phone)
        statement.bindText(4, entity.address)
        statement.bindText(5, entity.note)
      }
    }
    this.__insertAdapterOfTransactionEntity = object : EntityInsertAdapter<TransactionEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `transactions` (`invoiceNo`,`createdAt`,`cashierName`,`paymentMethod`,`total`,`status`,`cancelReason`,`cancelRequestReason`,`balancePosted`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransactionEntity) {
        statement.bindText(1, entity.invoiceNo)
        statement.bindLong(2, entity.createdAt)
        statement.bindText(3, entity.cashierName)
        statement.bindText(4, entity.paymentMethod)
        statement.bindLong(5, entity.total)
        statement.bindText(6, entity.status)
        statement.bindText(7, entity.cancelReason)
        statement.bindText(8, entity.cancelRequestReason)
        val _tmp: Int = if (entity.balancePosted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
      }
    }
    this.__insertAdapterOfTransactionItemEntity = object : EntityInsertAdapter<TransactionItemEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `transaction_items` (`id`,`invoiceNo`,`productId`,`productName`,`quantity`,`unitPrice`,`unitCost`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransactionItemEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.invoiceNo)
        statement.bindText(3, entity.productId)
        statement.bindText(4, entity.productName)
        statement.bindLong(5, entity.quantity.toLong())
        statement.bindLong(6, entity.unitPrice)
        statement.bindLong(7, entity.unitCost)
      }
    }
    this.__insertAdapterOfCashRecordEntity = object : EntityInsertAdapter<CashRecordEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `cash_records` (`id`,`kind`,`category`,`amount`,`note`,`createdAt`,`isPrive`,`isCapital`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CashRecordEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.kind)
        statement.bindText(3, entity.category)
        statement.bindLong(4, entity.amount)
        statement.bindText(5, entity.note)
        statement.bindLong(6, entity.createdAt)
        val _tmp: Int = if (entity.isPrive) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmp_1: Int = if (entity.isCapital) 1 else 0
        statement.bindLong(8, _tmp_1.toLong())
      }
    }
    this.__insertAdapterOfRestockEntity = object : EntityInsertAdapter<RestockEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `restocks` (`id`,`productId`,`supplierId`,`quantity`,`purchasePrice`,`total`,`createdAt`,`note`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RestockEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.supplierId)
        statement.bindLong(4, entity.quantity.toLong())
        statement.bindLong(5, entity.purchasePrice)
        statement.bindLong(6, entity.total)
        statement.bindLong(7, entity.createdAt)
        statement.bindText(8, entity.note)
      }
    }
    this.__insertAdapterOfStockAdjustmentEntity = object : EntityInsertAdapter<StockAdjustmentEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `stock_adjustments` (`id`,`productId`,`beforeStock`,`afterStock`,`difference`,`reason`,`note`,`lossValue`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StockAdjustmentEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindLong(3, entity.beforeStock.toLong())
        statement.bindLong(4, entity.afterStock.toLong())
        statement.bindLong(5, entity.difference.toLong())
        statement.bindText(6, entity.reason)
        statement.bindText(7, entity.note)
        statement.bindLong(8, entity.lossValue)
        statement.bindLong(9, entity.createdAt)
      }
    }
    this.__insertAdapterOfStockMovementEntity = object : EntityInsertAdapter<StockMovementEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `stock_movements` (`id`,`productId`,`productName`,`type`,`quantity`,`beforeStock`,`afterStock`,`reference`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StockMovementEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.productName)
        statement.bindText(4, entity.type)
        statement.bindLong(5, entity.quantity.toLong())
        statement.bindLong(6, entity.beforeStock.toLong())
        statement.bindLong(7, entity.afterStock.toLong())
        statement.bindText(8, entity.reference)
        statement.bindLong(9, entity.createdAt)
      }
    }
    this.__insertAdapterOfActivityLogEntity = object : EntityInsertAdapter<ActivityLogEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `activity_logs` (`id`,`actor`,`action`,`detail`,`createdAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ActivityLogEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.actor)
        statement.bindText(3, entity.action)
        statement.bindText(4, entity.detail)
        statement.bindLong(5, entity.createdAt)
      }
    }
    this.__insertAdapterOfNotificationEntity = object : EntityInsertAdapter<NotificationEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `notifications` (`id`,`title`,`message`,`type`,`createdAt`,`isRead`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: NotificationEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.message)
        statement.bindText(4, entity.type)
        statement.bindLong(5, entity.createdAt)
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(6, _tmp.toLong())
      }
    }
  }

  public override suspend fun putAccount(`value`: AccountEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAccountEntity.insert(_connection, value)
  }

  public override suspend fun putStore(`value`: StoreEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfStoreEntity.insert(_connection, value)
  }

  public override suspend fun putSession(`value`: SessionEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSessionEntity.insert(_connection, value)
  }

  public override suspend fun putProduct(`value`: ProductEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfProductEntity.insert(_connection, value)
  }

  public override suspend fun putSupplier(`value`: SupplierEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSupplierEntity.insert(_connection, value)
  }

  public override suspend fun putTransaction(`value`: TransactionEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransactionEntity.insert(_connection, value)
  }

  public override suspend fun putTransactionItems(values: List<TransactionItemEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransactionItemEntity.insert(_connection, values)
  }

  public override suspend fun putCashRecord(`value`: CashRecordEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCashRecordEntity.insert(_connection, value)
  }

  public override suspend fun putRestock(`value`: RestockEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRestockEntity.insert(_connection, value)
  }

  public override suspend fun putAdjustment(`value`: StockAdjustmentEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfStockAdjustmentEntity.insert(_connection, value)
  }

  public override suspend fun putMovement(`value`: StockMovementEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfStockMovementEntity.insert(_connection, value)
  }

  public override suspend fun putActivity(`value`: ActivityLogEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfActivityLogEntity.insert(_connection, value)
  }

  public override suspend fun putNotification(`value`: NotificationEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfNotificationEntity.insert(_connection, value)
  }

  public override suspend fun ownerLogin(email: String, hash: String): AccountEntity? {
    val _sql: String = "SELECT * FROM accounts WHERE email = ? AND passwordHash = ? AND role = 'owner' AND active = 1 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, email)
        _argIndex = 2
        _stmt.bindText(_argIndex, hash)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStoreId: Int = getColumnIndexOrThrow(_stmt, "storeId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfUsername: Int = getColumnIndexOrThrow(_stmt, "username")
        val _columnIndexOfPasswordHash: Int = getColumnIndexOrThrow(_stmt, "passwordHash")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _result: AccountEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStoreId: String
          _tmpStoreId = _stmt.getText(_columnIndexOfStoreId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpUsername: String
          _tmpUsername = _stmt.getText(_columnIndexOfUsername)
          val _tmpPasswordHash: String
          _tmpPasswordHash = _stmt.getText(_columnIndexOfPasswordHash)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          _result = AccountEntity(_tmpId,_tmpStoreId,_tmpName,_tmpEmail,_tmpUsername,_tmpPasswordHash,_tmpRole,_tmpActive,_tmpPhone)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cashierLogin(
    storeId: String,
    username: String,
    hash: String,
  ): AccountEntity? {
    val _sql: String = "SELECT * FROM accounts WHERE storeId = ? AND username = ? AND passwordHash = ? AND role = 'kasir' AND active = 1 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, storeId)
        _argIndex = 2
        _stmt.bindText(_argIndex, username)
        _argIndex = 3
        _stmt.bindText(_argIndex, hash)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStoreId: Int = getColumnIndexOrThrow(_stmt, "storeId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfUsername: Int = getColumnIndexOrThrow(_stmt, "username")
        val _columnIndexOfPasswordHash: Int = getColumnIndexOrThrow(_stmt, "passwordHash")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _result: AccountEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStoreId: String
          _tmpStoreId = _stmt.getText(_columnIndexOfStoreId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpUsername: String
          _tmpUsername = _stmt.getText(_columnIndexOfUsername)
          val _tmpPasswordHash: String
          _tmpPasswordHash = _stmt.getText(_columnIndexOfPasswordHash)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          _result = AccountEntity(_tmpId,_tmpStoreId,_tmpName,_tmpEmail,_tmpUsername,_tmpPasswordHash,_tmpRole,_tmpActive,_tmpPhone)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAccounts(): Flow<List<AccountEntity>> {
    val _sql: String = "SELECT * FROM accounts ORDER BY role, name"
    return createFlow(__db, false, arrayOf("accounts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStoreId: Int = getColumnIndexOrThrow(_stmt, "storeId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfUsername: Int = getColumnIndexOrThrow(_stmt, "username")
        val _columnIndexOfPasswordHash: Int = getColumnIndexOrThrow(_stmt, "passwordHash")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _result: MutableList<AccountEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AccountEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStoreId: String
          _tmpStoreId = _stmt.getText(_columnIndexOfStoreId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpUsername: String
          _tmpUsername = _stmt.getText(_columnIndexOfUsername)
          val _tmpPasswordHash: String
          _tmpPasswordHash = _stmt.getText(_columnIndexOfPasswordHash)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          _item = AccountEntity(_tmpId,_tmpStoreId,_tmpName,_tmpEmail,_tmpUsername,_tmpPasswordHash,_tmpRole,_tmpActive,_tmpPhone)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun account(id: String): AccountEntity? {
    val _sql: String = "SELECT * FROM accounts WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStoreId: Int = getColumnIndexOrThrow(_stmt, "storeId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfUsername: Int = getColumnIndexOrThrow(_stmt, "username")
        val _columnIndexOfPasswordHash: Int = getColumnIndexOrThrow(_stmt, "passwordHash")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _result: AccountEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStoreId: String
          _tmpStoreId = _stmt.getText(_columnIndexOfStoreId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpUsername: String
          _tmpUsername = _stmt.getText(_columnIndexOfUsername)
          val _tmpPasswordHash: String
          _tmpPasswordHash = _stmt.getText(_columnIndexOfPasswordHash)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          _result = AccountEntity(_tmpId,_tmpStoreId,_tmpName,_tmpEmail,_tmpUsername,_tmpPasswordHash,_tmpRole,_tmpActive,_tmpPhone)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun anyStore(): StoreEntity? {
    val _sql: String = "SELECT * FROM stores LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBusinessType: Int = getColumnIndexOrThrow(_stmt, "businessType")
        val _columnIndexOfBalance: Int = getColumnIndexOrThrow(_stmt, "balance")
        val _columnIndexOfBankInfo: Int = getColumnIndexOrThrow(_stmt, "bankInfo")
        val _columnIndexOfQrisImageUri: Int = getColumnIndexOrThrow(_stmt, "qrisImageUri")
        val _result: StoreEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBusinessType: String
          _tmpBusinessType = _stmt.getText(_columnIndexOfBusinessType)
          val _tmpBalance: Long
          _tmpBalance = _stmt.getLong(_columnIndexOfBalance)
          val _tmpBankInfo: String
          _tmpBankInfo = _stmt.getText(_columnIndexOfBankInfo)
          val _tmpQrisImageUri: String
          _tmpQrisImageUri = _stmt.getText(_columnIndexOfQrisImageUri)
          _result = StoreEntity(_tmpId,_tmpName,_tmpBusinessType,_tmpBalance,_tmpBankInfo,_tmpQrisImageUri)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun store(): StoreEntity? {
    val _sql: String = "SELECT * FROM stores WHERE id = (SELECT storeId FROM sessions WHERE id = 1) LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBusinessType: Int = getColumnIndexOrThrow(_stmt, "businessType")
        val _columnIndexOfBalance: Int = getColumnIndexOrThrow(_stmt, "balance")
        val _columnIndexOfBankInfo: Int = getColumnIndexOrThrow(_stmt, "bankInfo")
        val _columnIndexOfQrisImageUri: Int = getColumnIndexOrThrow(_stmt, "qrisImageUri")
        val _result: StoreEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBusinessType: String
          _tmpBusinessType = _stmt.getText(_columnIndexOfBusinessType)
          val _tmpBalance: Long
          _tmpBalance = _stmt.getLong(_columnIndexOfBalance)
          val _tmpBankInfo: String
          _tmpBankInfo = _stmt.getText(_columnIndexOfBankInfo)
          val _tmpQrisImageUri: String
          _tmpQrisImageUri = _stmt.getText(_columnIndexOfQrisImageUri)
          _result = StoreEntity(_tmpId,_tmpName,_tmpBusinessType,_tmpBalance,_tmpBankInfo,_tmpQrisImageUri)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeStore(): Flow<StoreEntity?> {
    val _sql: String = "SELECT stores.* FROM stores INNER JOIN sessions ON stores.id = sessions.storeId WHERE sessions.id = 1 LIMIT 1"
    return createFlow(__db, false, arrayOf("stores", "sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBusinessType: Int = getColumnIndexOrThrow(_stmt, "businessType")
        val _columnIndexOfBalance: Int = getColumnIndexOrThrow(_stmt, "balance")
        val _columnIndexOfBankInfo: Int = getColumnIndexOrThrow(_stmt, "bankInfo")
        val _columnIndexOfQrisImageUri: Int = getColumnIndexOrThrow(_stmt, "qrisImageUri")
        val _result: StoreEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBusinessType: String
          _tmpBusinessType = _stmt.getText(_columnIndexOfBusinessType)
          val _tmpBalance: Long
          _tmpBalance = _stmt.getLong(_columnIndexOfBalance)
          val _tmpBankInfo: String
          _tmpBankInfo = _stmt.getText(_columnIndexOfBankInfo)
          val _tmpQrisImageUri: String
          _tmpQrisImageUri = _stmt.getText(_columnIndexOfQrisImageUri)
          _result = StoreEntity(_tmpId,_tmpName,_tmpBusinessType,_tmpBalance,_tmpBankInfo,_tmpQrisImageUri)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeSession(): Flow<SessionEntity?> {
    val _sql: String = "SELECT * FROM sessions WHERE id = 1 LIMIT 1"
    return createFlow(__db, false, arrayOf("sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAccountId: Int = getColumnIndexOrThrow(_stmt, "accountId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfStoreId: Int = getColumnIndexOrThrow(_stmt, "storeId")
        val _result: SessionEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpAccountId: String
          _tmpAccountId = _stmt.getText(_columnIndexOfAccountId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpStoreId: String
          _tmpStoreId = _stmt.getText(_columnIndexOfStoreId)
          _result = SessionEntity(_tmpId,_tmpAccountId,_tmpRole,_tmpName,_tmpStoreId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun session(): SessionEntity? {
    val _sql: String = "SELECT * FROM sessions WHERE id = 1 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAccountId: Int = getColumnIndexOrThrow(_stmt, "accountId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfStoreId: Int = getColumnIndexOrThrow(_stmt, "storeId")
        val _result: SessionEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpAccountId: String
          _tmpAccountId = _stmt.getText(_columnIndexOfAccountId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpStoreId: String
          _tmpStoreId = _stmt.getText(_columnIndexOfStoreId)
          _result = SessionEntity(_tmpId,_tmpAccountId,_tmpRole,_tmpName,_tmpStoreId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeProducts(): Flow<List<ProductEntity>> {
    val _sql: String = "SELECT * FROM products WHERE archived = 0 ORDER BY name"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategories: Int = getColumnIndexOrThrow(_stmt, "categories")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfSellPrice: Int = getColumnIndexOrThrow(_stmt, "sellPrice")
        val _columnIndexOfHpp: Int = getColumnIndexOrThrow(_stmt, "hpp")
        val _columnIndexOfStock: Int = getColumnIndexOrThrow(_stmt, "stock")
        val _columnIndexOfMinStock: Int = getColumnIndexOrThrow(_stmt, "minStock")
        val _columnIndexOfSold30Days: Int = getColumnIndexOrThrow(_stmt, "sold30Days")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSku: String
          _tmpSku = _stmt.getText(_columnIndexOfSku)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategories: String
          _tmpCategories = _stmt.getText(_columnIndexOfCategories)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpSellPrice: Long
          _tmpSellPrice = _stmt.getLong(_columnIndexOfSellPrice)
          val _tmpHpp: Long
          _tmpHpp = _stmt.getLong(_columnIndexOfHpp)
          val _tmpStock: Int
          _tmpStock = _stmt.getLong(_columnIndexOfStock).toInt()
          val _tmpMinStock: Int
          _tmpMinStock = _stmt.getLong(_columnIndexOfMinStock).toInt()
          val _tmpSold30Days: Int
          _tmpSold30Days = _stmt.getLong(_columnIndexOfSold30Days).toInt()
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          _item = ProductEntity(_tmpId,_tmpSku,_tmpName,_tmpCategories,_tmpUnit,_tmpSellPrice,_tmpHpp,_tmpStock,_tmpMinStock,_tmpSold30Days,_tmpArchived)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun products(): List<ProductEntity> {
    val _sql: String = "SELECT * FROM products WHERE archived = 0 ORDER BY name"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategories: Int = getColumnIndexOrThrow(_stmt, "categories")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfSellPrice: Int = getColumnIndexOrThrow(_stmt, "sellPrice")
        val _columnIndexOfHpp: Int = getColumnIndexOrThrow(_stmt, "hpp")
        val _columnIndexOfStock: Int = getColumnIndexOrThrow(_stmt, "stock")
        val _columnIndexOfMinStock: Int = getColumnIndexOrThrow(_stmt, "minStock")
        val _columnIndexOfSold30Days: Int = getColumnIndexOrThrow(_stmt, "sold30Days")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSku: String
          _tmpSku = _stmt.getText(_columnIndexOfSku)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategories: String
          _tmpCategories = _stmt.getText(_columnIndexOfCategories)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpSellPrice: Long
          _tmpSellPrice = _stmt.getLong(_columnIndexOfSellPrice)
          val _tmpHpp: Long
          _tmpHpp = _stmt.getLong(_columnIndexOfHpp)
          val _tmpStock: Int
          _tmpStock = _stmt.getLong(_columnIndexOfStock).toInt()
          val _tmpMinStock: Int
          _tmpMinStock = _stmt.getLong(_columnIndexOfMinStock).toInt()
          val _tmpSold30Days: Int
          _tmpSold30Days = _stmt.getLong(_columnIndexOfSold30Days).toInt()
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          _item = ProductEntity(_tmpId,_tmpSku,_tmpName,_tmpCategories,_tmpUnit,_tmpSellPrice,_tmpHpp,_tmpStock,_tmpMinStock,_tmpSold30Days,_tmpArchived)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun product(id: String): ProductEntity? {
    val _sql: String = "SELECT * FROM products WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSku: Int = getColumnIndexOrThrow(_stmt, "sku")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategories: Int = getColumnIndexOrThrow(_stmt, "categories")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfSellPrice: Int = getColumnIndexOrThrow(_stmt, "sellPrice")
        val _columnIndexOfHpp: Int = getColumnIndexOrThrow(_stmt, "hpp")
        val _columnIndexOfStock: Int = getColumnIndexOrThrow(_stmt, "stock")
        val _columnIndexOfMinStock: Int = getColumnIndexOrThrow(_stmt, "minStock")
        val _columnIndexOfSold30Days: Int = getColumnIndexOrThrow(_stmt, "sold30Days")
        val _columnIndexOfArchived: Int = getColumnIndexOrThrow(_stmt, "archived")
        val _result: ProductEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSku: String
          _tmpSku = _stmt.getText(_columnIndexOfSku)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategories: String
          _tmpCategories = _stmt.getText(_columnIndexOfCategories)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpSellPrice: Long
          _tmpSellPrice = _stmt.getLong(_columnIndexOfSellPrice)
          val _tmpHpp: Long
          _tmpHpp = _stmt.getLong(_columnIndexOfHpp)
          val _tmpStock: Int
          _tmpStock = _stmt.getLong(_columnIndexOfStock).toInt()
          val _tmpMinStock: Int
          _tmpMinStock = _stmt.getLong(_columnIndexOfMinStock).toInt()
          val _tmpSold30Days: Int
          _tmpSold30Days = _stmt.getLong(_columnIndexOfSold30Days).toInt()
          val _tmpArchived: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfArchived).toInt()
          _tmpArchived = _tmp != 0
          _result = ProductEntity(_tmpId,_tmpSku,_tmpName,_tmpCategories,_tmpUnit,_tmpSellPrice,_tmpHpp,_tmpStock,_tmpMinStock,_tmpSold30Days,_tmpArchived)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun productCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM products"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeSuppliers(): Flow<List<SupplierEntity>> {
    val _sql: String = "SELECT * FROM suppliers ORDER BY name"
    return createFlow(__db, false, arrayOf("suppliers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _result: MutableList<SupplierEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SupplierEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          _item = SupplierEntity(_tmpId,_tmpName,_tmpPhone,_tmpAddress,_tmpNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun suppliers(): List<SupplierEntity> {
    val _sql: String = "SELECT * FROM suppliers ORDER BY name"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _result: MutableList<SupplierEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SupplierEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          _item = SupplierEntity(_tmpId,_tmpName,_tmpPhone,_tmpAddress,_tmpNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun supplier(id: String): SupplierEntity? {
    val _sql: String = "SELECT * FROM suppliers WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _result: SupplierEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          _result = SupplierEntity(_tmpId,_tmpName,_tmpPhone,_tmpAddress,_tmpNote)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTransactions(): Flow<List<TransactionEntity>> {
    val _sql: String = "SELECT * FROM transactions ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfInvoiceNo: Int = getColumnIndexOrThrow(_stmt, "invoiceNo")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCashierName: Int = getColumnIndexOrThrow(_stmt, "cashierName")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCancelReason: Int = getColumnIndexOrThrow(_stmt, "cancelReason")
        val _columnIndexOfCancelRequestReason: Int = getColumnIndexOrThrow(_stmt, "cancelRequestReason")
        val _columnIndexOfBalancePosted: Int = getColumnIndexOrThrow(_stmt, "balancePosted")
        val _result: MutableList<TransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntity
          val _tmpInvoiceNo: String
          _tmpInvoiceNo = _stmt.getText(_columnIndexOfInvoiceNo)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCashierName: String
          _tmpCashierName = _stmt.getText(_columnIndexOfCashierName)
          val _tmpPaymentMethod: String
          _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCancelReason: String
          _tmpCancelReason = _stmt.getText(_columnIndexOfCancelReason)
          val _tmpCancelRequestReason: String
          _tmpCancelRequestReason = _stmt.getText(_columnIndexOfCancelRequestReason)
          val _tmpBalancePosted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfBalancePosted).toInt()
          _tmpBalancePosted = _tmp != 0
          _item = TransactionEntity(_tmpInvoiceNo,_tmpCreatedAt,_tmpCashierName,_tmpPaymentMethod,_tmpTotal,_tmpStatus,_tmpCancelReason,_tmpCancelRequestReason,_tmpBalancePosted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun transactions(): List<TransactionEntity> {
    val _sql: String = "SELECT * FROM transactions ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfInvoiceNo: Int = getColumnIndexOrThrow(_stmt, "invoiceNo")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCashierName: Int = getColumnIndexOrThrow(_stmt, "cashierName")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCancelReason: Int = getColumnIndexOrThrow(_stmt, "cancelReason")
        val _columnIndexOfCancelRequestReason: Int = getColumnIndexOrThrow(_stmt, "cancelRequestReason")
        val _columnIndexOfBalancePosted: Int = getColumnIndexOrThrow(_stmt, "balancePosted")
        val _result: MutableList<TransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionEntity
          val _tmpInvoiceNo: String
          _tmpInvoiceNo = _stmt.getText(_columnIndexOfInvoiceNo)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCashierName: String
          _tmpCashierName = _stmt.getText(_columnIndexOfCashierName)
          val _tmpPaymentMethod: String
          _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCancelReason: String
          _tmpCancelReason = _stmt.getText(_columnIndexOfCancelReason)
          val _tmpCancelRequestReason: String
          _tmpCancelRequestReason = _stmt.getText(_columnIndexOfCancelRequestReason)
          val _tmpBalancePosted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfBalancePosted).toInt()
          _tmpBalancePosted = _tmp != 0
          _item = TransactionEntity(_tmpInvoiceNo,_tmpCreatedAt,_tmpCashierName,_tmpPaymentMethod,_tmpTotal,_tmpStatus,_tmpCancelReason,_tmpCancelRequestReason,_tmpBalancePosted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun transaction(invoice: String): TransactionEntity? {
    val _sql: String = "SELECT * FROM transactions WHERE invoiceNo = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, invoice)
        val _columnIndexOfInvoiceNo: Int = getColumnIndexOrThrow(_stmt, "invoiceNo")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCashierName: Int = getColumnIndexOrThrow(_stmt, "cashierName")
        val _columnIndexOfPaymentMethod: Int = getColumnIndexOrThrow(_stmt, "paymentMethod")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCancelReason: Int = getColumnIndexOrThrow(_stmt, "cancelReason")
        val _columnIndexOfCancelRequestReason: Int = getColumnIndexOrThrow(_stmt, "cancelRequestReason")
        val _columnIndexOfBalancePosted: Int = getColumnIndexOrThrow(_stmt, "balancePosted")
        val _result: TransactionEntity?
        if (_stmt.step()) {
          val _tmpInvoiceNo: String
          _tmpInvoiceNo = _stmt.getText(_columnIndexOfInvoiceNo)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCashierName: String
          _tmpCashierName = _stmt.getText(_columnIndexOfCashierName)
          val _tmpPaymentMethod: String
          _tmpPaymentMethod = _stmt.getText(_columnIndexOfPaymentMethod)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCancelReason: String
          _tmpCancelReason = _stmt.getText(_columnIndexOfCancelReason)
          val _tmpCancelRequestReason: String
          _tmpCancelRequestReason = _stmt.getText(_columnIndexOfCancelRequestReason)
          val _tmpBalancePosted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfBalancePosted).toInt()
          _tmpBalancePosted = _tmp != 0
          _result = TransactionEntity(_tmpInvoiceNo,_tmpCreatedAt,_tmpCashierName,_tmpPaymentMethod,_tmpTotal,_tmpStatus,_tmpCancelReason,_tmpCancelRequestReason,_tmpBalancePosted)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun transactionCount(start: Long, end: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM transactions WHERE createdAt >= ? AND createdAt < ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, start)
        _argIndex = 2
        _stmt.bindLong(_argIndex, end)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun transactionItems(invoice: String): List<TransactionItemEntity> {
    val _sql: String = "SELECT * FROM transaction_items WHERE invoiceNo = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, invoice)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInvoiceNo: Int = getColumnIndexOrThrow(_stmt, "invoiceNo")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnitPrice: Int = getColumnIndexOrThrow(_stmt, "unitPrice")
        val _columnIndexOfUnitCost: Int = getColumnIndexOrThrow(_stmt, "unitCost")
        val _result: MutableList<TransactionItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionItemEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInvoiceNo: String
          _tmpInvoiceNo = _stmt.getText(_columnIndexOfInvoiceNo)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpUnitPrice: Long
          _tmpUnitPrice = _stmt.getLong(_columnIndexOfUnitPrice)
          val _tmpUnitCost: Long
          _tmpUnitCost = _stmt.getLong(_columnIndexOfUnitCost)
          _item = TransactionItemEntity(_tmpId,_tmpInvoiceNo,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnitPrice,_tmpUnitCost)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllTransactionItems(): Flow<List<TransactionItemEntity>> {
    val _sql: String = "SELECT * FROM transaction_items ORDER BY id DESC"
    return createFlow(__db, false, arrayOf("transaction_items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInvoiceNo: Int = getColumnIndexOrThrow(_stmt, "invoiceNo")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfUnitPrice: Int = getColumnIndexOrThrow(_stmt, "unitPrice")
        val _columnIndexOfUnitCost: Int = getColumnIndexOrThrow(_stmt, "unitCost")
        val _result: MutableList<TransactionItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionItemEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInvoiceNo: String
          _tmpInvoiceNo = _stmt.getText(_columnIndexOfInvoiceNo)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpUnitPrice: Long
          _tmpUnitPrice = _stmt.getLong(_columnIndexOfUnitPrice)
          val _tmpUnitCost: Long
          _tmpUnitCost = _stmt.getLong(_columnIndexOfUnitCost)
          _item = TransactionItemEntity(_tmpId,_tmpInvoiceNo,_tmpProductId,_tmpProductName,_tmpQuantity,_tmpUnitPrice,_tmpUnitCost)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun oldTransactionIds(): List<String> {
    val _sql: String = "SELECT invoiceNo FROM transactions ORDER BY createdAt DESC LIMIT -1 OFFSET 5000"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeCashRecords(): Flow<List<CashRecordEntity>> {
    val _sql: String = "SELECT * FROM cash_records ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("cash_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsPrive: Int = getColumnIndexOrThrow(_stmt, "isPrive")
        val _columnIndexOfIsCapital: Int = getColumnIndexOrThrow(_stmt, "isCapital")
        val _result: MutableList<CashRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CashRecordEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsPrive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPrive).toInt()
          _tmpIsPrive = _tmp != 0
          val _tmpIsCapital: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsCapital).toInt()
          _tmpIsCapital = _tmp_1 != 0
          _item = CashRecordEntity(_tmpId,_tmpKind,_tmpCategory,_tmpAmount,_tmpNote,_tmpCreatedAt,_tmpIsPrive,_tmpIsCapital)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun oldCashIds(kind: String): List<String> {
    val _sql: String = "SELECT id FROM cash_records WHERE kind = ? ORDER BY createdAt DESC LIMIT -1 OFFSET 5000"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, kind)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cashRecords(kind: String): List<CashRecordEntity> {
    val _sql: String = "SELECT * FROM cash_records WHERE kind = ? ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, kind)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsPrive: Int = getColumnIndexOrThrow(_stmt, "isPrive")
        val _columnIndexOfIsCapital: Int = getColumnIndexOrThrow(_stmt, "isCapital")
        val _result: MutableList<CashRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CashRecordEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsPrive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPrive).toInt()
          _tmpIsPrive = _tmp != 0
          val _tmpIsCapital: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsCapital).toInt()
          _tmpIsCapital = _tmp_1 != 0
          _item = CashRecordEntity(_tmpId,_tmpKind,_tmpCategory,_tmpAmount,_tmpNote,_tmpCreatedAt,_tmpIsPrive,_tmpIsCapital)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeRestocks(): Flow<List<RestockEntity>> {
    val _sql: String = "SELECT * FROM restocks ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("restocks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfSupplierId: Int = getColumnIndexOrThrow(_stmt, "supplierId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfPurchasePrice: Int = getColumnIndexOrThrow(_stmt, "purchasePrice")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _result: MutableList<RestockEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RestockEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpSupplierId: String
          _tmpSupplierId = _stmt.getText(_columnIndexOfSupplierId)
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpPurchasePrice: Long
          _tmpPurchasePrice = _stmt.getLong(_columnIndexOfPurchasePrice)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          _item = RestockEntity(_tmpId,_tmpProductId,_tmpSupplierId,_tmpQuantity,_tmpPurchasePrice,_tmpTotal,_tmpCreatedAt,_tmpNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun oldRestockIds(): List<String> {
    val _sql: String = "SELECT id FROM restocks ORDER BY createdAt DESC LIMIT -1 OFFSET 5000"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAdjustments(): Flow<List<StockAdjustmentEntity>> {
    val _sql: String = "SELECT * FROM stock_adjustments ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("stock_adjustments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBeforeStock: Int = getColumnIndexOrThrow(_stmt, "beforeStock")
        val _columnIndexOfAfterStock: Int = getColumnIndexOrThrow(_stmt, "afterStock")
        val _columnIndexOfDifference: Int = getColumnIndexOrThrow(_stmt, "difference")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfLossValue: Int = getColumnIndexOrThrow(_stmt, "lossValue")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<StockAdjustmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StockAdjustmentEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpBeforeStock: Int
          _tmpBeforeStock = _stmt.getLong(_columnIndexOfBeforeStock).toInt()
          val _tmpAfterStock: Int
          _tmpAfterStock = _stmt.getLong(_columnIndexOfAfterStock).toInt()
          val _tmpDifference: Int
          _tmpDifference = _stmt.getLong(_columnIndexOfDifference).toInt()
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpLossValue: Long
          _tmpLossValue = _stmt.getLong(_columnIndexOfLossValue)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = StockAdjustmentEntity(_tmpId,_tmpProductId,_tmpBeforeStock,_tmpAfterStock,_tmpDifference,_tmpReason,_tmpNote,_tmpLossValue,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun oldAdjustmentIds(): List<String> {
    val _sql: String = "SELECT id FROM stock_adjustments ORDER BY createdAt DESC LIMIT -1 OFFSET 5000"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeMovements(): Flow<List<StockMovementEntity>> {
    val _sql: String = "SELECT * FROM stock_movements ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("stock_movements")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfBeforeStock: Int = getColumnIndexOrThrow(_stmt, "beforeStock")
        val _columnIndexOfAfterStock: Int = getColumnIndexOrThrow(_stmt, "afterStock")
        val _columnIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<StockMovementEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StockMovementEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpBeforeStock: Int
          _tmpBeforeStock = _stmt.getLong(_columnIndexOfBeforeStock).toInt()
          val _tmpAfterStock: Int
          _tmpAfterStock = _stmt.getLong(_columnIndexOfAfterStock).toInt()
          val _tmpReference: String
          _tmpReference = _stmt.getText(_columnIndexOfReference)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = StockMovementEntity(_tmpId,_tmpProductId,_tmpProductName,_tmpType,_tmpQuantity,_tmpBeforeStock,_tmpAfterStock,_tmpReference,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActivities(): Flow<List<ActivityLogEntity>> {
    val _sql: String = "SELECT * FROM activity_logs ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("activity_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfActor: Int = getColumnIndexOrThrow(_stmt, "actor")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfDetail: Int = getColumnIndexOrThrow(_stmt, "detail")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ActivityLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ActivityLogEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpActor: String
          _tmpActor = _stmt.getText(_columnIndexOfActor)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpDetail: String
          _tmpDetail = _stmt.getText(_columnIndexOfDetail)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = ActivityLogEntity(_tmpId,_tmpActor,_tmpAction,_tmpDetail,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeNotifications(): Flow<List<NotificationEntity>> {
    val _sql: String = "SELECT * FROM notifications ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("notifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _result: MutableList<NotificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: NotificationEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          _item = NotificationEntity(_tmpId,_tmpTitle,_tmpMessage,_tmpType,_tmpCreatedAt,_tmpIsRead)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun resetPassword(id: String, hash: String) {
    val _sql: String = "UPDATE accounts SET passwordHash = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, hash)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setAccountActive(id: String, active: Boolean) {
    val _sql: String = "UPDATE accounts SET active = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (active) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun changeBalance(storeId: String, delta: Long) {
    val _sql: String = "UPDATE stores SET balance = balance + ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, delta)
        _argIndex = 2
        _stmt.bindText(_argIndex, storeId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateBankInfo(storeId: String, info: String) {
    val _sql: String = "UPDATE stores SET bankInfo = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, info)
        _argIndex = 2
        _stmt.bindText(_argIndex, storeId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateQris(storeId: String, uri: String) {
    val _sql: String = "UPDATE stores SET qrisImageUri = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, uri)
        _argIndex = 2
        _stmt.bindText(_argIndex, storeId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearSession() {
    val _sql: String = "DELETE FROM sessions"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun archiveProduct(id: String) {
    val _sql: String = "UPDATE products SET archived = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStock(id: String, stock: Int) {
    val _sql: String = "UPDATE products SET stock = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, stock.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSupplier(id: String) {
    val _sql: String = "DELETE FROM suppliers WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteTransactions(ids: List<String>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM transactions WHERE invoiceNo IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteTransactionItems(ids: List<String>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM transaction_items WHERE invoiceNo IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCashRecords(ids: List<String>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM cash_records WHERE id IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteRestocks(ids: List<String>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM restocks WHERE id IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAdjustments(ids: List<String>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM stock_adjustments WHERE id IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun readAllNotifications() {
    val _sql: String = "UPDATE notifications SET isRead = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun readNotification(id: String) {
    val _sql: String = "UPDATE notifications SET isRead = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
