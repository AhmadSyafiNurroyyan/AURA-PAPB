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

@Composable internal fun WelcomeScreen(login:()->Unit,register:()->Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Purple,Color(0xFF4E0A24),Color(0xFF75000C),Red)))) {
        Column(Modifier.align(Alignment.Center).padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally) {
            Box(Modifier.size(92.dp).clip(RoundedCornerShape(26.dp)).background(Color.White.copy(.12f)),contentAlignment=Alignment.Center){Text(stringResource(R.string.screen_copy_032),fontSize=58.sp,color=Color.White,fontWeight=FontWeight.ExtraBold)}
            Spacer(Modifier.height(20.dp)); Text(stringResource(R.string.screen_copy_015),fontSize=36.sp,color=Color.White,fontWeight=FontWeight.ExtraBold,letterSpacing=5.sp)
            Text(stringResource(R.string.screen_copy_033),fontSize=20.sp,color=Color.White,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
            Text(stringResource(R.string.screen_copy_034),fontSize=13.sp,color=Color.White.copy(.8f),textAlign=TextAlign.Center)
        }
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp)) {
            Button(login,Modifier.fillMaxWidth().height(50.dp),colors=ButtonDefaults.buttonColors(containerColor=Color.White),shape=RoundedCornerShape(15.dp)){Text(stringResource(R.string.screen_copy_035),color=Red,fontWeight=FontWeight.Bold)}
            Spacer(Modifier.height(10.dp))
            OutlinedButton(register,Modifier.fillMaxWidth().height(50.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=Color.White),shape=RoundedCornerShape(15.dp)){Text(stringResource(R.string.screen_copy_036),fontWeight=FontWeight.Bold)}
            Text(stringResource(R.string.screen_copy_037),Modifier.fillMaxWidth().padding(top=18.dp),textAlign=TextAlign.Center,color=Color.White.copy(.7f),fontSize=10.sp)
        }
    }
}
