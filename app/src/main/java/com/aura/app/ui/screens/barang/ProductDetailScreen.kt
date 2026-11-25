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

@Composable internal fun ProductDetailScreen(p:ProductEntity?,restock:()->Unit,edit:()->Unit,adjust:()->Unit,movement:()->Unit,archive:()->Unit, modifier: Modifier = Modifier) {
    Page(modifier = modifier) { if(p==null){Text(stringResource(R.string.screen_copy_046))}else{
        Panel{Box(Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(14.dp)).background(Soft),contentAlignment=Alignment.Center){Text(stringResource(R.string.screen_copy_047),fontSize=52.sp,color=Red)};Spacer(Modifier.height(12.dp));Text(p.name,fontSize=20.sp,fontWeight=FontWeight.ExtraBold);Text("SKU: ${p.sku}",fontSize=11.sp,color=Muted);Text(money(p.sellPrice),fontSize=22.sp,color=Red,fontWeight=FontWeight.Bold)}
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Box(Modifier.weight(1f)){Metric(stringResource(R.string.screen_copy_048),"${p.stock} ${p.unit}")};Box(Modifier.weight(1f)){Metric(stringResource(R.string.screen_copy_049),money(p.hpp))}}
        Metric(stringResource(R.string.screen_copy_050),p.status,"Stok minimum ${p.minStock}")
        Primary(stringResource(R.string.screen_copy_051),restock);Secondary(stringResource(R.string.screen_copy_052),edit);Secondary(stringResource(R.string.screen_copy_053),adjust);Secondary(stringResource(R.string.screen_copy_054),movement);Text(stringResource(R.string.screen_copy_055),Modifier.clickable{archive()}.padding(14.dp),color=Red,fontWeight=FontWeight.Bold)
    }}
}
