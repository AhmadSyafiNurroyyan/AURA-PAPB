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

@Composable internal fun HistoryScreen(items:List<TransactionEntity>,openCancel:(TransactionEntity)->Unit,isCashier:Boolean, modifier: Modifier = Modifier) {
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_102),"Daftar transaksi tersimpan");items.forEach{Panel{Text(it.invoiceNo,fontWeight=FontWeight.Bold);Text("${date(it.createdAt)} • ${it.cashierName}",fontSize=10.sp,color=Muted);Text("${it.paymentMethod} • ${it.status}",fontSize=11.sp,color=if(it.status=="Selesai")Green else Red);Text(money(it.total),fontWeight=FontWeight.Bold,color=Red);if(it.status=="Selesai")Text(if(isCashier)"Ajukan Pembatalan" else "Batalkan Transaksi",Modifier.clickable{openCancel(it)}.padding(top=8.dp),color=Red,fontSize=11.sp,fontWeight=FontWeight.Bold);if(it.status=="Perlu Ditinjau"&&!isCashier)Text(stringResource(R.string.screen_copy_103),Modifier.clickable{openCancel(it)}.padding(top=8.dp),color=Red,fontSize=11.sp,fontWeight=FontWeight.Bold);if(it.status=="Dibatalkan")Text("Alasan: ${it.cancelReason}",fontSize=10.sp,color=Muted)}};if(items.isEmpty())Text(stringResource(R.string.screen_copy_104)) }
}
