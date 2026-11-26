package com.aura.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.R
import com.aura.app.ui.auth.UbahKataSandiUiState
import com.aura.app.ui.components.Heading
import com.aura.app.ui.components.Page
import com.aura.app.ui.components.Panel
import com.aura.app.ui.components.PasswordInput
import com.aura.app.ui.components.Primary
import com.aura.app.ui.components.Secondary
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.ui.theme.Red

@Composable
internal fun UbahKataSandiScreen(
    uiState: UbahKataSandiUiState,
    onKataSandiSaatIniChange: (String) -> Unit,
    onKataSandiBaruChange: (String) -> Unit,
    onKonfirmasiChange: (String) -> Unit,
    onSimpan: () -> Unit,
    onBatalAtauKeluar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorText = uiState.errorMsg ?: uiState.errorResId?.let { stringResource(it) }
    Page(modifier = modifier) {
        Heading(
            title = stringResource(R.string.ubah_sandi_title),
            subtitle = stringResource(R.string.ubah_sandi_subtitle),
        )

        if (uiState.wajibGantiSandi) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFF3CD))
                    .padding(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.ubah_sandi_wajib_banner),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF664D03),
                )
            }
        }

        if (!uiState.isOnline) {
            Text(
                text = stringResource(R.string.auth_error_offline),
                fontSize = 12.sp,
                color = Red,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Panel {
            PasswordInput(
                label = stringResource(R.string.ubah_sandi_saat_ini),
                value = uiState.kataSandiSaatIni,
                onChange = onKataSandiSaatIniChange,
                enabled = !uiState.isLoading,
            )
            PasswordInput(
                label = stringResource(R.string.ubah_sandi_baru),
                value = uiState.kataSandiBaru,
                onChange = onKataSandiBaruChange,
                enabled = !uiState.isLoading,
            )
            PasswordInput(
                label = stringResource(R.string.ubah_sandi_konfirmasi),
                value = uiState.konfirmasiKataSandi,
                onChange = onKonfirmasiChange,
                enabled = !uiState.isLoading,
            )

            if (!errorText.isNullOrBlank()) {
                Text(errorText, fontSize = 12.sp, color = Red, modifier = Modifier.padding(bottom = 8.dp))
            }

            Primary(
                text = stringResource(R.string.ubah_sandi_simpan),
                onClick = onSimpan,
                enabled = !uiState.isLoading && uiState.isOnline,
            )
            Spacer(Modifier.height(8.dp))
            Secondary(
                text = stringResource(
                    if (uiState.wajibGantiSandi) R.string.screen_copy_154 else R.string.screen_copy_026,
                ),
                onClick = onBatalAtauKeluar,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UbahKataSandiPreview() {
    AuraTheme {
        UbahKataSandiScreen(
            uiState = UbahKataSandiUiState(wajibGantiSandi = true),
            onKataSandiSaatIniChange = {},
            onKataSandiBaruChange = {},
            onKonfirmasiChange = {},
            onSimpan = {},
            onBatalAtauKeluar = {},
        )
    }
}
