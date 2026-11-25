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

@Composable internal fun SupplierScreen(items:List<SupplierEntity>,save:(String?,String,String,String,String)->Unit,delete:(String)->Unit, modifier: Modifier = Modifier) {
    var editing by remember{mutableStateOf<SupplierEntity?>(null)};var showForm by remember{mutableStateOf(false)};var name by remember{mutableStateOf("")};var phone by remember{mutableStateOf("")};var address by remember{mutableStateOf("")};var note by remember{mutableStateOf("")}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_076),"Kelola pemasok barang toko")
        Primary(stringResource(R.string.screen_copy_077),{editing=null;showForm=true;name="";phone="";address="";note=""})
        if(showForm) {
            Panel { Input(stringResource(R.string.screen_copy_078),name,{name=it});Input(stringResource(R.string.screen_copy_079),phone,{phone=it});Input(stringResource(R.string.screen_copy_080),address,{address=it});Input(stringResource(R.string.screen_copy_041),note,{note=it});Primary(stringResource(R.string.screen_copy_081),{save(editing?.id,name,phone,address,note);editing=null;showForm=false;name="";phone="";address="";note=""},name.isNotBlank());Secondary(stringResource(R.string.screen_copy_082),{editing=null;showForm=false;name="";phone="";address="";note=""}) }
        }
        items.forEach{Panel{Text(it.name,fontWeight=FontWeight.Bold);Text(it.phone,fontSize=11.sp,color=Muted);Text(it.address,fontSize=11.sp,color=Muted);Row{Text(stringResource(R.string.screen_copy_083),Modifier.clickable{editing=it;showForm=true;name=it.name;phone=it.phone;address=it.address;note=it.note}.padding(8.dp),color=Red);Text(stringResource(R.string.screen_copy_084),Modifier.clickable{delete(it.id)}.padding(8.dp),color=Red)}}}
    }
}
