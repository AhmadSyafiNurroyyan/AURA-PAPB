package com.aura.app.ui.screens.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.R
import com.aura.app.ui.auth.RegisterUiState
import com.aura.app.ui.components.Heading
import com.aura.app.ui.components.Input
import com.aura.app.ui.components.Page
import com.aura.app.ui.components.Primary
import com.aura.app.ui.components.Secondary
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.ui.theme.Muted
import com.aura.app.ui.theme.Red

@Composable
internal fun RegisterTwoScreen(
    uiState: RegisterUiState,
    onNamaTokoChange: (String) -> Unit,
    onJenisUsahaChange: (String) -> Unit,
    onSaldoAwalChange: (String) -> Unit,
    onSelesai: () -> Unit,
    onKembali: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorText = uiState.errorMsg ?: uiState.errorResId?.let { stringResource(it) }
    Page(modifier = modifier) {
        Heading(
            title = stringResource(R.string.screen_copy_027),
            subtitle = stringResource(R.string.auth_register_step2_subtitle),
        )
        Input(stringResource(R.string.screen_copy_028), uiState.namaToko, onNamaTokoChange)
        Input(stringResource(R.string.screen_copy_029), uiState.jenisUsaha, onJenisUsahaChange)
        Input(stringResource(R.string.screen_copy_030), uiState.saldoAwalText, onSaldoAwalChange, number = true)
        Text(
            text = stringResource(R.string.auth_register_opening_balance_helper),
            fontSize = 11.sp,
            color = Muted,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        if (!uiState.isOnline) {
            Text(
                text = stringResource(R.string.auth_error_offline),
                fontSize = 12.sp,
                color = Red,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        } else if (!errorText.isNullOrBlank()) {
            Text(
                text = errorText,
                fontSize = 12.sp,
                color = Red,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Primary(
            text = stringResource(R.string.screen_copy_031),
            onClick = onSelesai,
            enabled = !uiState.isLoading && uiState.isOnline && uiState.stepTwoValid,
        )
        Spacer(Modifier.height(8.dp))
        Secondary(stringResource(R.string.screen_copy_026), onKembali)
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterTwoScreenPreview() {
    AuraTheme {
        RegisterTwoScreen(
            uiState = RegisterUiState(namaToko = "Toko Maju"),
            onNamaTokoChange = {},
            onJenisUsahaChange = {},
            onSaldoAwalChange = {},
            onSelesai = {},
            onKembali = {},
        )
    }
}
