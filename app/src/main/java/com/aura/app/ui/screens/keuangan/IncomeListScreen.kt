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

@Composable internal fun IncomeListScreen(transactions:List<TransactionEntity>,records:List<CashRecordEntity>,add:()->Unit, modifier: Modifier = Modifier) {
    val total = transactions.filter{it.status=="Selesai"}.sumOf{it.total} + records.sumOf{it.amount}
    Page(modifier = modifier) {
        Heading(stringResource(R.string.screen_copy_131),"Penjualan dan pemasukan lain yang tercatat")
        Primary(stringResource(R.string.screen_copy_132),add)
        Spacer(Modifier.height(10.dp))
        Metric(stringResource(R.string.screen_copy_133),money(total))
        transactions.forEach { tx ->
            Panel {
                Text("Penjualan ${tx.invoiceNo}",fontWeight=FontWeight.Bold)
                Text("${tx.paymentMethod} • ${tx.cashierName} • ${date(tx.createdAt)}",fontSize=10.sp,color=Muted)
                Text(if(tx.status=="Dibatalkan") "Dibatalkan" else tx.status,fontSize=10.sp,color=if(tx.status=="Selesai")Green else Red)
                Text(money(tx.total),fontWeight=FontWeight.Bold,color=if(tx.status=="Dibatalkan")Muted else Green)
            }
        }
        records.forEach { record ->
            Panel {
                Text(record.category,fontWeight=FontWeight.Bold)
                Text(record.note,fontSize=11.sp,color=Muted)
                Text(date(record.createdAt),fontSize=10.sp,color=Muted)
                Text(money(record.amount),fontWeight=FontWeight.Bold,color=Green)
                if(record.isCapital) Text(stringResource(R.string.screen_copy_134),fontSize=10.sp,color=Muted)
            }
        }
    }
}
