package com.aura.app.ui.screens.analisis

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

@Composable internal fun AnalysisScreen(tab:String,setTab:(String)->Unit,products:List<ProductEntity>,tx:List<TransactionEntity>,items:List<TransactionItemEntity>,records:List<CashRecordEntity>,adjustments:List<StockAdjustmentEntity>,restocks:List<RestockEntity>,movements:List<StockMovementEntity>, modifier: Modifier = Modifier) {
    val successful=tx.filter{it.status=="Selesai"}
    val revenue=successful.sumOf{it.total}
    val successfulInvoices=successful.map{it.invoiceNo}.toSet()
    val costOfGoods=items.filter{it.invoiceNo in successfulInvoices}.sumOf{it.unitCost*it.quantity}
    val expense=records.filter{it.kind=="expense"&&!it.isPrive}.sumOf{it.amount}
    val stockLoss=adjustments.sumOf{it.lossValue}
    val capital=records.filter{it.kind=="income"&&it.isCapital}.sumOf{it.amount}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_001),"Data dihitung dari database lokal")
        Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){listOf("Ringkasan","Penjualan","Stok","Prediksi").forEach{FilterChip(selected=tab==it,onClick={setTab(it)},label={Text(it,fontSize=10.sp)})}}
        Spacer(Modifier.height(12.dp))
        when(tab){
            "Ringkasan"->{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Box(Modifier.weight(1f)){Metric(stringResource(R.string.screen_copy_002),money(revenue))};Box(Modifier.weight(1f)){Metric(stringResource(R.string.screen_copy_003),money(costOfGoods))}};Metric(stringResource(R.string.screen_copy_004),money(revenue-costOfGoods-expense-stockLoss),"Pengeluaran: ${money(expense)} • Kerugian stok: ${money(stockLoss)}");Text("Pemasukan modal dikecualikan: ${money(capital)}",fontSize=10.sp,color=Muted)}
            "Penjualan"->{Metric(stringResource(R.string.screen_copy_005),money(revenue));Metric(stringResource(R.string.screen_copy_006),money(costOfGoods));Metric(stringResource(R.string.screen_copy_007),successful.size.toString())}
            "Stok"->{Metric(stringResource(R.string.screen_copy_008),products.size.toString());Metric(stringResource(R.string.screen_copy_009),money(products.sumOf{it.stock.toLong()*it.hpp}));Metric(stringResource(R.string.screen_copy_010),products.sortedByDescending{it.sold30Days}.firstOrNull()?.name ?: "-")}
            else->{Metric(stringResource(R.string.screen_copy_011),money((revenue*1.08).toLong()),"Estimasi +8% dari total transaksi tersimpan");Text(stringResource(R.string.screen_copy_012),fontSize=11.sp,color=Muted)}
        }
        Heading(stringResource(R.string.screen_copy_013))
        val counters=listOf("Transaksi" to tx.size,"Restock" to restocks.size,"Penyesuaian Stok" to adjustments.size,
            "Pengeluaran Kustom" to records.count{it.kind=="expense"},"Pemasukan Kustom" to records.count{it.kind=="income"})
        counters.forEach { (label,count) ->
            Panel { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text(label,fontSize=11.sp); Text("$count / 5.000",fontSize=11.sp,fontWeight=FontWeight.Bold) }
                LinearProgressIndicator(progress=count/5000f,modifier=Modifier.fillMaxWidth().padding(top=6.dp),color=Red)
            }
        }
        Heading(stringResource(R.string.screen_copy_014))
        products.sortedBy{it.stock-it.minStock}.take(5).forEach{ProductLine(it,{})}
    }
}
