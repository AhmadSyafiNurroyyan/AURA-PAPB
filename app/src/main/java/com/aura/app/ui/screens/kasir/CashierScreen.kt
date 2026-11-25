package com.aura.app.ui.screens.kasir

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.aura.app.R
import com.aura.app.data.*
import com.aura.app.ui.components.*
import com.aura.app.ui.theme.*
import com.aura.app.core.format.RupiahFormatter
import com.aura.app.core.format.TanggalFormatter
import java.util.Locale

@Composable internal fun CashierScreen(items:List<ProductEntity>,search:String,setSearch:(String)->Unit,add:(ProductEntity)->Unit,cart:()->Unit,history:()->Unit,avatar:()->Unit, modifier: Modifier = Modifier) {
    var category by rememberSaveable { mutableStateOf("Semua") }
    Page(modifier = modifier) { Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f)){Heading(stringResource(R.string.screen_copy_099),"Pilih barang untuk transaksi")};Text(stringResource(R.string.screen_copy_100),Modifier.clickable{avatar()}.padding(8.dp),fontSize=23.sp,color=Purple)}
        OutlinedTextField(search,setSearch,Modifier.fillMaxWidth(),placeholder={Text(stringResource(R.string.screen_copy_067))},singleLine=true)
        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("Semua","Makanan","Sembako","Minuman").forEach{FilterChip(selected=category==it,onClick={category=it},label={Text(it,fontSize=10.sp)})}}
        items.filter{(it.name.contains(search,true)||it.sku.contains(search,true))&&(category=="Semua"||it.categories.split(",").any{c->c.trim().equals(category,true)})}.forEach{Row(Modifier.fillMaxWidth().padding(vertical=5.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(it.name,fontSize=12.sp,fontWeight=FontWeight.Bold);Text("${it.sku} • Stok ${it.stock}",fontSize=10.sp,color=Muted);Text(money(it.sellPrice),color=Red,fontWeight=FontWeight.Bold)};Button({add(it)},enabled=it.stock>0,colors=ButtonDefaults.buttonColors(containerColor=Red)){Text(stringResource(R.string.screen_copy_095))}}}
        Primary(stringResource(R.string.screen_copy_101),cart);Secondary(stringResource(R.string.screen_copy_102),history)
    }
}
