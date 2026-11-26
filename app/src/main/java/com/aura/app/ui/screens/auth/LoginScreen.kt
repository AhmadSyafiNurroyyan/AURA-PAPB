package com.aura.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.R
import com.aura.app.core.model.Peran
import com.aura.app.ui.auth.LoginUiState
import com.aura.app.ui.components.Primary
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.ui.theme.Purple
import com.aura.app.ui.theme.Red

@Composable
internal fun LoginScreen(
    uiState: LoginUiState,
    onPilihPeran: (Peran) -> Unit,
    onEmailChange: (String) -> Unit,
    onIdTokoChange: (String) -> Unit,
    onNamaAkunChange: (String) -> Unit,
    onKataSandiChange: (String) -> Unit,
    onMasuk: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val errorText = uiState.errorMsg ?: uiState.errorResId?.let { stringResource(it) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.65f),
        cursorColor = Color.White,
        focusedPlaceholderColor = Color.White.copy(alpha = 0.55f),
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.55f),
    )

    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Purple, Color(0xFF650014), Red)))
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(24.dp),
        ) {
            Text(
                stringResource(R.string.screen_copy_015),
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
            )
            Text(
                stringResource(R.string.screen_copy_016),
                fontSize = 23.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.screen_copy_017),
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
            )
            Spacer(Modifier.height(20.dp))

            Row(
                Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(4.dp),
            ) {
                listOf(
                    Peran.OWNER to stringResource(R.string.role_owner),
                    Peran.KASIR to stringResource(R.string.role_kasir),
                ).forEach { (peran, label) ->
                    val selected = uiState.peranTerpilih == peran
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (selected) Color.White else Color.Transparent)
                            .clickable(enabled = !uiState.isLoading) { onPilihPeran(peran) }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            label,
                            color = if (selected) Red else Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (uiState.peranTerpilih == Peran.OWNER) {
                Text(stringResource(R.string.screen_copy_023), color = Color.White, fontSize = 12.sp)
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.auth_hint_email), fontSize = 12.sp) },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                )
            } else {
                Text(stringResource(R.string.auth_label_id_toko), color = Color.White, fontSize = 12.sp)
                OutlinedTextField(
                    value = uiState.idToko,
                    onValueChange = onIdTokoChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.auth_hint_id_toko), fontSize = 12.sp) },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                )
                Spacer(Modifier.height(10.dp))
                Text(stringResource(R.string.auth_label_nama_akun), color = Color.White, fontSize = 12.sp)
                OutlinedTextField(
                    value = uiState.namaAkun,
                    onValueChange = onNamaAkunChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.auth_hint_nama_akun), fontSize = 12.sp) },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(stringResource(R.string.screen_copy_018), color = Color.White, fontSize = 12.sp)
            OutlinedTextField(
                value = uiState.kataSandi,
                onValueChange = onKataSandiChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.auth_hint_password), fontSize = 12.sp) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = stringResource(
                                if (passwordVisible) R.string.auth_password_hide_short else R.string.auth_password_show_short,
                            ),
                            color = Color.White,
                            fontSize = 11.sp,
                        )
                    }
                },
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
            )

            if (uiState.peranTerpilih == Peran.KASIR) {
                Text(
                    text = stringResource(R.string.auth_kasir_forgot_password_info),
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            if (!uiState.isOnline) {
                Text(
                    text = stringResource(R.string.auth_error_offline),
                    color = Color(0xFFFFD8D8),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp),
                )
            } else if (!errorText.isNullOrBlank()) {
                Text(
                    text = errorText,
                    color = Color(0xFFFFD8D8),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }

            Spacer(Modifier.height(18.dp))
            Primary(
                text = stringResource(
                    if (uiState.peranTerpilih == Peran.OWNER) R.string.auth_login_owner_cta else R.string.auth_login_kasir_cta,
                ),
                onClick = onMasuk,
                enabled = !uiState.isLoading && uiState.isOnline,
            )

            Text(
                stringResource(R.string.screen_copy_019),
                Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !uiState.isLoading) { onRegister() }
                    .padding(14.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 12.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    AuraTheme {
        LoginScreen(
            uiState = LoginUiState(peranTerpilih = Peran.KASIR),
            onPilihPeran = {},
            onEmailChange = {},
            onIdTokoChange = {},
            onNamaAkunChange = {},
            onKataSandiChange = {},
            onMasuk = {},
            onRegister = {},
        )
    }
}
