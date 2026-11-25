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

@Composable internal fun PaymentScreen(total:Long,checkout:(String,Long)->Unit,back:()->Unit, modifier: Modifier = Modifier) {
    var method by rememberSaveable{mutableStateOf("Tunai")};var received by rememberSaveable{mutableStateOf(total.toString())}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_105),"Periksa total transaksi sebelum menyelesaikan");Metric(stringResource(R.string.screen_copy_106),money(total))
        Text(stringResource(R.string.screen_copy_107),fontWeight=FontWeight.Bold,fontSize=12.sp)
        listOf("Tunai","Transfer","QRIS").forEach{Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).clickable{method=it}.padding(10.dp),verticalAlignment=Alignment.CenterVertically){RadioButton(method==it,{method=it});Text(it,fontWeight=FontWeight.SemiBold)}}
        if(method=="Tunai"){Input(stringResource(R.string.screen_copy_108),received,{received=it},number=true);Text("Kembalian: ${money(((received.toLongOrNull()?:0)-total).coerceAtLeast(0))}",fontWeight=FontWeight.Bold,color=Green)}
        Primary(stringResource(R.string.screen_copy_109),{checkout(method,received.toLongOrNull()?:0)},method!="Tunai"||(received.toLongOrNull()?:0)>=total);Secondary(stringResource(R.string.screen_copy_026),back)
    }
}
