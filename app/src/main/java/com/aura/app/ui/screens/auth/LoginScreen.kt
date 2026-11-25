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

@Composable internal fun LoginScreen(onLogin:(String,String,String)->Unit,onRegister:()->Unit, modifier: Modifier = Modifier) {
    var role by rememberSaveable{mutableStateOf("owner")}
    var identity by rememberSaveable{mutableStateOf("owner@tokoberkah.com")}
    var password by rememberSaveable{mutableStateOf("123456")}
    Box(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Purple,Color(0xFF650014),Red)))) {
        Column(Modifier.fillMaxWidth().align(Alignment.Center).padding(24.dp)) {
            Text(stringResource(R.string.screen_copy_015),fontSize=30.sp,color=Color.White,fontWeight=FontWeight.ExtraBold,letterSpacing=4.sp)
            Text(stringResource(R.string.screen_copy_016),fontSize=23.sp,color=Color.White,fontWeight=FontWeight.Bold)
            Text(stringResource(R.string.screen_copy_017),fontSize=12.sp,color=Color.White.copy(.8f))
            Spacer(Modifier.height(20.dp))
            Row(Modifier.clip(CircleShape).background(Color.White.copy(.15f)).padding(4.dp)) {
                listOf("owner" to "Owner","kasir" to "Kasir").forEach{(v,t)->
                    Box(Modifier.weight(1f).clip(CircleShape).background(if(role==v)Color.White else Color.Transparent).clickable{role=v;identity=if(v=="owner")"owner@tokoberkah.com" else "siti.aminah"}.padding(12.dp),contentAlignment=Alignment.Center){Text(t,color=if(role==v)Red else Color.White,fontWeight=FontWeight.Bold)}
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(if(role=="owner")"Email" else "Username Kasir (format IDTOKO/username)",color=Color.White,fontSize=12.sp)
            OutlinedTextField(identity,{identity=it},Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(12.dp))
            Spacer(Modifier.height(10.dp)); Text(stringResource(R.string.screen_copy_018),color=Color.White,fontSize=12.sp)
            OutlinedTextField(password,{password=it},Modifier.fillMaxWidth(),singleLine=true,visualTransformation=PasswordVisualTransformation(),shape=RoundedCornerShape(12.dp))
            Spacer(Modifier.height(18.dp)); Primary("Masuk sebagai ${if(role=="owner")"Owner" else "Kasir"}",{onLogin(identity,password,role)})
            Text(stringResource(R.string.screen_copy_019),Modifier.fillMaxWidth().clickable{onRegister()}.padding(14.dp),textAlign=TextAlign.Center,color=Color.White,fontSize=12.sp)
            Text(stringResource(R.string.screen_copy_020),fontSize=10.sp,color=Color.White.copy(.72f),textAlign=TextAlign.Center)
        }
    }
}
