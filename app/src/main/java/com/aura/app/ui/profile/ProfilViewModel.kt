package com.aura.app.ui.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aura.app.R
import com.aura.app.core.model.Peran
import com.aura.app.data.network.KoneksiChecker
import com.aura.app.data.preferences.SesiAktif
import com.aura.app.data.repository.AkunRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfilUiState(
    val peran: Peran = Peran.OWNER,
    val idToko: String = "",
    val nama: String = "",
    val email: String = "",
    val nomorHp: String = "",
    val isSaving: Boolean = false,
    val isOnline: Boolean = true,
    val errorMsg: String? = null,
    @StringRes val errorResId: Int? = null,
)

sealed interface EditProfilEvent {
    data object SimpanBerhasil : EditProfilEvent
}

class ProfilViewModel(
    private val akunRepository: AkunRepository,
    private val isOnlineProvider: () -> Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfilUiState(isOnline = isOnlineProvider()))
    val uiState: StateFlow<EditProfilUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditProfilEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<EditProfilEvent> = _events.asSharedFlow()

    private var initializedForAccountId: String? = null

    fun muatDariSesi(sesi: SesiAktif) {
        if (initializedForAccountId == sesi.akunId) return
        initializedForAccountId = sesi.akunId
        _uiState.update {
            it.copy(
                peran = sesi.peran,
                idToko = sesi.idToko,
                nama = sesi.nama,
                email = sesi.email.orEmpty(),
                nomorHp = sesi.nomorHp.orEmpty(),
                isOnline = isOnlineProvider(),
            )
        }
        viewModelScope.launch {
            if (!isOnlineProvider()) return@launch
            try {
                val latest = akunRepository.refreshProfil()
                _uiState.update {
                    it.copy(
                        nama = latest.namaAkun,
                        email = latest.email.orEmpty(),
                        nomorHp = latest.nomorHp.orEmpty(),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Gunakan data sesi lokal jika refresh latar belakang gagal.
            }
        }
    }

    fun ubahNama(value: String) {
        _uiState.update { it.copy(nama = value, errorMsg = null, errorResId = null) }
    }

    fun ubahEmail(value: String) {
        if (_uiState.value.peran != Peran.OWNER) return
        _uiState.update { it.copy(email = value, errorMsg = null, errorResId = null) }
    }

    fun ubahNomorHp(value: String) {
        _uiState.update { it.copy(nomorHp = value, errorMsg = null, errorResId = null) }
    }

    fun simpan() {
        val online = isOnlineProvider()
        val state = _uiState.value
        if (!online) {
            _uiState.update { it.copy(isOnline = false, errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        if (state.nama.isBlank()) {
            _uiState.update { it.copy(isOnline = true, errorResId = R.string.profil_error_name_required, errorMsg = null) }
            return
        }
        if (state.peran == Peran.OWNER && state.email.isNotBlank() && !EMAIL_REGEX.matches(state.email.trim())) {
            _uiState.update { it.copy(isOnline = true, errorResId = R.string.profil_error_email_invalid, errorMsg = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                akunRepository.updateProfil(
                    nama = state.nama,
                    email = if (state.peran == Peran.OWNER) state.email else null,
                    nomorHp = state.nomorHp,
                )
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(EditProfilEvent.SimpanBerhasil)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isOnline = isOnlineProvider(),
                        errorMsg = e.message,
                        errorResId = if (e.message.isNullOrBlank()) R.string.auth_error_generic else null,
                    )
                }
            }
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

        fun factory(
            akunRepository: AkunRepository,
            koneksiChecker: KoneksiChecker,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfilViewModel(akunRepository) { koneksiChecker.isOnline() } as T
        }
    }
}
