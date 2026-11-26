package com.aura.app.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.R
import com.aura.app.core.format.TanggalFormatter
import com.aura.app.core.model.Peran
import com.aura.app.core.model.StatusAkun
import com.aura.app.data.local.entity.AkunEntity
import com.aura.app.ui.components.Heading
import com.aura.app.ui.components.Input
import com.aura.app.ui.components.Panel
import com.aura.app.ui.components.PasswordInput
import com.aura.app.ui.components.Primary
import com.aura.app.ui.components.Secondary
import com.aura.app.ui.profile.InfoLoginKasir
import com.aura.app.ui.profile.ManajemenKasirUiState
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.ui.theme.Bg
import com.aura.app.ui.theme.Green
import com.aura.app.ui.theme.Muted
import com.aura.app.ui.theme.Purple
import com.aura.app.ui.theme.Red
import com.aura.app.ui.theme.Soft

@Composable
internal fun CashierAccountsScreen(
    uiState: ManajemenKasirUiState,
    onBuatSandiOtomatis: () -> String,
    onTambahKasir: (namaAkun: String, kataSandiAwal: String, onSuccess: () -> Unit) -> Unit,
    onUbahStatus: (akunId: String, aktif: Boolean) -> Unit,
    onResetSandi: (akunId: String, namaAkun: String, sandiBaru: String, onSuccess: () -> Unit) -> Unit,
    onHapusKasir: (akunId: String) -> Unit,
    onRefresh: () -> Unit,
    onTutupInfoLogin: () -> Unit,
    onPesan: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var showCreateForm by rememberSaveable { mutableStateOf(false) }
    var newName by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }

    var resetTarget by remember { mutableStateOf<AkunEntity?>(null) }
    var resetPasswordText by rememberSaveable { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<AkunEntity?>(null) }

    val errorText = uiState.errorMsg ?: uiState.errorResId?.let { stringResource(it) }
    val infoCopiedText = stringResource(R.string.kasir_info_disalin)
    val waUnavailableText = stringResource(R.string.kasir_wa_tidak_tersedia)

    Column(
        modifier
            .fillMaxSize()
            .background(Bg)
            .padding(18.dp),
    ) {
        Heading(stringResource(R.string.screen_copy_141), stringResource(R.string.kasir_subtitle))
        Primary(
            text = stringResource(R.string.screen_copy_142),
            onClick = {
                newName = ""
                newPassword = onBuatSandiOtomatis()
                showCreateForm = true
            },
            enabled = !uiState.isSubmitting && uiState.isOnline,
        )
        Spacer(Modifier.height(10.dp))

        if (!errorText.isNullOrBlank()) {
            Panel {
                Text(errorText, fontSize = 12.sp, color = Red)
                Spacer(Modifier.height(6.dp))
                Secondary(stringResource(R.string.kasir_coba_lagi), onRefresh)
            }
        }

        if (showCreateForm) {
            Panel {
                Input(
                    label = stringResource(R.string.auth_label_nama_akun),
                    value = newName,
                    onChange = { newName = it },
                )
                PasswordInput(
                    label = stringResource(R.string.kasir_sandi_awal_label),
                    value = newPassword,
                    onChange = { newPassword = it },
                    enabled = !uiState.isSubmitting,
                )
                TextButton(
                    onClick = { newPassword = onBuatSandiOtomatis() },
                    enabled = !uiState.isSubmitting,
                ) {
                    Text(stringResource(R.string.kasir_buat_sandi_otomatis), color = Purple, fontSize = 12.sp)
                }
                Primary(
                    text = stringResource(R.string.screen_copy_145),
                    onClick = {
                        onTambahKasir(newName, newPassword) {
                            showCreateForm = false
                            newName = ""
                            newPassword = ""
                        }
                    },
                    enabled = !uiState.isSubmitting && newName.isNotBlank() && newPassword.length >= 8,
                )
                Spacer(Modifier.height(6.dp))
                Secondary(stringResource(R.string.screen_copy_043)) { showCreateForm = false }
            }
        }

        when {
            uiState.isLoading && uiState.daftarKasir.isEmpty() -> {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Red)
                }
            }

            uiState.daftarKasir.isEmpty() -> {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.kasir_empty), fontSize = 13.sp, color = Muted)
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(uiState.daftarKasir, key = { it.id }) { kasir ->
                        val aktif = kasir.status == StatusAkun.AKTIF
                        Panel {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(kasir.namaAkun, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    val lastLoginText = kasir.terakhirLogin?.let {
                                        stringResource(R.string.kasir_terakhir_login_format, TanggalFormatter.format(it))
                                    } ?: stringResource(R.string.kasir_belum_pernah_login)
                                    Text(lastLoginText, fontSize = 11.sp, color = Muted)
                                }
                                Text(
                                    text = stringResource(
                                        if (aktif) R.string.kasir_status_aktif else R.string.kasir_status_nonaktif,
                                    ),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (aktif) Green else Muted,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Soft)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = stringResource(
                                        if (aktif) R.string.kasir_nonaktifkan else R.string.kasir_aktifkan,
                                    ),
                                    color = Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .clickable(enabled = !uiState.isSubmitting && uiState.isOnline) {
                                            onUbahStatus(kasir.id, !aktif)
                                        }
                                        .padding(vertical = 8.dp),
                                )
                                Text(
                                    text = stringResource(R.string.screen_copy_146),
                                    color = Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .clickable(enabled = !uiState.isSubmitting && uiState.isOnline) {
                                            resetPasswordText = onBuatSandiOtomatis()
                                            resetTarget = kasir
                                        }
                                        .padding(vertical = 8.dp),
                                )
                                Text(
                                    text = stringResource(R.string.kasir_hapus_cta),
                                    color = Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .clickable(enabled = !uiState.isSubmitting && uiState.isOnline) {
                                            deleteTarget = kasir
                                        }
                                        .padding(vertical = 8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    resetTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { if (!uiState.isSubmitting) resetTarget = null },
            title = { Text(stringResource(R.string.kasir_reset_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.kasir_reset_pesan, target.namaAkun), fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    PasswordInput(
                        label = stringResource(R.string.kasir_sandi_awal_label),
                        value = resetPasswordText,
                        onChange = { resetPasswordText = it },
                        enabled = !uiState.isSubmitting,
                    )
                    TextButton(
                        onClick = { resetPasswordText = onBuatSandiOtomatis() },
                        enabled = !uiState.isSubmitting,
                    ) {
                        Text(stringResource(R.string.kasir_buat_sandi_otomatis), color = Purple, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetSandi(target.id, target.namaAkun, resetPasswordText) {
                            resetTarget = null
                            resetPasswordText = ""
                        }
                    },
                    enabled = !uiState.isSubmitting && resetPasswordText.length >= 8,
                ) {
                    Text(stringResource(R.string.kasir_reset_simpan), color = Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { resetTarget = null },
                    enabled = !uiState.isSubmitting,
                ) {
                    Text(stringResource(R.string.screen_copy_043))
                }
            },
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.kasir_hapus_confirm_title)) },
            text = { Text(stringResource(R.string.kasir_hapus_confirm_message, target.namaAkun)) },
            confirmButton = {
                TextButton(onClick = {
                    onHapusKasir(target.id)
                    deleteTarget = null
                }) {
                    Text(stringResource(R.string.kasir_hapus_cta), color = Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(stringResource(R.string.screen_copy_043))
                }
            },
        )
    }

    uiState.infoLoginBaru?.let { info ->
        ShareKasirInfoDialog(
            info = info,
            onSalin = { pesan ->
                clipboard.setText(AnnotatedString(pesan))
                onPesan(infoCopiedText)
            },
            onBagikanWhatsApp = { pesan ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/?text=${Uri.encode(pesan)}"),
                )
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    onPesan(waUnavailableText)
                }
            },
            onDismiss = onTutupInfoLogin,
        )
    }
}

@Composable
private fun ShareKasirInfoDialog(
    info: InfoLoginKasir,
    onSalin: (String) -> Unit,
    onBagikanWhatsApp: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val shareText = stringResource(
        R.string.kasir_share_template,
        info.idToko,
        info.namaAkun,
        info.kataSandi,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (info.isReset) R.string.kasir_info_login_title_reset else R.string.kasir_info_login_title_baru,
                ),
            )
        },
        text = {
            Column {
                Text(stringResource(R.string.auth_label_id_toko) + ": ${info.idToko}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(stringResource(R.string.auth_label_nama_akun) + ": ${info.namaAkun}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(stringResource(R.string.screen_copy_018) + ": ${info.kataSandi}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.kasir_info_login_note), fontSize = 11.sp, color = Muted)
            }
        },
        confirmButton = {
            TextButton(onClick = { onBagikanWhatsApp(shareText) }) {
                Text(stringResource(R.string.kasir_bagikan_wa), color = Red)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onSalin(shareText) }) {
                    Text(stringResource(R.string.kasir_bagikan_salin), color = Purple)
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.screen_copy_082))
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun CashierAccountsPreview() {
    AuraTheme {
        CashierAccountsScreen(
            uiState = ManajemenKasirUiState(
                idToko = "AB12CD34",
                isLoading = false,
                daftarKasir = listOf(
                    AkunEntity(
                        id = "1",
                        idToko = "AB12CD34",
                        namaAkun = "siti.aminah",
                        peran = Peran.KASIR,
                        status = StatusAkun.AKTIF,
                    ),
                ),
            ),
            onBuatSandiOtomatis = { "Abc12345" },
            onTambahKasir = { _, _, _ -> },
            onUbahStatus = { _, _ -> },
            onResetSandi = { _, _, _, _ -> },
            onHapusKasir = {},
            onRefresh = {},
            onTutupInfoLogin = {},
            onPesan = {},
        )
    }
}
