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

@Composable internal fun ProductListScreen(items:List<ProductEntity>,search:String,setSearch:(String)->Unit,detail:(String)->Unit,add:()->Unit,restock:()->Unit, modifier: Modifier = Modifier) {
    var filter by rememberSaveable { mutableStateOf("Semua") }
    Page(modifier = modifier) { Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f)){Heading(stringResource(R.string.screen_copy_065),"Kelola produk dan stok")};Button(add,colors=ButtonDefaults.buttonColors(containerColor=Red)){Text(stringResource(R.string.screen_copy_066))}}
        OutlinedTextField(search,setSearch,Modifier.fillMaxWidth(),placeholder={Text(stringResource(R.string.screen_copy_067))},singleLine=true,shape=RoundedCornerShape(12.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("Semua","Aman","Menipis","Habis").forEach{FilterChip(selected=filter==it,onClick={filter=it},label={Text(it,fontSize=10.sp)})}}
        items.filter{(it.name.contains(search,true)||it.sku.contains(search,true))&&(filter=="Semua"||it.status==filter)}.forEach{ProductLine(it,{detail(it.id)});Text("Restock ${it.name}",Modifier.clickable{restock()}.padding(start=12.dp,bottom=6.dp),fontSize=10.sp,color=Red)}
        if(items.isEmpty())Text(stringResource(R.string.screen_copy_068),color=Muted)
    }
}
