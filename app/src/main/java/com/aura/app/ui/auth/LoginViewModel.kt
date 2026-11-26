package com.aura.app.ui.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aura.app.R
import com.aura.app.core.model.Peran
import com.aura.app.data.network.KoneksiChecker
import com.aura.app.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val peranTerpilih: Peran = Peran.OWNER,
    val email: String = "",
    val idToko: String = "",
    val namaAkun: String = "",
    val kataSandi: String = "",
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val errorMsg: String? = null,
    @StringRes val errorResId: Int? = null,
)

sealed interface LoginEvent {
    data object MasukKeDashboard : LoginEvent
    data object MasukKeKasir : LoginEvent
    data object WajibGantiSandi : LoginEvent
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val isOnlineProvider: () -> Boolean,
    private val scopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val workScope: CoroutineScope get() = scopeOverride ?: viewModelScope

    private val _uiState = MutableStateFlow(LoginUiState(isOnline = isOnlineProvider()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun refreshKoneksi() {
        _uiState.update { it.copy(isOnline = isOnlineProvider()) }
    }

    fun pilihPeran(peran: Peran) {
        _uiState.update {
            it.copy(peranTerpilih = peran, errorMsg = null, errorResId = null)
        }
    }

    fun ubahEmail(value: String) {
        _uiState.update { it.copy(email = value, errorMsg = null, errorResId = null) }
    }

    fun ubahIdToko(value: String) {
        _uiState.update { it.copy(idToko = value.uppercase(), errorMsg = null, errorResId = null) }
    }

    fun ubahNamaAkun(value: String) {
        _uiState.update { it.copy(namaAkun = value, errorMsg = null, errorResId = null) }
    }

    fun ubahKataSandi(value: String) {
        _uiState.update { it.copy(kataSandi = value, errorMsg = null, errorResId = null) }
    }

    fun masuk() {
        val online = isOnlineProvider()
        val state = _uiState.value
        if (!online) {
            _uiState.update { it.copy(isOnline = false, errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        val validationError = validasiForm(state)
        if (validationError != null) {
            _uiState.update { it.copy(isOnline = true, errorResId = validationError, errorMsg = null) }
            return
        }

        workScope.launch {
            _uiState.update { it.copy(isLoading = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                val sesi = when (state.peranTerpilih) {
                    Peran.OWNER -> authRepository.loginOwner(state.email, state.kataSandi)
                    Peran.KASIR -> authRepository.loginKasir(state.idToko, state.namaAkun, state.kataSandi)
                }
                _uiState.update { it.copy(isLoading = false, kataSandi = "") }
                val navEvent = when {
                    sesi.wajibGantiSandi -> LoginEvent.WajibGantiSandi
                    sesi.peran == Peran.OWNER -> LoginEvent.MasukKeDashboard
                    else -> LoginEvent.MasukKeKasir
                }
                _events.emit(navEvent)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isOnline = isOnlineProvider(),
                        errorMsg = e.message,
                        errorResId = if (e.message.isNullOrBlank()) R.string.auth_error_generic else null,
                    )
                }
            }
        }
    }

    private fun validasiForm(state: LoginUiState): Int? = when (state.peranTerpilih) {
        Peran.OWNER -> when {
            state.email.isBlank() -> R.string.auth_error_email_required
            state.kataSandi.isBlank() -> R.string.auth_error_password_required
            else -> null
        }

        Peran.KASIR -> when {
            state.idToko.isBlank() -> R.string.auth_error_store_id_required
            state.namaAkun.isBlank() -> R.string.auth_error_username_required
            state.kataSandi.isBlank() -> R.string.auth_error_password_required
            else -> null
        }
    }

    companion object {
        fun factory(
            authRepository: AuthRepository,
            koneksiChecker: KoneksiChecker,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                LoginViewModel(
                    authRepository = authRepository,
                    isOnlineProvider = { koneksiChecker.isOnline() },
                ) as T
        }
    }
}
