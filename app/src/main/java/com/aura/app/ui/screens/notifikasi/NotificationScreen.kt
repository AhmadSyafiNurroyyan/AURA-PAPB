package com.aura.app.ui.screens.notifikasi

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

@Composable internal fun NotificationScreen(items:List<NotificationEntity>,read:(String)->Unit,readAll:()->Unit,go:(String)->Unit, modifier: Modifier = Modifier) {
    Page(modifier = modifier) { Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f)){Heading(stringResource(R.string.screen_copy_136),"Pembaruan kondisi toko")};Text(stringResource(R.string.screen_copy_137),Modifier.clickable{readAll()},fontSize=11.sp,color=Red)}
        items.forEach{Panel{Text(it.title,fontWeight=FontWeight.Bold);Text(it.message,fontSize=12.sp,color=Muted);Text(date(it.createdAt),fontSize=10.sp,color=Muted);if(!it.isRead)Text(stringResource(R.string.screen_copy_138),Modifier.clickable{read(it.id)}.padding(top=8.dp),color=Red,fontSize=11.sp,fontWeight=FontWeight.Bold)}}
        if(items.isEmpty())Text(stringResource(R.string.screen_copy_139),color=Muted)
    }
}
