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

@Composable internal fun ProductFormScreen(p:ProductEntity?,save:(String,String,String,String,Long,Long,Int,Int)->Unit,back:()->Unit, modifier: Modifier = Modifier) {
    var sku by rememberSaveable{mutableStateOf(p?.sku?:"")};var name by rememberSaveable{mutableStateOf(p?.name?:"")};var categories by rememberSaveable{mutableStateOf(p?.categories?:"Makanan")};var unit by rememberSaveable{mutableStateOf(p?.unit?:"pcs")}
    var sell by rememberSaveable{mutableStateOf(p?.sellPrice?.toString()?:"")};var hpp by rememberSaveable{mutableStateOf(p?.hpp?.toString()?:"")};var stock by rememberSaveable{mutableStateOf(p?.stock?.toString()?:"0")};var min by rememberSaveable{mutableStateOf(p?.minStock?.toString()?:"5")}
    Page(modifier = modifier) { Heading(if(p==null)"Tambah Barang" else "Edit Barang","Isi informasi produk")
        Input(stringResource(R.string.screen_copy_056),sku,{sku=it});Input(stringResource(R.string.screen_copy_057),name,{name=it});Input(stringResource(R.string.screen_copy_058),categories,{categories=it});Input(stringResource(R.string.screen_copy_059),unit,{unit=it});Input(stringResource(R.string.screen_copy_060),sell,{sell=it},number=true);if(p==null) Input(stringResource(R.string.screen_copy_061),hpp,{hpp=it},number=true);Input(stringResource(R.string.screen_copy_062),stock,{stock=it},number=true);Input(stringResource(R.string.screen_copy_063),min,{min=it},number=true)
        Primary(stringResource(R.string.screen_copy_064),{save(sku,name,categories,unit,sell.toLongOrNull()?:0,hpp.toLongOrNull()?:0,stock.toIntOrNull()?:0,min.toIntOrNull()?:0)},sku.isNotBlank()&&name.isNotBlank()&&(sell.toLongOrNull()?:-1)>=0);Secondary(stringResource(R.string.screen_copy_043),back)
    }
}
