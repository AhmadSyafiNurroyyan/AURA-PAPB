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
import com.aura.app.ui.components.PasswordInput
import com.aura.app.ui.components.Primary
import com.aura.app.ui.components.Secondary
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.ui.theme.Muted

@Composable
internal fun RegisterOneScreen(
    uiState: RegisterUiState,
    onNamaChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNomorHpChange: (String) -> Unit,
    onKataSandiChange: (String) -> Unit,
    onLanjut: () -> Unit,
    onKembali: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Page(modifier = modifier) {
        Heading(
            title = stringResource(R.string.screen_copy_021),
            subtitle = stringResource(R.string.auth_register_step1_subtitle),
        )
        Input(stringResource(R.string.screen_copy_022), uiState.namaLengkap, onNamaChange)
        Input(stringResource(R.string.screen_copy_023), uiState.email, onEmailChange)
        Input(stringResource(R.string.screen_copy_024), uiState.nomorHp, onNomorHpChange, number = true)
        PasswordInput(
            label = stringResource(R.string.screen_copy_018),
            value = uiState.kataSandi,
            onChange = onKataSandiChange,
        )
        Text(
            text = stringResource(R.string.auth_register_password_helper),
            fontSize = 11.sp,
            color = Muted,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Primary(
            text = stringResource(R.string.screen_copy_025),
            onClick = onLanjut,
            enabled = uiState.stepOneValid,
        )
        Spacer(Modifier.height(8.dp))
        Secondary(stringResource(R.string.screen_copy_026), onKembali)
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterOneScreenPreview() {
    AuraTheme {
        RegisterOneScreen(
            uiState = RegisterUiState(),
            onNamaChange = {},
            onEmailChange = {},
            onNomorHpChange = {},
            onKataSandiChange = {},
            onLanjut = {},
            onKembali = {},
        )
    }
}
