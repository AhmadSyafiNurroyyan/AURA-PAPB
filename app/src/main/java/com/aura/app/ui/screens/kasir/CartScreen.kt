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

@Composable internal fun CartScreen(cart:List<CartLineData>,change:(String,Int)->Unit,pay:()->Unit,back:()->Unit, modifier: Modifier = Modifier) {
    val total=cart.sumOf{it.unitPrice*it.quantity}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_093),"${cart.sumOf{it.quantity}} item dalam keranjang")
        cart.forEach{Panel{Text(it.name,fontWeight=FontWeight.Bold);Text(money(it.unitPrice)+" / unit",fontSize=11.sp,color=Muted);Row(verticalAlignment=Alignment.CenterVertically){Text(stringResource(R.string.screen_copy_094),Modifier.clickable{change(it.productId,-1)}.padding(8.dp),fontSize=20.sp,color=Red);Text(it.quantity.toString(),fontWeight=FontWeight.Bold);Text(stringResource(R.string.screen_copy_095),Modifier.clickable{change(it.productId,1)}.padding(8.dp),fontSize=20.sp,color=Red);Spacer(Modifier.weight(1f));Text(money(it.unitPrice*it.quantity),fontWeight=FontWeight.Bold)}}}
        Panel{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(stringResource(R.string.screen_copy_096),fontWeight=FontWeight.Bold);Text(money(total),fontWeight=FontWeight.ExtraBold,color=Red)}}
        Primary(stringResource(R.string.screen_copy_097),pay,cart.isNotEmpty());Secondary(stringResource(R.string.screen_copy_098),back)
    }
}
