package com.aura.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.R
import com.aura.app.core.model.Peran
import com.aura.app.data.preferences.RiwayatAkunLogin
import com.aura.app.data.preferences.SesiAktif
import com.aura.app.ui.auth.GantiAkunDialogState
import com.aura.app.ui.theme.Muted
import com.aura.app.ui.theme.Purple
import com.aura.app.ui.theme.Red
import com.aura.app.ui.theme.Soft

@Composable
internal fun PasswordInput(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    Column(modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            placeholder = { if (placeholder.isNotBlank()) Text(placeholder, fontSize = 12.sp) },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { visible = !visible }) {
                    Text(
                        text = stringResource(
                            if (visible) R.string.auth_password_hide_short else R.string.auth_password_show_short,
                        ),
                        fontSize = 11.sp,
                        color = Red,
                    )
                }
            },
            shape = RoundedCornerShape(13.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AvatarSheet(
    sesi: SesiAktif?,
    onDismiss: () -> Unit,
    onCopyStoreId: () -> Unit,
    onEditProfil: () -> Unit,
    onUbahSandi: () -> Unit,
    onBukaGantiAkun: () -> Unit,
    onMintaKeluar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    val peranLabel = stringResource(
        if (sesi?.peran == Peran.OWNER) R.string.role_owner else R.string.role_kasir,
    )
    ModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier, containerColor = Color.White) {
        Column(Modifier.fillMaxWidth().padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(Purple),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = sesi?.nama?.take(1)?.uppercase().orEmpty().ifBlank { "A" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(sesi?.nama ?: "-", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = peranLabel,
                            modifier = Modifier.clip(CircleShape).background(Soft).padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = Purple,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.profil_id_toko_format, sesi?.idToko ?: "-"),
                            fontSize = 11.sp,
                            color = Muted,
                        )
                    }
                }
                if (!sesi?.idToko.isNullOrBlank()) {
                    TextButton(onClick = {
                        clipboard.setText(AnnotatedString(sesi!!.idToko))
                        onCopyStoreId()
                    }) {
                        Text(stringResource(R.string.profil_salin), color = Red, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            ActionRow(
                title = stringResource(R.string.sheet_avatar_edit_profil),
                detail = stringResource(
                    if (sesi?.peran == Peran.OWNER) {
                        R.string.sheet_avatar_edit_profil_detail
                    } else {
                        R.string.sheet_avatar_edit_profil_kasir_detail
                    },
                ),
                onClick = { onDismiss(); onEditProfil() },
            )
            ActionRow(
                title = stringResource(R.string.ubah_sandi_title),
                detail = stringResource(R.string.sheet_avatar_ubah_sandi_detail),
                onClick = { onDismiss(); onUbahSandi() },
            )
            ActionRow(
                title = stringResource(R.string.screen_copy_153),
                detail = stringResource(R.string.sheet_avatar_ganti_akun_detail),
                onClick = { onDismiss(); onBukaGantiAkun() },
            )
            ActionRow(
                title = stringResource(R.string.screen_copy_154),
                detail = stringResource(R.string.sheet_avatar_keluar_detail),
                onClick = { onDismiss(); onMintaKeluar() },
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GantiAkunSheet(
    riwayatAkun: List<RiwayatAkunLogin>,
    activeAccountId: String?,
    dialogState: GantiAkunDialogState,
    onDismiss: () -> Unit,
    onSubmitPassword: (RiwayatAkunLogin, String) -> Unit,
    onClearError: () -> Unit,
    onTambahAkunLain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedAccount by remember { mutableStateOf<RiwayatAkunLogin?>(null) }
    var password by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier, containerColor = Color.White) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Text(stringResource(R.string.ganti_akun_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(stringResource(R.string.ganti_akun_subtitle), fontSize = 12.sp, color = Muted)
            Spacer(Modifier.height(12.dp))

            if (riwayatAkun.isEmpty()) {
                Text(stringResource(R.string.ganti_akun_kosong), fontSize = 12.sp, color = Muted)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height((riwayatAkun.size.coerceAtMost(5) * 64).dp),
                ) {
                    items(riwayatAkun, key = { it.akunId }) { akun ->
                        val isCurrent = akun.akunId == activeAccountId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Soft)
                                .clickable(enabled = !isCurrent) {
                                    password = ""
                                    onClearError()
                                    selectedAccount = akun
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(akun.nama, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                val peranTxt = stringResource(
                                    if (akun.peran == Peran.OWNER) R.string.role_owner else R.string.role_kasir,
                                )
                                Text("$peranTxt • ${akun.idToko}", fontSize = 11.sp, color = Muted)
                            }
                            if (isCurrent) {
                                Text(
                                    stringResource(R.string.kasir_status_aktif),
                                    fontSize = 11.sp,
                                    color = Purple,
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {
                                Text("›", fontSize = 20.sp, color = Red)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Primary(stringResource(R.string.ganti_akun_tambah_lain), onClick = {
                onDismiss()
                onTambahAkunLain()
            })
            Spacer(Modifier.height(16.dp))
        }
    }

    selectedAccount?.let { target ->
        val errText = dialogState.errorMsg ?: dialogState.errorResId?.let { stringResource(it) }
        AlertDialog(
            onDismissRequest = {
                if (!dialogState.isLoading) {
                    selectedAccount = null
                    password = ""
                    onClearError()
                }
            },
            title = { Text(stringResource(R.string.ganti_akun_verifikasi_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.ganti_akun_verifikasi_pesan, target.nama), fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    PasswordInput(
                        label = stringResource(R.string.screen_copy_018),
                        value = password,
                        onChange = { password = it; onClearError() },
                        enabled = !dialogState.isLoading,
                    )
                    if (!errText.isNullOrBlank()) {
                        Text(errText, fontSize = 11.sp, color = Red)
                    }
                    if (dialogState.isLoading) {
                        CircularProgressIndicator(Modifier.padding(top = 8.dp).size(22.dp), color = Red)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onSubmitPassword(target, password) },
                    enabled = !dialogState.isLoading,
                ) {
                    Text(stringResource(R.string.ganti_akun_masuk_cta), color = Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedAccount = null
                        password = ""
                        onClearError()
                    },
                    enabled = !dialogState.isLoading,
                ) {
                    Text(stringResource(R.string.screen_copy_043))
                }
            },
        )
    }
}

@Composable
internal fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.logout_confirm_title)) },
        text = { Text(stringResource(R.string.logout_confirm_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.logout_confirm_cta), color = Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.screen_copy_043))
            }
        },
    )
}
