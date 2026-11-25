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

@Composable internal fun RegisterOneScreen(name:String,setName:(String)->Unit,email:String,setEmail:(String)->Unit,phone:String,setPhone:(String)->Unit,password:String,setPassword:(String)->Unit,next:()->Unit,back:()->Unit, modifier: Modifier = Modifier) {
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_021),"Langkah 1 dari 2 • Informasi akun Owner")
        Input(stringResource(R.string.screen_copy_022),name,setName); Input(stringResource(R.string.screen_copy_023),email,setEmail); Input(stringResource(R.string.screen_copy_024),phone,setPhone,number=true); Input(stringResource(R.string.screen_copy_018),password,setPassword,password=true)
        Primary(stringResource(R.string.screen_copy_025),next,name.isNotBlank()&&email.contains("@")&&password.length>=6); Secondary(stringResource(R.string.screen_copy_026),back)
    }
}
