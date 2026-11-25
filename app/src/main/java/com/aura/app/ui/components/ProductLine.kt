package com.aura.app.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.aura.app.core.logic.StatusStokEvaluator
import com.aura.app.core.model.StatusStok
import com.aura.app.data.*
import com.aura.app.ui.components.*
import com.aura.app.ui.theme.*
import com.aura.app.core.format.RupiahFormatter
import com.aura.app.core.format.TanggalFormatter
import java.util.Locale

@Composable internal fun ProductLine(p:ProductEntity,onClick:()->Unit) {
    val status = StatusStokEvaluator.evaluate(p.stock, p.minStock, diarsipkan = false)
    val statusLabel = when (status) {
        StatusStok.AMAN -> "Aman"
        StatusStok.MENIPIS -> "Menipis"
        StatusStok.HABIS -> "Habis"
        StatusStok.DIARSIPKAN -> "Diarsipkan"
    }
    Row(Modifier.fillMaxWidth().padding(vertical=5.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).clickable{onClick()}.padding(12.dp),verticalAlignment=Alignment.CenterVertically){
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Soft),contentAlignment=Alignment.Center){Text("▣",fontSize=20.sp,color=Red)}
        Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(p.name,fontSize=12.sp,fontWeight=FontWeight.Bold);Text("${p.sku} • ${p.stock} ${p.unit}",fontSize=10.sp,color=Muted);Text(statusLabel,fontSize=10.sp,color=StatusColors.forStatus(status))}
        Text(money(p.sellPrice),fontSize=11.sp,fontWeight=FontWeight.Bold)
    }
}
