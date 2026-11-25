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
@Composable internal fun AdjustmentScreen(products:List<ProductEntity>,save:(String,Int,String,String)->Unit,back:()->Unit, modifier: Modifier = Modifier) {
    var pid by rememberSaveable{mutableStateOf(products.firstOrNull()?.id?:"")};var stock by rememberSaveable{mutableStateOf("")};var reason by rememberSaveable{mutableStateOf("Rusak")};var note by rememberSaveable{mutableStateOf("")}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_038),"Khusus Owner • Catat kondisi stok aktual")
        var expanded by remember{mutableStateOf(false)}
        ExposedDropdownMenuBox(expanded,{expanded=it}){OutlinedTextField(products.firstOrNull{it.id==pid}?.name?:"Pilih barang",{},Modifier.menuAnchor().fillMaxWidth(),readOnly=true);ExposedDropdownMenu(expanded,{expanded=false}){products.forEach{DropdownMenuItem(text={Text(it.name)},onClick={pid=it.id;expanded=false})}}}
        Input(stringResource(R.string.screen_copy_039),stock,{stock=it},number=true)
        Text(stringResource(R.string.screen_copy_040),fontSize=12.sp,fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf("Rusak","Hilang","Kadaluarsa","Koreksi").forEach{FilterChip(selected=reason==it,onClick={reason=it},label={Text(it,fontSize=10.sp)})}}
        Input(stringResource(R.string.screen_copy_041),note,{note=it});Primary(stringResource(R.string.screen_copy_042),{save(pid,stock.toIntOrNull()?:-1,reason,note)},pid.isNotBlank()&&(stock.toIntOrNull()?:-1)>=0);Secondary(stringResource(R.string.screen_copy_043),back)
    }
}
