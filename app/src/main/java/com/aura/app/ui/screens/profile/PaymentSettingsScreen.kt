package com.aura.app.ui.screens.profile

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

@Composable internal fun PaymentSettingsScreen(bank:String,save:(String)->Unit,saveQris:(String)->Unit, modifier: Modifier = Modifier) {
    var info by rememberSaveable(bank){mutableStateOf(bank)}
    var qrisUri by rememberSaveable{mutableStateOf("")}
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            qrisUri = uri.toString()
            saveQris(qrisUri)
        }
    }
    Page(modifier = modifier) {
        Heading(stringResource(R.string.screen_copy_107),"Pengaturan rekening dan QRIS toko")
        Panel {
            Text(stringResource(R.string.screen_copy_147),fontWeight=FontWeight.Bold)
            Text(stringResource(R.string.screen_copy_148),fontSize=11.sp,color=Muted)
            Spacer(Modifier.height(10.dp))
            Button({picker.launch(arrayOf("image/*"))},colors=ButtonDefaults.buttonColors(containerColor=Red)) { Text(stringResource(R.string.screen_copy_149)) }
            if(qrisUri.isNotBlank()) Text("Gambar tersimpan: ${qrisUri.take(48)}…",fontSize=10.sp,color=Green)
        }
        Input(stringResource(R.string.screen_copy_150),info,{info=it})
        Primary(stringResource(R.string.screen_copy_151),{save(info)})
    }
}
