package com.aura.app.ui.screens.profile

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
import com.aura.app.core.model.Peran
import com.aura.app.ui.components.Heading
import com.aura.app.ui.components.Input
import com.aura.app.ui.components.Page
import com.aura.app.ui.components.Panel
import com.aura.app.ui.components.Primary
import com.aura.app.ui.components.Secondary
import com.aura.app.ui.profile.EditProfilUiState
import com.aura.app.ui.theme.AuraTheme
import com.aura.app.ui.theme.Muted
import com.aura.app.ui.theme.Red

@Composable
internal fun EditProfilScreen(
    uiState: EditProfilUiState,
    onNamaChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNomorHpChange: (String) -> Unit,
    onSimpan: () -> Unit,
    onKembali: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorText = uiState.errorMsg ?: uiState.errorResId?.let { stringResource(it) }
    Page(modifier = modifier) {
        Heading(
            title = stringResource(R.string.profil_edit_title),
            subtitle = stringResource(R.string.profil_edit_subtitle),
        )
        Panel {
            Input(
                label = stringResource(R.string.screen_copy_143),
                value = uiState.nama,
                onChange = onNamaChange,
            )
            Input(
                label = stringResource(R.string.screen_copy_024),
                value = uiState.nomorHp,
                onChange = onNomorHpChange,
                number = true,
            )
            if (uiState.peran == Peran.OWNER) {
                Input(
                    label = stringResource(R.string.screen_copy_023),
                    value = uiState.email,
                    onChange = onEmailChange,
                )
            } else {
                Text(
                    text = stringResource(R.string.profil_email_owner_only_note),
                    fontSize = 11.sp,
                    color = Muted,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }

            if (!errorText.isNullOrBlank()) {
                Text(errorText, fontSize = 12.sp, color = Red, modifier = Modifier.padding(bottom = 8.dp))
            }

            Primary(
                text = stringResource(R.string.profil_simpan),
                onClick = onSimpan,
                enabled = !uiState.isSaving && uiState.isOnline,
            )
            Spacer(Modifier.height(8.dp))
            Secondary(stringResource(R.string.screen_copy_026), onKembali)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfilPreview() {
    AuraTheme {
        EditProfilScreen(
            uiState = EditProfilUiState(peran = Peran.OWNER, nama = "Budi", email = "owner@aura.id"),
            onNamaChange = {},
            onEmailChange = {},
            onNomorHpChange = {},
            onSimpan = {},
            onKembali = {},
        )
    }
}
