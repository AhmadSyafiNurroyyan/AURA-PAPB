package com.aura.app.ui.screens.keuangan

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

@Composable internal fun CashRecordListScreen(title:String,records:List<CashRecordEntity>,add:()->Unit, modifier: Modifier = Modifier) {
    Page(modifier = modifier) { Heading("Riwayat $title","Catatan tersimpan di database lokal");Primary("Catat $title",add)
        val total=records.sumOf{it.amount};Spacer(Modifier.height(12.dp));Metric("Total $title",money(total))
        records.forEach{Panel{Text(it.category,fontWeight=FontWeight.Bold);Text(it.note.ifBlank{"Tanpa catatan"},fontSize=11.sp,color=Muted);Text(date(it.createdAt),fontSize=10.sp,color=Muted);Text(money(it.amount),fontWeight=FontWeight.Bold,color=Red);if(it.isPrive)Text(stringResource(R.string.screen_copy_118),fontSize=10.sp,color=Muted);if(it.isCapital)Text(stringResource(R.string.screen_copy_119),fontSize=10.sp,color=Muted)}}
        if(records.isEmpty())Text(stringResource(R.string.screen_copy_120),color=Muted)
    }
}
