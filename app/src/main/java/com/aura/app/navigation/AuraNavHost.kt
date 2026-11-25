package com.aura.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aura.app.ui.AuraViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aura.app.navigation.AuraNavHost
import com.aura.app.navigation.AuraBottomBar
import com.aura.app.navigation.Routes
import com.aura.app.ui.components.*
import com.aura.app.ui.theme.*
import com.aura.app.ui.screens.auth.*
import com.aura.app.ui.screens.dashboard.*
import com.aura.app.ui.screens.manajemen.*
import com.aura.app.ui.screens.analisis.*
import com.aura.app.ui.screens.profile.*
import com.aura.app.ui.screens.notifikasi.*
import com.aura.app.ui.screens.keuangan.*
import com.aura.app.ui.screens.barang.*
import com.aura.app.ui.screens.kasir.*
import com.aura.app.R
import com.aura.app.data.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ownerRoutes = setOf(
    Routes.DASHBOARD, Routes.MENU_MANAJEMEN, Routes.ANALISIS, Routes.PROFIL, Routes.NOTIFIKASI,
    Routes.PENGELUARAN, Routes.CATAT_PENGELUARAN, Routes.PEMASUKAN, Routes.CATAT_PEMASUKAN,
    Routes.MANAJEMEN_BARANG, Routes.PROFIL_BARANG, Routes.TAMBAH_BARANG, Routes.CATAT_RESTOCK,
    Routes.PENYESUAIAN_STOK, Routes.PERGERAKAN_STOK, Routes.SUPPLIER, Routes.RIWAYAT_AKTIVITAS,
    Routes.PENGATURAN, Routes.MANAJEMEN_AKUN_KASIR, Routes.METODE_PEMBAYARAN,
    Routes.K4_TRANSAKSI_BERHASIL_OWNER, Routes.RIWAYAT_HARI_INI_OWNER,
)

@Composable
private fun RouteNavHost(
    navController: NavHostController,
    content: @Composable (String) -> Unit,
) {
    NavHost(navController = navController, startDestination = "auth_graph") {
        navigation(startDestination = Routes.WELCOME, route = "auth_graph") {
            composable(Routes.WELCOME) { content(Routes.WELCOME) }
            composable(Routes.LOGIN) { content(Routes.LOGIN) }
            composable(Routes.REGISTER_STEP1) { content(Routes.REGISTER_STEP1) }
            composable(Routes.REGISTER_STEP2) { content(Routes.REGISTER_STEP2) }
        }
        navigation(startDestination = Routes.DASHBOARD, route = "owner_tab_graph") {
            composable(Routes.DASHBOARD) { content(Routes.DASHBOARD) }
            composable(Routes.MENU_MANAJEMEN) { content(Routes.MENU_MANAJEMEN) }
            composable(Routes.ANALISIS) { content(Routes.ANALISIS) }
            composable(Routes.PROFIL) { content(Routes.PROFIL) }
        }
        navigation(startDestination = Routes.MANAJEMEN_BARANG, route = "barang_tab_graph") {
            composable(Routes.MANAJEMEN_BARANG) { content(Routes.MANAJEMEN_BARANG) }
            composable(Routes.PROFIL_BARANG) { content(Routes.PROFIL_BARANG) }
            composable(Routes.TAMBAH_BARANG) { content(Routes.TAMBAH_BARANG) }
            composable(Routes.CATAT_RESTOCK) { content(Routes.CATAT_RESTOCK) }
            composable(Routes.PENYESUAIAN_STOK) { content(Routes.PENYESUAIAN_STOK) }
            composable(Routes.PERGERAKAN_STOK) { content(Routes.PERGERAKAN_STOK) }
            composable(Routes.SUPPLIER) { content(Routes.SUPPLIER) }
        }
        navigation(startDestination = Routes.PENGELUARAN, route = "keuangan_tab_graph") {
            composable(Routes.PENGELUARAN) { content(Routes.PENGELUARAN) }
            composable(Routes.CATAT_PENGELUARAN) { content(Routes.CATAT_PENGELUARAN) }
            composable(Routes.PEMASUKAN) { content(Routes.PEMASUKAN) }
            composable(Routes.CATAT_PEMASUKAN) { content(Routes.CATAT_PEMASUKAN) }
        }
        navigation(startDestination = Routes.K1_BERANDA_KASIR, route = "kasir_tab_graph") {
            composable(Routes.K1_BERANDA_KASIR) { content(Routes.K1_BERANDA_KASIR) }
            composable(Routes.K2_CATAT_TRANSAKSI) { content(Routes.K2_CATAT_TRANSAKSI) }
            composable(Routes.K3_PEMBAYARAN) { content(Routes.K3_PEMBAYARAN) }
            composable(Routes.K4_TRANSAKSI_BERHASIL) { content(Routes.K4_TRANSAKSI_BERHASIL) }
            composable(Routes.K4_TRANSAKSI_BERHASIL_OWNER) { content(Routes.K4_TRANSAKSI_BERHASIL_OWNER) }
            composable(Routes.RIWAYAT_HARI_INI) { content(Routes.RIWAYAT_HARI_INI) }
            composable(Routes.RIWAYAT_HARI_INI_OWNER) { content(Routes.RIWAYAT_HARI_INI_OWNER) }
        }
        navigation(startDestination = Routes.PENGATURAN, route = "profil_tab_graph") {
            composable(Routes.NOTIFIKASI) { content(Routes.NOTIFIKASI) }
            composable(Routes.RIWAYAT_AKTIVITAS) { content(Routes.RIWAYAT_AKTIVITAS) }
            composable(Routes.PENGATURAN) { content(Routes.PENGATURAN) }
            composable(Routes.MANAJEMEN_AKUN_KASIR) { content(Routes.MANAJEMEN_AKUN_KASIR) }
            composable(Routes.METODE_PEMBAYARAN) { content(Routes.METODE_PEMBAYARAN) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraNavHost(vm: AuraViewModel = viewModel()) {
    val session by vm.session.collectAsStateWithLifecycle()
    val store by vm.store.collectAsStateWithLifecycle()
    val products by vm.products.collectAsStateWithLifecycle()
    val suppliers by vm.suppliers.collectAsStateWithLifecycle()
    val transactions by vm.transactions.collectAsStateWithLifecycle()
    val transactionItems by vm.transactionItems.collectAsStateWithLifecycle()
    val adjustments by vm.adjustments.collectAsStateWithLifecycle()
    val cashRecords by vm.cashRecords.collectAsStateWithLifecycle()
    val restocks by vm.restocks.collectAsStateWithLifecycle()
    val movements by vm.movements.collectAsStateWithLifecycle()
    val activities by vm.activities.collectAsStateWithLifecycle()
    val notifications by vm.notifications.collectAsStateWithLifecycle()
    val accounts by vm.accounts.collectAsStateWithLifecycle()
    val cart by vm.cart.collectAsStateWithLifecycle()
    val invoice by vm.lastInvoice.collectAsStateWithLifecycle()
    val lastTotal by vm.lastTotal.collectAsStateWithLifecycle()
    val message by vm.message.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    val ready by vm.ready.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val route = navController.currentBackStackEntryAsState().value?.destination?.route ?: Routes.WELCOME
    val stack = remember { mutableStateListOf<String>() }
    var selectedProductId by rememberSaveable { mutableStateOf("BRG-001") }
    var editProductId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTransaction by rememberSaveable { mutableStateOf("") }
    var showAvatar by remember { mutableStateOf(false) }
    var cancelInvoice by remember { mutableStateOf<String?>(null) }
    var isOwnerCancel by remember { mutableStateOf(true) }
    var cancelReason by rememberSaveable { mutableStateOf("") }
    var hideBalance by rememberSaveable { mutableStateOf(true) }
    var analysisTab by rememberSaveable { mutableStateOf("Ringkasan") }
    var search by rememberSaveable { mutableStateOf("") }
    var registerName by rememberSaveable { mutableStateOf("") }
    var registerEmail by rememberSaveable { mutableStateOf("") }
    var registerPhone by rememberSaveable { mutableStateOf("") }
    var registerPassword by rememberSaveable { mutableStateOf("") }

    fun root(to: String) {
        stack.clear()
        navController.navigate(to) {
            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            launchSingleTop = true
        }
    }
    fun go(to: String) {
        if (session?.role == "kasir" && to in ownerRoutes) {
            vm.showMessage("Hanya Owner yang dapat mengakses menu ini.")
        } else {
            stack.add(route)
            navController.navigate(to) { launchSingleTop = true }
        }
    }
    fun back() {
        if (stack.isNotEmpty()) {
            val target = stack.removeAt(stack.lastIndex)
            navController.popBackStack(target, inclusive = false)
        } else if (session == null && route != Routes.WELCOME) {
            root(Routes.WELCOME)
        }
    }
    LaunchedEffect(session?.id) {
        if (session != null && route in listOf(Routes.WELCOME,Routes.LOGIN,Routes.REGISTER_STEP1,Routes.REGISTER_STEP2)) {
            root(if(session!!.role=="owner") "dashboard" else "k1_beranda_kasir")
        }
    }
    BackHandler(enabled = route != "welcome") { back() }


        Scaffold(
            containerColor=Bg,
            topBar = {
                if (route !in listOf("welcome","login","register_step1","register_step2")) {
                    TopAppBar(
                        title={ Column { Text("AURA",fontSize=10.sp,letterSpacing=2.sp,color=Red,fontWeight=FontWeight.ExtraBold); Text(routeTitle(route),fontSize=18.sp,fontWeight=FontWeight.Bold) } },
                        navigationIcon={ if(stack.isNotEmpty() && !(route=="k1_beranda_kasir" && session?.role=="kasir")) Text("‹",Modifier.clickable { back() }.padding(horizontal=16.dp),fontSize=30.sp,color=Red) },
                        actions={
                            if(route in listOf("k1_beranda_kasir","k2_catat_transaksi","k3_pembayaran","k4_transaksi_berhasil","k4_transaksi_berhasil_owner","riwayat_hari_ini","riwayat_hari_ini_owner")) {
                                Text(if(session?.role=="owner") "OWNER" else "KASIR",Modifier.padding(horizontal=6.dp).clip(CircleShape).background(Soft).padding(horizontal=8.dp,vertical=6.dp),fontSize=9.sp,color=Purple,fontWeight=FontWeight.Bold)
                                Text("●",Modifier.clickable { showAvatar=true }.padding(horizontal=12.dp),color=Purple,fontSize=20.sp)
                            } else if(session!=null) Text(if(session?.role=="owner") "OWNER" else "KASIR",Modifier.padding(horizontal=12.dp).clip(CircleShape).background(Soft).padding(horizontal=10.dp,vertical=6.dp),fontSize=9.sp,color=Purple,fontWeight=FontWeight.Bold)
                        },
                        colors=TopAppBarDefaults.topAppBarColors(containerColor=Color.White)
                    )
                }
            },
            bottomBar = {
                if(session?.role=="owner" && route in listOf("dashboard","menu_manajemen","analisis","profil")) {
                    AuraBottomBar(route) { target, isRoot -> if(isRoot) root(target) else go(target) }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                RouteNavHost(navController) { activeRoute ->
                if (!ready) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color=Red)
                } else when(activeRoute) {
                    Routes.WELCOME -> WelcomeScreen({go("login")},{go("register_step1")})
                    Routes.LOGIN -> LoginScreen({email,pass,role ->
                        if(role=="owner") vm.loginOwner(email,pass) { ok -> if(ok) root("dashboard") }
                        else {
                            val parts=email.split("/")
                            if(parts.size==2) vm.loginCashier(parts[0],parts[1],pass) { ok -> if(ok) root("k1_beranda_kasir") }
                            else vm.loginCashier(store?.id ?: "",email,pass) { ok -> if(ok) root("k1_beranda_kasir") }
                        }
                    },{go("register_step1")})
                    Routes.REGISTER_STEP1 -> RegisterOneScreen(registerName,{registerName=it},registerEmail,{registerEmail=it},registerPhone,{registerPhone=it},registerPassword,{registerPassword=it},{go("register_step2")},{back()})
                    Routes.REGISTER_STEP2 -> RegisterTwoScreen({name,type,balance -> vm.register(registerName,registerEmail,registerPhone,registerPassword,name,type,balance) { ok -> if(ok) root("dashboard") }},{back()})
                    "dashboard" -> DashboardScreen(store,products,transactions,cashRecords,hideBalance,{hideBalance=!hideBalance},{go(it)})
                    "menu_manajemen" -> ManagementScreen({go(it)})
                    "analisis" -> AnalysisScreen(analysisTab,{analysisTab=it},products,transactions,transactionItems,cashRecords,adjustments,restocks,movements)
                    "profil" -> ProfileScreen(session?.name ?: "",session?.role ?: "owner",store?.id ?: "",{go("pengaturan")},{showAvatar=true},{vm.logout();root("welcome")})
                    "notifikasi" -> NotificationScreen(notifications,{vm.readNotification(it)},{vm.readAllNotifications()},{go(it)})
                    "pengeluaran" -> ExpenseListScreen(cashRecords.filter{it.kind=="expense"},restocks,products,{go("catat_pengeluaran")})
                    "catat_pengeluaran" -> ExpenseFormScreen({category,amount,note,prive -> vm.expense(category,amount,note,prive); back()},{back()})
                    "pemasukan" -> IncomeListScreen(transactions,cashRecords.filter{it.kind=="income"},{go("catat_pemasukan")})
                    "catat_pemasukan" -> IncomeFormScreen({category,amount,note,capital -> vm.income(category,amount,note,capital); back()},{back()})
                    "manajemen_barang" -> ProductListScreen(products,search,{search=it},{selectedProductId=it;go("profil_barang")},{editProductId=null;go("tambah_barang")},{go("catat_restock")})
                    "profil_barang" -> ProductDetailScreen(products.firstOrNull{it.id==selectedProductId},{go("catat_restock")},{editProductId=selectedProductId;go("tambah_barang")},{go("penyesuaian_stok")},{go("pergerakan_stok")},{vm.archiveProduct(selectedProductId);back()})
                    "tambah_barang" -> ProductFormScreen(products.firstOrNull{it.id==editProductId}, {sku,name,categories,unit,sell,hpp,stock,min ->
                        vm.saveProduct(editProductId,sku,name,categories,unit,sell,hpp,stock,min); back()
                    },{back()})
                    "catat_restock" -> RestockScreen(products,suppliers,{pid,qty,price,sid,note -> vm.restock(pid,qty,price,sid,note);back()},{back()})
                    "penyesuaian_stok" -> AdjustmentScreen(products,{pid,stock,reason,note -> vm.adjustStock(pid,stock,reason,note);back()},{back()})
                    "pergerakan_stok" -> MovementScreen(movements)
                    "supplier" -> SupplierScreen(suppliers,{id,name,phone,address,note -> vm.saveSupplier(id,name,phone,address,note)},{vm.deleteSupplier(it)})
                    "riwayat_aktivitas" -> ActivityScreen(activities)
                    "pengaturan" -> SettingsScreen({go("manajemen_akun_kasir")},{go("metode_pembayaran")})
                    "manajemen_akun_kasir" -> CashierAccountsScreen(accounts.filter{it.role=="kasir"},{id,name,user,pass,phone -> vm.saveCashier(id,name,user,pass,phone)},{id,active -> vm.setCashierActive(id,active)},{id,pass -> vm.resetCashierPassword(id,pass)})
                    "metode_pembayaran" -> PaymentSettingsScreen(store?.bankInfo ?: "",{vm.updateBankInfo(it)},{vm.updateQris(it)})
                    "k1_beranda_kasir" -> CashierScreen(products,search,{search=it},{vm.addToCart(it)},{go("k2_catat_transaksi")},{go("riwayat_hari_ini")},{showAvatar=true})
                    "k2_catat_transaksi" -> CartScreen(cart,{id,delta -> vm.changeCart(id,delta)},{go("k3_pembayaran")},{back()})
                    "k3_pembayaran" -> PaymentScreen(cart.sumOf{it.unitPrice*it.quantity},{method,received -> vm.checkout(method,received) {
                        go(if(session?.role=="owner") "k4_transaksi_berhasil_owner" else "k4_transaksi_berhasil")
                    }},{back()})
                    "k4_transaksi_berhasil","k4_transaksi_berhasil_owner" -> SuccessScreen(invoice,lastTotal,session?.role=="owner",{root("k1_beranda_kasir")},{go(if(session?.role=="owner") "riwayat_hari_ini_owner" else "riwayat_hari_ini")},{root("dashboard")})
                    "riwayat_hari_ini","riwayat_hari_ini_owner" -> HistoryScreen(transactions,{tx ->
                        selectedTransaction=tx.invoiceNo; isOwnerCancel=session?.role=="owner"; cancelInvoice=tx.invoiceNo
                    },session?.role=="kasir")
                    else -> WelcomeScreen({root("login")},{root("register_step1")})
                }
                if(busy) LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter),color=Red)
                }
            }
        }
        if(showAvatar) ModalBottomSheet(onDismissRequest={showAvatar=false},containerColor=Color.White) {
            Column(Modifier.fillMaxWidth().padding(22.dp)) {
                Text(session?.name ?: "Pengguna",fontWeight=FontWeight.Bold,fontSize=18.sp)
                Text("ID Toko: ${store?.id ?: "-"} • ${session?.role ?: ""}",fontSize=12.sp,color=Muted)
                Spacer(Modifier.height(18.dp))
                ActionRow("Ganti Akun","Masuk menggunakan akun lain") { showAvatar=false; vm.logout(); root("login") }
                ActionRow("Keluar Akun","Akhiri sesi perangkat") { showAvatar=false; vm.logout(); root("welcome") }
                Spacer(Modifier.height(25.dp))
            }
        }
        if(cancelInvoice!=null) AlertDialog(
            onDismissRequest={cancelInvoice=null;cancelReason=""},
            title={Text(if(isOwnerCancel) "Batalkan Transaksi?" else "Ajukan Pembatalan?")},
            text={Column { Text("Alasan wajib diisi. Stok akan dikembalikan setelah pembatalan Owner disetujui.",fontSize=12.sp); OutlinedTextField(cancelReason,{cancelReason=it},label={Text("Alasan pembatalan")},modifier=Modifier.fillMaxWidth()) }},
            confirmButton={TextButton(onClick={
                if(cancelReason.isNotBlank()) { vm.cancelTransaction(cancelInvoice!!,cancelReason,!isOwnerCancel); cancelInvoice=null;cancelReason="" }
            }) {Text(if(isOwnerCancel) "Batalkan" else "Kirim Pengajuan",color=Red)}},
            dismissButton={TextButton(onClick={cancelInvoice=null;cancelReason=""}) {Text("Kembali")}}
        )
        if(message!=null) AlertDialog(onDismissRequest={vm.dismissMessage()},title={Text("AURA")},text={Text(message ?: "")},confirmButton={TextButton(onClick={vm.dismissMessage()}){Text("OK",color=Red)}})
}

private fun routeTitle(route:String)=mapOf(
    "dashboard" to "Dashboard","menu_manajemen" to "Menu Manajemen","analisis" to "Analisis Bisnis","profil" to "Profil",
    "notifikasi" to "Notifikasi","pengeluaran" to "Pengeluaran","catat_pengeluaran" to "Catat Pengeluaran",
    "pemasukan" to "Pemasukan","catat_pemasukan" to "Catat Pemasukan","manajemen_barang" to "Manajemen Barang",
    "profil_barang" to "Profil Barang","tambah_barang" to "Tambah / Edit Barang","catat_restock" to "Catat Restock",
    "penyesuaian_stok" to "Penyesuaian Stok","pergerakan_stok" to "Pergerakan Stok","supplier" to "Supplier",
    "riwayat_aktivitas" to "Riwayat Aktivitas","pengaturan" to "Pengaturan","manajemen_akun_kasir" to "Akun Kasir",
    "metode_pembayaran" to "Metode Pembayaran","k1_beranda_kasir" to "Beranda Kasir","k2_catat_transaksi" to "Keranjang",
    "k3_pembayaran" to "Pembayaran","k4_transaksi_berhasil" to "Transaksi Berhasil","k4_transaksi_berhasil_owner" to "Transaksi Berhasil","riwayat_hari_ini" to "Riwayat Hari Ini","riwayat_hari_ini_owner" to "Riwayat Hari Ini"
)[route] ?: "AURA"
