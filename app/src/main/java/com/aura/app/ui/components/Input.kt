package com.aura.app.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.aura.app.data.*
import com.aura.app.ui.components.*
import com.aura.app.ui.theme.*
import com.aura.app.core.format.RupiahFormatter
import com.aura.app.core.format.TanggalFormatter
import java.util.Locale

@Composable internal fun Input(label:String,value:String,onChange:(String)->Unit,number:Boolean=false,password:Boolean=false,placeholder:String="") {
    Column(Modifier.fillMaxWidth().padding(bottom=10.dp)) {
        Text(label,fontSize=12.sp,fontWeight=FontWeight.Bold,color=Ink)
        OutlinedTextField(value,onChange,Modifier.fillMaxWidth(),singleLine=true,
            placeholder={Text(placeholder.ifBlank{"Masukkan $label"},fontSize=12.sp)},
            keyboardOptions=if(number) KeyboardOptions(keyboardType=KeyboardType.Number) else KeyboardOptions.Default,
            visualTransformation=if(password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            shape=RoundedCornerShape(13.dp))
    }
}
