package com.aura.app.ui

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aura.app.R
import com.aura.app.data.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Red = Color(0xFF930000)
private val DarkRed = Color(0xFF680000)
private val Purple = Color(0xFF311A37)
private val Bg = Color(0xFFFCF9F8)
private val Soft = Color(0xFFF6F3F2)
private val Ink = Color(0xFF1B1B1C)
private val Muted = Color(0xFF765F5B)
private val Green = Color(0xFF16834A)
private fun money(v: Long) = "Rp " + NumberFormat.getNumberInstance(Locale("id","ID")).format(v)
private fun date(v: Long) = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id","ID")).format(Date(v))

private val ownerRoutes = setOf("dashboard","menu_manajemen","analisis","profil","notifikasi","pengeluaran",
    "catat_pengeluaran","pemasukan","catat_pemasukan","manajemen_barang","profil_barang","tambah_barang",
    "catat_restock","penyesuaian_stok","pergerakan_stok","supplier","riwayat_aktivitas","pengaturan",
    "manajemen_akun_kasir","metode_pembayaran","k4_transaksi_berhasil_owner","riwayat_hari_ini_owner")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraApp(vm: AuraViewModel = viewModel()) {
    val session by vm.session.collectAsState()
    val store by vm.store.collectAsState()
    val products by vm.products.collectAsState()
    val suppliers by vm.suppliers.collectAsState()
    val transactions by vm.transactions.collectAsState()
    val transactionItems by vm.transactionItems.collectAsState()
    val adjustments by vm.adjustments.collectAsState()
    val cashRecords by vm.cashRecords.collectAsState()
    val restocks by vm.restocks.collectAsState()
    val movements by vm.movements.collectAsState()
    val activities by vm.activities.collectAsState()
    val notifications by vm.notifications.collectAsState()
    val accounts by vm.accounts.collectAsState()
    val cart by vm.cart.collectAsState()
    val invoice by vm.lastInvoice.collectAsState()
    val lastTotal by vm.lastTotal.collectAsState()
    val message by vm.message.collectAsState()
    val busy by vm.busy.collectAsState()
    val ready by vm.ready.collectAsState()

    var route by rememberSaveable { mutableStateOf("welcome") }
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

    fun root(to: String) { stack.clear(); route=to }
    fun go(to: String) {
        if (session?.role == "kasir" && to in ownerRoutes) {
            vm.showMessage("Hanya Owner yang dapat mengakses menu ini.")
        } else { stack.add(route); route=to }
    }
    fun back() { if (stack.isNotEmpty()) route=stack.removeAt(stack.lastIndex) else if(session==null) route="welcome" }
    LaunchedEffect(session?.id) {
        if (session != null && route in listOf("welcome","login","register_step1","register_step2")) {
            root(if(session!!.role=="owner") "dashboard" else "k1_beranda_kasir")
        }
    }
    BackHandler(enabled = route != "welcome") { back() }

    MaterialTheme(colorScheme = lightColorScheme(primary=Red,onPrimary=Color.White,background=Bg,surface=Color.White,onSurface=Ink)) {
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
                    NavigationBar(containerColor=Color.White) {
                        NavigationBarItem(route=="dashboard",{root("dashboard")},icon={Text("⌂")},label={Text("Home",fontSize=10.sp)})
                        NavigationBarItem(route=="menu_manajemen",{root("menu_manajemen")},icon={Text("▦")},label={Text("Manajemen",fontSize=10.sp)})
                        NavigationBarItem(false,{go("k1_beranda_kasir")},icon={Text("+",fontSize=22.sp,color=Color.White,modifier=Modifier.clip(CircleShape).background(Red).padding(horizontal=12.dp, vertical=2.dp))},label={Text("Kasir",fontSize=10.sp)})
                        NavigationBarItem(route=="analisis",{root("analisis")},icon={Text("▥")},label={Text("Analisis",fontSize=10.sp)})
                        NavigationBarItem(route=="profil",{root("profil")},icon={Text("●")},label={Text("Profil",fontSize=10.sp)})
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                if (!ready) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color=Red)
                } else when(route) {
                    "welcome" -> WelcomePage({go("login")},{go("register_step1")})
                    "login" -> LoginPage({email,pass,role ->
                        if(role=="owner") vm.loginOwner(email,pass) { ok -> if(ok) root("dashboard") }
                        else {
                            val parts=email.split("/")
                            if(parts.size==2) vm.loginCashier(parts[0],parts[1],pass) { ok -> if(ok) root("k1_beranda_kasir") }
                            else vm.loginCashier(store?.id ?: "",email,pass) { ok -> if(ok) root("k1_beranda_kasir") }
                        }
                    },{go("register_step1")})
                    "register_step1" -> RegisterOnePage(registerName,{registerName=it},registerEmail,{registerEmail=it},registerPhone,{registerPhone=it},registerPassword,{registerPassword=it},{go("register_step2")},{back()})
                    "register_step2" -> RegisterTwoPage({name,type,balance -> vm.register(registerName,registerEmail,registerPhone,registerPassword,name,type,balance) { ok -> if(ok) root("dashboard") }},{back()})
                    "dashboard" -> DashboardPage(store,products,transactions,cashRecords,hideBalance,{hideBalance=!hideBalance},{go(it)})
                    "menu_manajemen" -> ManagementPage({go(it)})
                    "analisis" -> AnalysisPage(analysisTab,{analysisTab=it},products,transactions,transactionItems,cashRecords,adjustments,restocks,movements)
                    "profil" -> ProfilePage(session?.name ?: "",session?.role ?: "owner",store?.id ?: "",{go("pengaturan")},{showAvatar=true},{vm.logout();root("welcome")})
                    "notifikasi" -> NotificationPage(notifications,{vm.readNotification(it)},{vm.readAllNotifications()},{go(it)})
                    "pengeluaran" -> ExpenseListPage(cashRecords.filter{it.kind=="expense"},restocks,products,{go("catat_pengeluaran")})
                    "catat_pengeluaran" -> ExpenseFormPage({category,amount,note,prive -> vm.expense(category,amount,note,prive); back()},{back()})
                    "pemasukan" -> IncomeListPage(transactions,cashRecords.filter{it.kind=="income"},{go("catat_pemasukan")})
                    "catat_pemasukan" -> IncomeFormPage({category,amount,note,capital -> vm.income(category,amount,note,capital); back()},{back()})
                    "manajemen_barang" -> ProductListPage(products,search,{search=it},{selectedProductId=it;go("profil_barang")},{editProductId=null;go("tambah_barang")},{go("catat_restock")})
                    "profil_barang" -> ProductDetailPage(products.firstOrNull{it.id==selectedProductId},{go("catat_restock")},{editProductId=selectedProductId;go("tambah_barang")},{go("penyesuaian_stok")},{go("pergerakan_stok")},{vm.archiveProduct(selectedProductId);back()})
                    "tambah_barang" -> ProductFormPage(products.firstOrNull{it.id==editProductId}, {sku,name,categories,unit,sell,hpp,stock,min ->
                        vm.saveProduct(editProductId,sku,name,categories,unit,sell,hpp,stock,min); back()
                    },{back()})
                    "catat_restock" -> RestockPage(products,suppliers,{pid,qty,price,sid,note -> vm.restock(pid,qty,price,sid,note);back()},{back()})
                    "penyesuaian_stok" -> AdjustmentPage(products,{pid,stock,reason,note -> vm.adjustStock(pid,stock,reason,note);back()},{back()})
                    "pergerakan_stok" -> MovementPage(movements)
                    "supplier" -> SupplierPage(suppliers,{id,name,phone,address,note -> vm.saveSupplier(id,name,phone,address,note)},{vm.deleteSupplier(it)})
                    "riwayat_aktivitas" -> ActivityPage(activities)
                    "pengaturan" -> SettingsPage({go("manajemen_akun_kasir")},{go("metode_pembayaran")})
                    "manajemen_akun_kasir" -> CashierAccountsPage(accounts.filter{it.role=="kasir"},{id,name,user,pass,phone -> vm.saveCashier(id,name,user,pass,phone)},{id,active -> vm.setCashierActive(id,active)},{id,pass -> vm.resetCashierPassword(id,pass)})
                    "metode_pembayaran" -> PaymentSettingsPage(store?.bankInfo ?: "",{vm.updateBankInfo(it)},{vm.updateQris(it)})
                    "k1_beranda_kasir" -> CashierPage(products,search,{search=it},{vm.addToCart(it)},{go("k2_catat_transaksi")},{go("riwayat_hari_ini")},{showAvatar=true})
                    "k2_catat_transaksi" -> CartPage(cart,{id,delta -> vm.changeCart(id,delta)},{go("k3_pembayaran")},{back()})
                    "k3_pembayaran" -> PaymentPage(cart.sumOf{it.unitPrice*it.quantity},{method,received -> vm.checkout(method,received) {
                        go(if(session?.role=="owner") "k4_transaksi_berhasil_owner" else "k4_transaksi_berhasil")
                    }},{back()})
                    "k4_transaksi_berhasil","k4_transaksi_berhasil_owner" -> SuccessPage(invoice,lastTotal,session?.role=="owner",{root("k1_beranda_kasir")},{go(if(session?.role=="owner") "riwayat_hari_ini_owner" else "riwayat_hari_ini")},{root("dashboard")})
                    "riwayat_hari_ini","riwayat_hari_ini_owner" -> HistoryPage(transactions,{tx ->
                        selectedTransaction=tx.invoiceNo; isOwnerCancel=session?.role=="owner"; cancelInvoice=tx.invoiceNo
                    },session?.role=="kasir")
                    else -> WelcomePage({root("login")},{root("register_step1")})
                }
                if(busy) LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter),color=Red)
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

@Composable private fun Page(content:@Composable ColumnScope.()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp),content=content)
}
@Composable private fun Heading(title:String,subtitle:String?=null) {
    Column(Modifier.padding(bottom=12.dp)) { Text(title,fontSize=18.sp,fontWeight=FontWeight.Bold,color=Ink); if(subtitle!=null) Text(subtitle,fontSize=12.sp,color=Muted) }
}
@Composable private fun Panel(content:@Composable ColumnScope.()->Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom=10.dp).clip(RoundedCornerShape(20.dp)).background(Color.White)
        .border(1.dp,Color(0xFFEFE7E5),RoundedCornerShape(20.dp)).padding(15.dp),content=content)
}
@Composable private fun Primary(text:String,onClick:()->Unit,enabled:Boolean=true) {
    Button(onClick,Modifier.fillMaxWidth().height(50.dp),enabled=enabled,shape=RoundedCornerShape(15.dp),
        colors=ButtonDefaults.buttonColors(containerColor=Red)) { Text(text,fontWeight=FontWeight.Bold) }
}
@Composable private fun Secondary(text:String,onClick:()->Unit) {
    OutlinedButton(onClick,Modifier.fillMaxWidth().height(48.dp),shape=RoundedCornerShape(15.dp)) { Text(text,color=Red,fontWeight=FontWeight.Bold) }
}
@Composable private fun Metric(label:String,value:String,detail:String?=null) {
    Panel { Text(label,fontSize=11.sp,color=Muted); Text(value,fontSize=20.sp,fontWeight=FontWeight.ExtraBold,color=Ink); if(detail!=null) Text(detail,fontSize=10.sp,color=Green) }
}
@Composable private fun Input(label:String,value:String,onChange:(String)->Unit,number:Boolean=false,password:Boolean=false,placeholder:String="") {
    Column(Modifier.fillMaxWidth().padding(bottom=10.dp)) {
        Text(label,fontSize=12.sp,fontWeight=FontWeight.Bold,color=Ink)
        OutlinedTextField(value,onChange,Modifier.fillMaxWidth(),singleLine=true,
            placeholder={Text(placeholder.ifBlank{"Masukkan $label"},fontSize=12.sp)},
            keyboardOptions=if(number) KeyboardOptions(keyboardType=KeyboardType.Number) else KeyboardOptions.Default,
            visualTransformation=if(password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            shape=RoundedCornerShape(13.dp))
    }
}
@Composable private fun ActionRow(title:String,detail:String,onClick:()->Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical=5.dp).clip(RoundedCornerShape(15.dp)).background(Color.White)
        .clickable{onClick()}.padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title,fontWeight=FontWeight.Bold,fontSize=13.sp); Text(detail,fontSize=10.sp,color=Muted) }
        Text("›",fontSize=23.sp,color=Red)
    }
}
@Composable private fun WelcomePage(login:()->Unit,register:()->Unit) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Purple,Color(0xFF4E0A24),Color(0xFF75000C),Red)))) {
        Column(Modifier.align(Alignment.Center).padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally) {
            Box(Modifier.size(92.dp).clip(RoundedCornerShape(26.dp)).background(Color.White.copy(.12f)),contentAlignment=Alignment.Center){Text("A",fontSize=58.sp,color=Color.White,fontWeight=FontWeight.ExtraBold)}
            Spacer(Modifier.height(20.dp)); Text("AURA",fontSize=36.sp,color=Color.White,fontWeight=FontWeight.ExtraBold,letterSpacing=5.sp)
            Text("Kembangkan Bisnis Anda Lebih Cepat",fontSize=20.sp,color=Color.White,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
            Text("Kelola inventaris, transaksi, dan keuangan toko dalam satu aplikasi.",fontSize=13.sp,color=Color.White.copy(.8f),textAlign=TextAlign.Center)
        }
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp)) {
            Button(login,Modifier.fillMaxWidth().height(50.dp),colors=ButtonDefaults.buttonColors(containerColor=Color.White),shape=RoundedCornerShape(15.dp)){Text("Masuk",color=Red,fontWeight=FontWeight.Bold)}
            Spacer(Modifier.height(10.dp))
            OutlinedButton(register,Modifier.fillMaxWidth().height(50.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=Color.White),shape=RoundedCornerShape(15.dp)){Text("Buat Akun",fontWeight=FontWeight.Bold)}
            Text("AURA • Retail Management",Modifier.fillMaxWidth().padding(top=18.dp),textAlign=TextAlign.Center,color=Color.White.copy(.7f),fontSize=10.sp)
        }
    }
}
@Composable private fun LoginPage(onLogin:(String,String,String)->Unit,onRegister:()->Unit) {
    var role by rememberSaveable{mutableStateOf("owner")}
    var identity by rememberSaveable{mutableStateOf("owner@tokoberkah.com")}
    var password by rememberSaveable{mutableStateOf("123456")}
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Purple,Color(0xFF650014),Red)))) {
        Column(Modifier.fillMaxWidth().align(Alignment.Center).padding(24.dp)) {
            Text("AURA",fontSize=30.sp,color=Color.White,fontWeight=FontWeight.ExtraBold,letterSpacing=4.sp)
            Text("Masuk ke AURA",fontSize=23.sp,color=Color.White,fontWeight=FontWeight.Bold)
            Text("Gunakan akun lokal untuk melanjutkan",fontSize=12.sp,color=Color.White.copy(.8f))
            Spacer(Modifier.height(20.dp))
            Row(Modifier.clip(CircleShape).background(Color.White.copy(.15f)).padding(4.dp)) {
                listOf("owner" to "Owner","kasir" to "Kasir").forEach{(v,t)->
                    Box(Modifier.weight(1f).clip(CircleShape).background(if(role==v)Color.White else Color.Transparent).clickable{role=v;identity=if(v=="owner")"owner@tokoberkah.com" else "siti.aminah"}.padding(12.dp),contentAlignment=Alignment.Center){Text(t,color=if(role==v)Red else Color.White,fontWeight=FontWeight.Bold)}
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(if(role=="owner")"Email" else "Username Kasir (format IDTOKO/username)",color=Color.White,fontSize=12.sp)
            OutlinedTextField(identity,{identity=it},Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(12.dp))
            Spacer(Modifier.height(10.dp)); Text("Kata Sandi",color=Color.White,fontSize=12.sp)
            OutlinedTextField(password,{password=it},Modifier.fillMaxWidth(),singleLine=true,visualTransformation=PasswordVisualTransformation(),shape=RoundedCornerShape(12.dp))
            Spacer(Modifier.height(18.dp)); Primary("Masuk sebagai ${if(role=="owner")"Owner" else "Kasir"}",{onLogin(identity,password,role)})
            Text("Belum punya akun? Buat Akun",Modifier.fillMaxWidth().clickable{onRegister()}.padding(14.dp),textAlign=TextAlign.Center,color=Color.White,fontSize=12.sp)
            Text("Akun demo: owner@tokoberkah.com / 123456\nKasir: AB12CD34/siti.aminah / 123456",fontSize=10.sp,color=Color.White.copy(.72f),textAlign=TextAlign.Center)
        }
    }
}
@Composable private fun RegisterOnePage(name:String,setName:(String)->Unit,email:String,setEmail:(String)->Unit,phone:String,setPhone:(String)->Unit,password:String,setPassword:(String)->Unit,next:()->Unit,back:()->Unit) {
    Page { Heading("Daftar Akun AURA","Langkah 1 dari 2 • Informasi akun Owner")
        Input("Nama Lengkap",name,setName); Input("Email",email,setEmail); Input("Nomor HP",phone,setPhone,number=true); Input("Kata Sandi",password,setPassword,password=true)
        Primary("Lanjutkan",next,name.isNotBlank()&&email.contains("@")&&password.length>=6); Secondary("Kembali",back)
    }
}
@Composable private fun RegisterTwoPage(done:(String,String,Long)->Unit,back:()->Unit) {
    var store by rememberSaveable{mutableStateOf("")}; var type by rememberSaveable{mutableStateOf("Retail / Sembako")}; var balance by rememberSaveable{mutableStateOf("0")}
    Page { Heading("Isi Profil Toko","Langkah 2 dari 2 • Identitas toko"); Input("Nama Toko",store,{store=it}); Input("Jenis Usaha",type,{type=it}); Input("Saldo Awal",balance,{balance=it},number=true)
        Primary("Selesaikan Pendaftaran",{done(store,type,balance.toLongOrNull()?:0)},store.isNotBlank()); Secondary("Kembali",back)
    }
}
@Composable private fun DashboardPage(store:StoreEntity?,products:List<ProductEntity>,tx:List<TransactionEntity>,records:List<CashRecordEntity>,hide:Boolean,toggle:()->Unit,go:(String)->Unit) {
    val today=tx.filter{it.status=="Selesai"}
    val expenses=records.filter{it.kind=="expense"&&!it.isPrive}.sumOf{it.amount}
    Page {
        Text("Selamat datang kembali",fontSize=12.sp,color=Muted)
        Heading(store?.name ?: "Ringkasan Toko","${store?.businessType ?: "Retail"} • ${store?.id ?: ""}")
        Panel {
            Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Saldo Kas Toko",fontSize=11.sp,color=Muted);Text(if(hide)"Rp ••••••••" else money(store?.balance?:0),fontSize=25.sp,fontWeight=FontWeight.ExtraBold,color=Red)};Text(if(hide)"◉" else "◎",Modifier.clickable{toggle()}.padding(8.dp),fontSize=22.sp,color=Red)}
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Box(Modifier.weight(1f)){Metric("Penjualan Tercatat",money(today.sumOf{it.total}),"${today.size} transaksi")};Box(Modifier.weight(1f)){Metric("Pengeluaran Operasional",money(expenses))}}
        Heading("Perlu Perhatian")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Box(Modifier.weight(1f)){Metric("Stok Menipis",products.count{it.stock in 1..it.minStock}.toString(),"Perlu restock")};Box(Modifier.weight(1f)){Metric("Stok Habis",products.count{it.stock==0}.toString(),"Segera isi")}}
        Heading("Akses Cepat")
        listOf("Manajemen Barang" to "manajemen_barang","Catat Restock" to "catat_restock","Pengeluaran" to "pengeluaran","Pemasukan" to "pemasukan","Riwayat Hari Ini" to "riwayat_hari_ini","Notifikasi" to "notifikasi").forEach{ActionRow(it.first,"Buka modul") {go(it.second)}}
    }
}
@Composable private fun ManagementPage(go:(String)->Unit) {
    Page { Heading("Menu Manajemen","Kelola seluruh operasional toko")
        listOf("Buka menu kasir" to "k1_beranda_kasir","Daftar barang, stok" to "manajemen_barang","Riwayat keluar-masuk" to "pergerakan_stok","Daftar pemasok" to "supplier","Pengeluaran" to "pengeluaran","Pemasukan" to "pemasukan","Log audit seluruh aktivitas" to "riwayat_aktivitas","Pengaturan toko" to "pengaturan").forEach{ActionRow(it.first,"Kelola ${it.first.lowercase()}"){go(it.second)}}
    }
}
@Composable private fun AnalysisPage(tab:String,setTab:(String)->Unit,products:List<ProductEntity>,tx:List<TransactionEntity>,items:List<TransactionItemEntity>,records:List<CashRecordEntity>,adjustments:List<StockAdjustmentEntity>,restocks:List<RestockEntity>,movements:List<StockMovementEntity>) {
    val successful=tx.filter{it.status=="Selesai"}
    val revenue=successful.sumOf{it.total}
    val successfulInvoices=successful.map{it.invoiceNo}.toSet()
    val costOfGoods=items.filter{it.invoiceNo in successfulInvoices}.sumOf{it.unitCost*it.quantity}
    val expense=records.filter{it.kind=="expense"&&!it.isPrive}.sumOf{it.amount}
    val stockLoss=adjustments.sumOf{it.lossValue}
    val capital=records.filter{it.kind=="income"&&it.isCapital}.sumOf{it.amount}
    Page { Heading("Analisis Bisnis","Data dihitung dari database lokal")
        Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){listOf("Ringkasan","Penjualan","Stok","Prediksi").forEach{FilterChip(selected=tab==it,onClick={setTab(it)},label={Text(it,fontSize=10.sp)})}}
        Spacer(Modifier.height(12.dp))
        when(tab){
            "Ringkasan"->{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Box(Modifier.weight(1f)){Metric("Omzet",money(revenue))};Box(Modifier.weight(1f)){Metric("HPP Terjual",money(costOfGoods))}};Metric("Estimasi Laba Bersih",money(revenue-costOfGoods-expense-stockLoss),"Pengeluaran: ${money(expense)} • Kerugian stok: ${money(stockLoss)}");Text("Pemasukan modal dikecualikan: ${money(capital)}",fontSize=10.sp,color=Muted)}
            "Penjualan"->{Metric("Total Penjualan",money(revenue));Metric("HPP Barang Terjual",money(costOfGoods));Metric("Jumlah Transaksi",successful.size.toString())}
            "Stok"->{Metric("Jumlah Produk",products.size.toString());Metric("Nilai Inventaris",money(products.sumOf{it.stock.toLong()*it.hpp}));Metric("Produk Fast Moving",products.sortedByDescending{it.sold30Days}.firstOrNull()?.name ?: "-")}
            else->{Metric("Prediksi Sederhana",money((revenue*1.08).toLong()),"Estimasi +8% dari total transaksi tersimpan");Text("Prediksi ini merupakan estimasi sederhana dari data lokal, bukan model machine learning.",fontSize=11.sp,color=Muted)}
        }
        Heading("Buffer Rolling Window")
        val counters=listOf("Transaksi" to tx.size,"Restock" to restocks.size,"Penyesuaian Stok" to adjustments.size,
            "Pengeluaran Kustom" to records.count{it.kind=="expense"},"Pemasukan Kustom" to records.count{it.kind=="income"})
        counters.forEach { (label,count) ->
            Panel { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text(label,fontSize=11.sp); Text("$count / 5.000",fontSize=11.sp,fontWeight=FontWeight.Bold) }
                LinearProgressIndicator(progress=count/5000f,modifier=Modifier.fillMaxWidth().padding(top=6.dp),color=Red)
            }
        }
        Heading("Ringkasan Stok")
        products.sortedBy{it.stock-it.minStock}.take(5).forEach{ProductLine(it,{})}
    }
}
@Composable private fun ProfilePage(name:String,role:String,storeId:String,settings:()->Unit,switch:()->Unit,logout:()->Unit) {
    Page { Panel { Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(56.dp).clip(CircleShape).background(Purple),contentAlignment=Alignment.Center){Text(name.take(1),color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold)};Spacer(Modifier.width(12.dp));Column{Text(name,fontWeight=FontWeight.Bold,fontSize=16.sp);Text(if(role=="owner")"Owner Toko" else "Kasir",fontSize=12.sp,color=Muted);Text("ID Toko: $storeId",fontSize=11.sp,color=Muted)}}}
        ActionRow("Pengaturan Toko","Akun kasir dan metode pembayaran",settings);ActionRow("Ganti Akun","Beralih sesi perangkat",switch);ActionRow("Keluar Akun","Akhiri sesi AURA",logout)
    }
}
@Composable private fun NotificationPage(items:List<NotificationEntity>,read:(String)->Unit,readAll:()->Unit,go:(String)->Unit) {
    Page { Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f)){Heading("Notifikasi","Pembaruan kondisi toko")};Text("Baca semua",Modifier.clickable{readAll()},fontSize=11.sp,color=Red)}
        items.forEach{Panel{Text(it.title,fontWeight=FontWeight.Bold);Text(it.message,fontSize=12.sp,color=Muted);Text(date(it.createdAt),fontSize=10.sp,color=Muted);if(!it.isRead)Text("Tandai dibaca",Modifier.clickable{read(it.id)}.padding(top=8.dp),color=Red,fontSize=11.sp,fontWeight=FontWeight.Bold)}}
        if(items.isEmpty())Text("Belum ada notifikasi.",color=Muted)
    }
}
@Composable private fun CashRecordListPage(title:String,records:List<CashRecordEntity>,add:()->Unit) {
    Page { Heading("Riwayat $title","Catatan tersimpan di database lokal");Primary("Catat $title",add)
        val total=records.sumOf{it.amount};Spacer(Modifier.height(12.dp));Metric("Total $title",money(total))
        records.forEach{Panel{Text(it.category,fontWeight=FontWeight.Bold);Text(it.note.ifBlank{"Tanpa catatan"},fontSize=11.sp,color=Muted);Text(date(it.createdAt),fontSize=10.sp,color=Muted);Text(money(it.amount),fontWeight=FontWeight.Bold,color=Red);if(it.isPrive)Text("Prive • dikecualikan dari laba bersih",fontSize=10.sp,color=Muted);if(it.isCapital)Text("Modal • dikecualikan dari omzet/laba",fontSize=10.sp,color=Muted)}}
        if(records.isEmpty())Text("Belum ada catatan.",color=Muted)
    }
}


@Composable private fun ExpenseListPage(records:List<CashRecordEntity>,restocks:List<RestockEntity>,products:List<ProductEntity>,add:()->Unit) {
    val total = records.sumOf{it.amount} + restocks.sumOf{it.total}
    Page {
        Heading("Riwayat Pengeluaran","Pengeluaran kustom dan pembelian restock")
        Primary("Catat Pengeluaran",add)
        Spacer(Modifier.height(10.dp))
        Metric("Total Pengeluaran Tercatat",money(total))
        restocks.forEach { restock ->
            Panel {
                Text("Pembelian Stok • ${products.firstOrNull{it.id==restock.productId}?.name ?: restock.productId}",fontWeight=FontWeight.Bold)
                Text("${restock.quantity} unit • ${money(restock.purchasePrice)} / unit",fontSize=10.sp,color=Muted)
                Text(date(restock.createdAt),fontSize=10.sp,color=Muted)
                Text(money(restock.total),fontWeight=FontWeight.Bold,color=Red)
            }
        }
        records.forEach { record ->
            Panel {
                Text(record.category,fontWeight=FontWeight.Bold)
                Text(record.note.ifBlank{"Tanpa catatan"},fontSize=11.sp,color=Muted)
                Text(date(record.createdAt),fontSize=10.sp,color=Muted)
                Text(money(record.amount),fontWeight=FontWeight.Bold,color=Red)
                if(record.isPrive) Text("Prive • dikecualikan dari laba bersih",fontSize=10.sp,color=Muted)
            }
        }
    }
}
@Composable private fun IncomeListPage(transactions:List<TransactionEntity>,records:List<CashRecordEntity>,add:()->Unit) {
    val total = transactions.filter{it.status=="Selesai"}.sumOf{it.total} + records.sumOf{it.amount}
    Page {
        Heading("Riwayat Pemasukan","Penjualan dan pemasukan lain yang tercatat")
        Primary("Catat Pemasukan",add)
        Spacer(Modifier.height(10.dp))
        Metric("Total Pemasukan",money(total))
        transactions.forEach { tx ->
            Panel {
                Text("Penjualan ${tx.invoiceNo}",fontWeight=FontWeight.Bold)
                Text("${tx.paymentMethod} • ${tx.cashierName} • ${date(tx.createdAt)}",fontSize=10.sp,color=Muted)
                Text(if(tx.status=="Dibatalkan") "Dibatalkan" else tx.status,fontSize=10.sp,color=if(tx.status=="Selesai")Green else Red)
                Text(money(tx.total),fontWeight=FontWeight.Bold,color=if(tx.status=="Dibatalkan")Muted else Green)
            }
        }
        records.forEach { record ->
            Panel {
                Text(record.category,fontWeight=FontWeight.Bold)
                Text(record.note,fontSize=11.sp,color=Muted)
                Text(date(record.createdAt),fontSize=10.sp,color=Muted)
                Text(money(record.amount),fontWeight=FontWeight.Bold,color=Green)
                if(record.isCapital) Text("Tambah modal • dikecualikan dari omzet/laba",fontSize=10.sp,color=Muted)
            }
        }
    }
}
@Composable private fun ExpenseFormPage(save:(String,Long,String,Boolean)->Unit,back:()->Unit) {
    var category by rememberSaveable{mutableStateOf("Operasional Toko")};var amount by rememberSaveable{mutableStateOf("")};var note by rememberSaveable{mutableStateOf("")};var prive by rememberSaveable{mutableStateOf(false)}
    Page { Heading("Catat Pengeluaran Toko","Nominal akan mengurangi saldo kas");Input("Kategori",category,{category=it});Input("Nominal",amount,{amount=it},number=true);Input("Catatan",note,{note=it});Row(verticalAlignment=Alignment.CenterVertically){Checkbox(prive,{prive=it});Text("Pengeluaran Prive (dikecualikan dari laba bersih)",fontSize=11.sp)}
        Primary("Simpan Pengeluaran",{save(category,amount.toLongOrNull()?:0,note,prive)},category.isNotBlank()&&(amount.toLongOrNull()?:0)>0);Secondary("Batal",back)
    }
}
@Composable private fun IncomeFormPage(save:(String,Long,String,Boolean)->Unit,back:()->Unit) {
    var category by rememberSaveable{mutableStateOf("Penjualan Lainnya")};var amount by rememberSaveable{mutableStateOf("")};var note by rememberSaveable{mutableStateOf("")};var capital by rememberSaveable{mutableStateOf(false)}
    Page { Heading("Catat Pemasukan Toko","Nominal akan menambah saldo kas");Input("Kategori",category,{category=it});Input("Nominal",amount,{amount=it},number=true);Input("Catatan",note,{note=it});Row(verticalAlignment=Alignment.CenterVertically){Checkbox(capital,{capital=it});Text("Tambah Modal (dikecualikan dari omzet/laba)",fontSize=11.sp)}
        Primary("Simpan Pemasukan",{save(category,amount.toLongOrNull()?:0,note,capital)},category.isNotBlank()&&(amount.toLongOrNull()?:0)>0);Secondary("Batal",back)
    }
}
@Composable private fun ProductListPage(items:List<ProductEntity>,search:String,setSearch:(String)->Unit,detail:(String)->Unit,add:()->Unit,restock:()->Unit) {
    var filter by rememberSaveable { mutableStateOf("Semua") }
    Page { Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f)){Heading("Daftar Barang","Kelola produk dan stok")};Button(add,colors=ButtonDefaults.buttonColors(containerColor=Red)){Text("+ Tambah")}}
        OutlinedTextField(search,setSearch,Modifier.fillMaxWidth(),placeholder={Text("Cari nama barang atau SKU")},singleLine=true,shape=RoundedCornerShape(12.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("Semua","Aman","Menipis","Habis").forEach{FilterChip(selected=filter==it,onClick={filter=it},label={Text(it,fontSize=10.sp)})}}
        items.filter{(it.name.contains(search,true)||it.sku.contains(search,true))&&(filter=="Semua"||it.status==filter)}.forEach{ProductLine(it,{detail(it.id)});Text("Restock ${it.name}",Modifier.clickable{restock()}.padding(start=12.dp,bottom=6.dp),fontSize=10.sp,color=Red)}
        if(items.isEmpty())Text("Belum ada barang.",color=Muted)
    }
}
@Composable private fun ProductLine(p:ProductEntity,onClick:()->Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical=5.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).clickable{onClick()}.padding(12.dp),verticalAlignment=Alignment.CenterVertically){
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Soft),contentAlignment=Alignment.Center){Text("▣",fontSize=20.sp,color=Red)}
        Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(p.name,fontSize=12.sp,fontWeight=FontWeight.Bold);Text("${p.sku} • ${p.stock} ${p.unit}",fontSize=10.sp,color=Muted);Text(p.status,fontSize=10.sp,color=if(p.status=="Aman")Green else Red)}
        Text(money(p.sellPrice),fontSize=11.sp,fontWeight=FontWeight.Bold)
    }
}
@Composable private fun ProductDetailPage(p:ProductEntity?,restock:()->Unit,edit:()->Unit,adjust:()->Unit,movement:()->Unit,archive:()->Unit) {
    Page { if(p==null){Text("Barang tidak ditemukan")}else{
        Panel{Box(Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(14.dp)).background(Soft),contentAlignment=Alignment.Center){Text("▣",fontSize=52.sp,color=Red)};Spacer(Modifier.height(12.dp));Text(p.name,fontSize=20.sp,fontWeight=FontWeight.ExtraBold);Text("SKU: ${p.sku}",fontSize=11.sp,color=Muted);Text(money(p.sellPrice),fontSize=22.sp,color=Red,fontWeight=FontWeight.Bold)}
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Box(Modifier.weight(1f)){Metric("Stok","${p.stock} ${p.unit}")};Box(Modifier.weight(1f)){Metric("HPP",money(p.hpp))}}
        Metric("Status",p.status,"Stok minimum ${p.minStock}")
        Primary("Restock",restock);Secondary("Edit Barang",edit);Secondary("Penyesuaian Stok",adjust);Secondary("Kartu Stok",movement);Text("Arsipkan Barang",Modifier.clickable{archive()}.padding(14.dp),color=Red,fontWeight=FontWeight.Bold)
    }}
}
@Composable private fun ProductFormPage(p:ProductEntity?,save:(String,String,String,String,Long,Long,Int,Int)->Unit,back:()->Unit) {
    var sku by rememberSaveable{mutableStateOf(p?.sku?:"")};var name by rememberSaveable{mutableStateOf(p?.name?:"")};var categories by rememberSaveable{mutableStateOf(p?.categories?:"Makanan")};var unit by rememberSaveable{mutableStateOf(p?.unit?:"pcs")}
    var sell by rememberSaveable{mutableStateOf(p?.sellPrice?.toString()?:"")};var hpp by rememberSaveable{mutableStateOf(p?.hpp?.toString()?:"")};var stock by rememberSaveable{mutableStateOf(p?.stock?.toString()?:"0")};var min by rememberSaveable{mutableStateOf(p?.minStock?.toString()?:"5")}
    Page { Heading(if(p==null)"Tambah Barang" else "Edit Barang","Isi informasi produk")
        Input("SKU",sku,{sku=it});Input("Nama Barang",name,{name=it});Input("Kategori",categories,{categories=it});Input("Satuan",unit,{unit=it});Input("Harga Jual",sell,{sell=it},number=true);if(p==null) Input("Harga Beli / HPP",hpp,{hpp=it},number=true);Input("Stok Awal",stock,{stock=it},number=true);Input("Stok Minimum",min,{min=it},number=true)
        Primary("Simpan Barang",{save(sku,name,categories,unit,sell.toLongOrNull()?:0,hpp.toLongOrNull()?:0,stock.toIntOrNull()?:0,min.toIntOrNull()?:0)},sku.isNotBlank()&&name.isNotBlank()&&(sell.toLongOrNull()?:-1)>=0);Secondary("Batal",back)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun RestockPage(products:List<ProductEntity>,suppliers:List<SupplierEntity>,save:(String,Int,Long,String,String)->Unit,back:()->Unit) {
    var productId by rememberSaveable{mutableStateOf(products.firstOrNull()?.id?:"")};var qty by rememberSaveable{mutableStateOf("1")};var price by rememberSaveable{mutableStateOf("")};var supplier by rememberSaveable{mutableStateOf("")};var note by rememberSaveable{mutableStateOf("")}
    Page { Heading("Catat Restock","Stok, pengeluaran pembelian, saldo, dan HPP diperbarui otomatis")
        if(products.isNotEmpty()) {Text("Pilih Barang",fontWeight=FontWeight.Bold,fontSize=12.sp);var expanded by remember{mutableStateOf(false)}
            ExposedDropdownMenuBox(expanded,{expanded=it}){OutlinedTextField(products.firstOrNull{it.id==productId}?.name?:"Pilih barang",{},Modifier.menuAnchor().fillMaxWidth(),readOnly=true);ExposedDropdownMenu(expanded,{expanded=false}){products.forEach{DropdownMenuItem(text={Text(it.name)},onClick={productId=it.id;expanded=false})}}}}
        Input("Jumlah Barang",qty,{qty=it},number=true);Input("Harga Beli per Unit",price,{price=it},number=true)
        Text("Supplier",fontWeight=FontWeight.Bold,fontSize=12.sp);var expandedS by remember{mutableStateOf(false)}
        ExposedDropdownMenuBox(expandedS,{expandedS=it}){OutlinedTextField(suppliers.firstOrNull{it.id==supplier}?.name?: "Pilih supplier (opsional)",{},Modifier.menuAnchor().fillMaxWidth(),readOnly=true);ExposedDropdownMenu(expandedS,{expandedS=false}){DropdownMenuItem(text={Text("Tidak dipilih")},onClick={supplier="";expandedS=false});suppliers.forEach{DropdownMenuItem(text={Text(it.name)},onClick={supplier=it.id;expandedS=false})}}}
        Input("Catatan",note,{note=it})
        val total=(qty.toLongOrNull()?:0L)*(price.toLongOrNull()?:0L);Text("Total Pembelian: ${money(total)}",fontWeight=FontWeight.Bold,color=Red)
        Primary("Simpan Restock",{save(productId,qty.toIntOrNull()?:0,price.toLongOrNull()?:0,supplier,note)},productId.isNotBlank()&&(qty.toIntOrNull()?:0)>0&&(price.toLongOrNull()?:-1)>=0);Secondary("Batal",back)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun AdjustmentPage(products:List<ProductEntity>,save:(String,Int,String,String)->Unit,back:()->Unit) {
    var pid by rememberSaveable{mutableStateOf(products.firstOrNull()?.id?:"")};var stock by rememberSaveable{mutableStateOf("")};var reason by rememberSaveable{mutableStateOf("Rusak")};var note by rememberSaveable{mutableStateOf("")}
    Page { Heading("Penyesuaian Stok Fisik","Khusus Owner • Catat kondisi stok aktual")
        var expanded by remember{mutableStateOf(false)}
        ExposedDropdownMenuBox(expanded,{expanded=it}){OutlinedTextField(products.firstOrNull{it.id==pid}?.name?:"Pilih barang",{},Modifier.menuAnchor().fillMaxWidth(),readOnly=true);ExposedDropdownMenu(expanded,{expanded=false}){products.forEach{DropdownMenuItem(text={Text(it.name)},onClick={pid=it.id;expanded=false})}}}
        Input("Stok Fisik Terbaru",stock,{stock=it},number=true)
        Text("Alasan",fontSize=12.sp,fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf("Rusak","Hilang","Kadaluarsa","Koreksi").forEach{FilterChip(selected=reason==it,onClick={reason=it},label={Text(it,fontSize=10.sp)})}}
        Input("Catatan",note,{note=it});Primary("Simpan Penyesuaian",{save(pid,stock.toIntOrNull()?:-1,reason,note)},pid.isNotBlank()&&(stock.toIntOrNull()?:-1)>=0);Secondary("Batal",back)
    }
}
@Composable private fun MovementPage(items:List<StockMovementEntity>) {
    Page { Heading("Pergerakan Stok","Riwayat penuh (append-only)");items.forEach{Panel{Text(it.type,fontWeight=FontWeight.Bold);Text(it.productName,fontSize=12.sp);Text("${if(it.quantity>0)"+" else ""}${it.quantity} • ${it.beforeStock} → ${it.afterStock}",color=if(it.quantity>=0)Green else Red,fontWeight=FontWeight.Bold);Text("${it.reference} • ${date(it.createdAt)}",fontSize=10.sp,color=Muted)}};if(items.isEmpty())Text("Belum ada pergerakan stok.") }
}
@Composable private fun SupplierPage(items:List<SupplierEntity>,save:(String?,String,String,String,String)->Unit,delete:(String)->Unit) {
    var editing by remember{mutableStateOf<SupplierEntity?>(null)};var showForm by remember{mutableStateOf(false)};var name by remember{mutableStateOf("")};var phone by remember{mutableStateOf("")};var address by remember{mutableStateOf("")};var note by remember{mutableStateOf("")}
    Page { Heading("Daftar Supplier","Kelola pemasok barang toko")
        Primary("＋ Tambah Supplier",{editing=null;showForm=true;name="";phone="";address="";note=""})
        if(showForm) {
            Panel { Input("Nama Supplier",name,{name=it});Input("Nomor Telepon",phone,{phone=it});Input("Alamat",address,{address=it});Input("Catatan",note,{note=it});Primary("Simpan Supplier",{save(editing?.id,name,phone,address,note);editing=null;showForm=false;name="";phone="";address="";note=""},name.isNotBlank());Secondary("Tutup",{editing=null;showForm=false;name="";phone="";address="";note=""}) }
        }
        items.forEach{Panel{Text(it.name,fontWeight=FontWeight.Bold);Text(it.phone,fontSize=11.sp,color=Muted);Text(it.address,fontSize=11.sp,color=Muted);Row{Text("Edit",Modifier.clickable{editing=it;showForm=true;name=it.name;phone=it.phone;address=it.address;note=it.note}.padding(8.dp),color=Red);Text("Hapus",Modifier.clickable{delete(it.id)}.padding(8.dp),color=Red)}}}
    }
}
@Composable private fun ActivityPage(items:List<ActivityLogEntity>) {
    Page { Heading("Riwayat Aktivitas","Log audit append-only");items.forEach{Panel{Text(it.action,fontWeight=FontWeight.Bold);Text("${it.actor} • ${it.detail}",fontSize=11.sp,color=Muted);Text(date(it.createdAt),fontSize=10.sp,color=Muted)}} }
}
@Composable private fun SettingsPage(cashier:()->Unit,payment:()->Unit) {
    Page { Heading("Pengaturan","Khusus Owner");ActionRow("Manajemen Akun Kasir","Tambah, reset sandi, nonaktifkan akun",cashier);ActionRow("Metode Pembayaran","Atur QRIS dan rekening bank",payment) }
}
@Composable private fun CashierAccountsPage(items:List<AccountEntity>,save:(String?,String,String,String,String)->Unit,active:(String,Boolean)->Unit,reset:(String,String)->Unit) {
    var showForm by remember{mutableStateOf(false)};var editingId by remember{mutableStateOf<String?>(null)};var name by remember{mutableStateOf("")};var username by remember{mutableStateOf("")};var password by remember{mutableStateOf("123456")};var phone by remember{mutableStateOf("")}
    Page { Heading("Manajemen Akun Kasir","Akun pegawai yang terhubung ke toko");Primary("＋ Tambah Akun Kasir",{editingId=null;showForm=true;name="";username="";password="123456";phone=""})
        if(showForm)Panel{Input("Nama",name,{name=it});Input("Username",username,{username=it});Input("Kata Sandi",password,{password=it},password=true);Input("Nomor HP",phone,{phone=it});Primary("Simpan Akun",{save(editingId,name,username,password,phone);showForm=false;editingId=null;name="";username="";password="123456";phone=""},name.isNotBlank()&&username.isNotBlank());Secondary("Batal",{showForm=false;editingId=null})}
        items.forEach{Panel{Text(it.name,fontWeight=FontWeight.Bold);Text("@${it.username} • ${if(it.active)"Aktif" else "Nonaktif"}",fontSize=11.sp,color=Muted);Row{Text("Edit",Modifier.clickable{editingId=it.id;name=it.name;username=it.username;phone=it.phone;password="";showForm=true}.padding(8.dp),color=Red);Text(if(it.active)"Nonaktifkan" else "Aktifkan",Modifier.clickable{active(it.id,!it.active)}.padding(8.dp),color=Red);Text("Reset sandi",Modifier.clickable{reset(it.id,"123456")}.padding(8.dp),color=Red)}}}
    }
}

@Composable private fun PaymentSettingsPage(bank:String,save:(String)->Unit,saveQris:(String)->Unit) {
    var info by rememberSaveable(bank){mutableStateOf(bank)}
    var qrisUri by rememberSaveable{mutableStateOf("")}
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            qrisUri = uri.toString()
            saveQris(qrisUri)
        }
    }
    Page {
        Heading("Metode Pembayaran","Pengaturan rekening dan QRIS toko")
        Panel {
            Text("QRIS Toko",fontWeight=FontWeight.Bold)
            Text("Pilih gambar QRIS dari penyimpanan perangkat. URI gambar akan disimpan secara lokal.",fontSize=11.sp,color=Muted)
            Spacer(Modifier.height(10.dp))
            Button({picker.launch(arrayOf("image/*"))},colors=ButtonDefaults.buttonColors(containerColor=Red)) { Text("Pilih Gambar QRIS") }
            if(qrisUri.isNotBlank()) Text("Gambar tersimpan: ${qrisUri.take(48)}…",fontSize=10.sp,color=Green)
        }
        Input("Informasi Rekening Bank",info,{info=it})
        Primary("Simpan Informasi Bank",{save(info)})
    }
}
@Composable private fun CashierPage(items:List<ProductEntity>,search:String,setSearch:(String)->Unit,add:(ProductEntity)->Unit,cart:()->Unit,history:()->Unit,avatar:()->Unit) {
    var category by rememberSaveable { mutableStateOf("Semua") }
    Page { Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f)){Heading("Halo, Kasir","Pilih barang untuk transaksi")};Text("●",Modifier.clickable{avatar()}.padding(8.dp),fontSize=23.sp,color=Purple)}
        OutlinedTextField(search,setSearch,Modifier.fillMaxWidth(),placeholder={Text("Cari nama barang atau SKU")},singleLine=true)
        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("Semua","Makanan","Sembako","Minuman").forEach{FilterChip(selected=category==it,onClick={category=it},label={Text(it,fontSize=10.sp)})}}
        items.filter{(it.name.contains(search,true)||it.sku.contains(search,true))&&(category=="Semua"||it.categories.split(",").any{c->c.trim().equals(category,true)})}.forEach{Row(Modifier.fillMaxWidth().padding(vertical=5.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(it.name,fontSize=12.sp,fontWeight=FontWeight.Bold);Text("${it.sku} • Stok ${it.stock}",fontSize=10.sp,color=Muted);Text(money(it.sellPrice),color=Red,fontWeight=FontWeight.Bold)};Button({add(it)},enabled=it.stock>0,colors=ButtonDefaults.buttonColors(containerColor=Red)){Text("+")}}}
        Primary("Lihat Keranjang & Pembayaran",cart);Secondary("Riwayat Hari Ini",history)
    }
}
@Composable private fun CartPage(cart:List<CartLineData>,change:(String,Int)->Unit,pay:()->Unit,back:()->Unit) {
    val total=cart.sumOf{it.unitPrice*it.quantity}
    Page { Heading("Item Belanja","${cart.sumOf{it.quantity}} item dalam keranjang")
        cart.forEach{Panel{Text(it.name,fontWeight=FontWeight.Bold);Text(money(it.unitPrice)+" / unit",fontSize=11.sp,color=Muted);Row(verticalAlignment=Alignment.CenterVertically){Text("−",Modifier.clickable{change(it.productId,-1)}.padding(8.dp),fontSize=20.sp,color=Red);Text(it.quantity.toString(),fontWeight=FontWeight.Bold);Text("+",Modifier.clickable{change(it.productId,1)}.padding(8.dp),fontSize=20.sp,color=Red);Spacer(Modifier.weight(1f));Text(money(it.unitPrice*it.quantity),fontWeight=FontWeight.Bold)}}}
        Panel{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Total Belanja",fontWeight=FontWeight.Bold);Text(money(total),fontWeight=FontWeight.ExtraBold,color=Red)}}
        Primary("Lanjut ke Pembayaran",pay,cart.isNotEmpty());Secondary("Kembali ke Katalog",back)
    }
}
@Composable private fun PaymentPage(total:Long,checkout:(String,Long)->Unit,back:()->Unit) {
    var method by rememberSaveable{mutableStateOf("Tunai")};var received by rememberSaveable{mutableStateOf(total.toString())}
    Page { Heading("Pembayaran","Periksa total transaksi sebelum menyelesaikan");Metric("Total Tagihan",money(total))
        Text("Metode Pembayaran",fontWeight=FontWeight.Bold,fontSize=12.sp)
        listOf("Tunai","Transfer","QRIS").forEach{Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).clickable{method=it}.padding(10.dp),verticalAlignment=Alignment.CenterVertically){RadioButton(method==it,{method=it});Text(it,fontWeight=FontWeight.SemiBold)}}
        if(method=="Tunai"){Input("Uang Diterima",received,{received=it},number=true);Text("Kembalian: ${money(((received.toLongOrNull()?:0)-total).coerceAtLeast(0))}",fontWeight=FontWeight.Bold,color=Green)}
        Primary("Konfirmasi Pembayaran",{checkout(method,received.toLongOrNull()?:0)},method!="Tunai"||(received.toLongOrNull()?:0)>=total);Secondary("Kembali",back)
    }
}

@Composable private fun SuccessPage(invoice:String,total:Long,isOwner:Boolean,home:()->Unit,history:()->Unit,exitCashier:()->Unit) {
    Page {
        Column(Modifier.fillMaxWidth().padding(25.dp),horizontalAlignment=Alignment.CenterHorizontally) {
            Box(Modifier.size(78.dp).clip(CircleShape).background(Color(0xFFE3F5E9)),contentAlignment=Alignment.Center) {
                Text("✓",fontSize=44.sp,color=Green,fontWeight=FontWeight.Bold)
            }
            Text("Transaksi Berhasil!",fontSize=22.sp,fontWeight=FontWeight.ExtraBold)
            Text("Pembayaran telah dicatat secara lokal",fontSize=11.sp,color=Muted)
        }
        Panel {
            Text("Struk Transaksi",fontWeight=FontWeight.Bold)
            Text(invoice,fontSize=11.sp,color=Muted)
            HorizontalDivider(Modifier.padding(vertical=10.dp))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Total"); Text(money(total),fontWeight=FontWeight.Bold) }
        }
        Primary("Transaksi Baru",home)
        Secondary("Lihat Riwayat",history)
        if(isOwner) Secondary("Keluar dari Kasir",exitCashier)
    }
}
@Composable private fun HistoryPage(items:List<TransactionEntity>,openCancel:(TransactionEntity)->Unit,isCashier:Boolean) {
    Page { Heading("Riwayat Hari Ini","Daftar transaksi tersimpan");items.forEach{Panel{Text(it.invoiceNo,fontWeight=FontWeight.Bold);Text("${date(it.createdAt)} • ${it.cashierName}",fontSize=10.sp,color=Muted);Text("${it.paymentMethod} • ${it.status}",fontSize=11.sp,color=if(it.status=="Selesai")Green else Red);Text(money(it.total),fontWeight=FontWeight.Bold,color=Red);if(it.status=="Selesai")Text(if(isCashier)"Ajukan Pembatalan" else "Batalkan Transaksi",Modifier.clickable{openCancel(it)}.padding(top=8.dp),color=Red,fontSize=11.sp,fontWeight=FontWeight.Bold);if(it.status=="Perlu Ditinjau"&&!isCashier)Text("Tinjau / Batalkan",Modifier.clickable{openCancel(it)}.padding(top=8.dp),color=Red,fontSize=11.sp,fontWeight=FontWeight.Bold);if(it.status=="Dibatalkan")Text("Alasan: ${it.cancelReason}",fontSize=10.sp,color=Muted)}};if(items.isEmpty())Text("Belum ada transaksi.") }
}
