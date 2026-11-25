package com.aura.app.ui.screens.auth

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

@Composable internal fun RegisterTwoScreen(done:(String,String,Long)->Unit,back:()->Unit, modifier: Modifier = Modifier) {
    var store by rememberSaveable{mutableStateOf("")}; var type by rememberSaveable{mutableStateOf("Retail / Sembako")}; var balance by rememberSaveable{mutableStateOf("0")}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_027),"Langkah 2 dari 2 • Identitas toko"); Input(stringResource(R.string.screen_copy_028),store,{store=it}); Input(stringResource(R.string.screen_copy_029),type,{type=it}); Input(stringResource(R.string.screen_copy_030),balance,{balance=it},number=true)
        Primary(stringResource(R.string.screen_copy_031),{done(store,type,balance.toLongOrNull()?:0)},store.isNotBlank()); Secondary(stringResource(R.string.screen_copy_026),back)
    }
}
