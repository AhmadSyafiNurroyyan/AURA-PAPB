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

@Composable internal fun IncomeFormScreen(save:(String,Long,String,Boolean)->Unit,back:()->Unit, modifier: Modifier = Modifier) {
    var category by rememberSaveable{mutableStateOf("Penjualan Lainnya")};var amount by rememberSaveable{mutableStateOf("")};var note by rememberSaveable{mutableStateOf("")};var capital by rememberSaveable{mutableStateOf(false)}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_128),"Nominal akan menambah saldo kas");Input(stringResource(R.string.screen_copy_058),category,{category=it});Input(stringResource(R.string.screen_copy_122),amount,{amount=it},number=true);Input(stringResource(R.string.screen_copy_041),note,{note=it});Row(verticalAlignment=Alignment.CenterVertically){Checkbox(capital,{capital=it});Text(stringResource(R.string.screen_copy_129),fontSize=11.sp)}
        Primary(stringResource(R.string.screen_copy_130),{save(category,amount.toLongOrNull()?:0,note,capital)},category.isNotBlank()&&(amount.toLongOrNull()?:0)>0);Secondary(stringResource(R.string.screen_copy_043),back)
    }
}
