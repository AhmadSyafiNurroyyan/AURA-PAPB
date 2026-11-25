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

@Composable internal fun MovementScreen(items:List<StockMovementEntity>, modifier: Modifier = Modifier) {
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_044),"Riwayat penuh (append-only)");items.forEach{Panel{Text(it.type,fontWeight=FontWeight.Bold);Text(it.productName,fontSize=12.sp);Text("${if(it.quantity>0)"+" else ""}${it.quantity} • ${it.beforeStock} → ${it.afterStock}",color=if(it.quantity>=0)Green else Red,fontWeight=FontWeight.Bold);Text("${it.reference} • ${date(it.createdAt)}",fontSize=10.sp,color=Muted)}};if(items.isEmpty())Text(stringResource(R.string.screen_copy_045)) }
}
