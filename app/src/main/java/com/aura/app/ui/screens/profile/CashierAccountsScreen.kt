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

@Composable internal fun CashierAccountsScreen(items:List<AccountEntity>,save:(String?,String,String,String,String)->Unit,active:(String,Boolean)->Unit,reset:(String,String)->Unit, modifier: Modifier = Modifier) {
    var showForm by remember{mutableStateOf(false)};var editingId by remember{mutableStateOf<String?>(null)};var name by remember{mutableStateOf("")};var username by remember{mutableStateOf("")};var password by remember{mutableStateOf("123456")};var phone by remember{mutableStateOf("")}
    Page(modifier = modifier) { Heading(stringResource(R.string.screen_copy_141),"Akun pegawai yang terhubung ke toko");Primary(stringResource(R.string.screen_copy_142),{editingId=null;showForm=true;name="";username="";password="123456";phone=""})
        if(showForm)Panel{Input(stringResource(R.string.screen_copy_143),name,{name=it});Input(stringResource(R.string.screen_copy_144),username,{username=it});Input(stringResource(R.string.screen_copy_018),password,{password=it},password=true);Input(stringResource(R.string.screen_copy_024),phone,{phone=it});Primary(stringResource(R.string.screen_copy_145),{save(editingId,name,username,password,phone);showForm=false;editingId=null;name="";username="";password="123456";phone=""},name.isNotBlank()&&username.isNotBlank());Secondary(stringResource(R.string.screen_copy_043),{showForm=false;editingId=null})}
        items.forEach{Panel{Text(it.name,fontWeight=FontWeight.Bold);Text("@${it.username} • ${if(it.active)"Aktif" else "Nonaktif"}",fontSize=11.sp,color=Muted);Row{Text(stringResource(R.string.screen_copy_083),Modifier.clickable{editingId=it.id;name=it.name;username=it.username;phone=it.phone;password="";showForm=true}.padding(8.dp),color=Red);Text(if(it.active)"Nonaktifkan" else "Aktifkan",Modifier.clickable{active(it.id,!it.active)}.padding(8.dp),color=Red);Text(stringResource(R.string.screen_copy_146),Modifier.clickable{reset(it.id,"123456")}.padding(8.dp),color=Red)}}}
    }
}
