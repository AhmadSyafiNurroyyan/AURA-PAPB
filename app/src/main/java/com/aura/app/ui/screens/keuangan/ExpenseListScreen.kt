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

@Composable internal fun ExpenseListScreen(records:List<CashRecordEntity>,restocks:List<RestockEntity>,products:List<ProductEntity>,add:()->Unit, modifier: Modifier = Modifier) {
    val total = records.sumOf{it.amount} + restocks.sumOf{it.total}
    Page(modifier = modifier) {
        Heading(stringResource(R.string.screen_copy_125),"Pengeluaran kustom dan pembelian restock")
        Primary(stringResource(R.string.screen_copy_126),add)
        Spacer(Modifier.height(10.dp))
        Metric(stringResource(R.string.screen_copy_127),money(total))
        restocks.forEach { restock ->
            Panel {
                Text("Pembelian Stok • ${products.firstOrNull{it.id==restock.productId}?.name ?: restock.productId}",fontWeight=FontWeight.Bold)
                Text("${restock.quantity} unit • ${money(restock.purchasePrice)} / unit",fontSize=10.sp,color=Muted)
                Text(date(restock.createdAt),fontSize=10.sp,color=Muted)
                Text(money(restock.total),fontWeight=FontWeight.Bold,color=Red)
            }
        }
        records.forEach { record ->
            Panel {
                Text(record.category,fontWeight=FontWeight.Bold)
                Text(record.note.ifBlank{"Tanpa catatan"},fontSize=11.sp,color=Muted)
                Text(date(record.createdAt),fontSize=10.sp,color=Muted)
                Text(money(record.amount),fontWeight=FontWeight.Bold,color=Red)
                if(record.isPrive) Text(stringResource(R.string.screen_copy_118),fontSize=10.sp,color=Muted)
            }
        }
    }
}
