package com.aura.app.ui.screens.profile

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

@Composable internal fun ProfileScreen(name:String,role:String,storeId:String,settings:()->Unit,switch:()->Unit,logout:()->Unit, modifier: Modifier = Modifier) {
    Page(modifier = modifier) { Panel { Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(56.dp).clip(CircleShape).background(Purple),contentAlignment=Alignment.Center){Text(name.take(1),color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold)};Spacer(Modifier.width(12.dp));Column{Text(name,fontWeight=FontWeight.Bold,fontSize=16.sp);Text(if(role=="owner")"Owner Toko" else "Kasir",fontSize=12.sp,color=Muted);Text("ID Toko: $storeId",fontSize=11.sp,color=Muted)}}}
        ActionRow(stringResource(R.string.screen_copy_152),"Akun kasir dan metode pembayaran",settings);ActionRow(stringResource(R.string.screen_copy_153),"Beralih sesi perangkat",switch);ActionRow(stringResource(R.string.screen_copy_154),"Akhiri sesi AURA",logout)
    }
}
