package com.aura.app.ui.screens.barang

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun RestockScreen(products:List<ProductEntity>,suppliers:List<SupplierEntity>,save:(String,Int,Long,String,String)->Unit,back:()->Unit, modifier: Modifier = Modifier) {
    var productId by rememberSaveable{mutableStateOf(products.firstOrNull()?.id?:"")};var qty by rememberSaveable{mutableStateOf("1")};var price by rememberSaveable{mutableStateOf("")};var supplier by rememberSaveable{mutableStateOf("")};var note by rememberSaveable{mutableStateOf("")}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_069),"Stok, pengeluaran pembelian, saldo, dan HPP diperbarui otomatis")
        if(products.isNotEmpty()) {Text(stringResource(R.string.screen_copy_070),fontWeight=FontWeight.Bold,fontSize=12.sp);var expanded by remember{mutableStateOf(false)}
            ExposedDropdownMenuBox(expanded,{expanded=it}){OutlinedTextField(products.firstOrNull{it.id==productId}?.name?:"Pilih barang",{},Modifier.menuAnchor().fillMaxWidth(),readOnly=true);ExposedDropdownMenu(expanded,{expanded=false}){products.forEach{DropdownMenuItem(text={Text(it.name)},onClick={productId=it.id;expanded=false})}}}}
        Input(stringResource(R.string.screen_copy_071),qty,{qty=it},number=true);Input(stringResource(R.string.screen_copy_072),price,{price=it},number=true)
        Text(stringResource(R.string.screen_copy_073),fontWeight=FontWeight.Bold,fontSize=12.sp);var expandedS by remember{mutableStateOf(false)}
        ExposedDropdownMenuBox(expandedS,{expandedS=it}){OutlinedTextField(suppliers.firstOrNull{it.id==supplier}?.name?: "Pilih supplier (opsional)",{},Modifier.menuAnchor().fillMaxWidth(),readOnly=true);ExposedDropdownMenu(expandedS,{expandedS=false}){DropdownMenuItem(text={Text(stringResource(R.string.screen_copy_074))},onClick={supplier="";expandedS=false});suppliers.forEach{DropdownMenuItem(text={Text(it.name)},onClick={supplier=it.id;expandedS=false})}}}
        Input(stringResource(R.string.screen_copy_041),note,{note=it})
        val total=(qty.toLongOrNull()?:0L)*(price.toLongOrNull()?:0L);Text("Total Pembelian: ${money(total)}",fontWeight=FontWeight.Bold,color=Red)
        Primary(stringResource(R.string.screen_copy_075),{save(productId,qty.toIntOrNull()?:0,price.toLongOrNull()?:0,supplier,note)},productId.isNotBlank()&&(qty.toIntOrNull()?:0)>0&&(price.toLongOrNull()?:-1)>=0);Secondary(stringResource(R.string.screen_copy_043),back)
    }
}
