package com.aura.app.ui.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aura.app.R
import com.aura.app.core.model.Peran
import com.aura.app.data.network.KoneksiChecker
import com.aura.app.data.preferences.RiwayatAkunLogin
import com.aura.app.data.preferences.SesiAktif
import com.aura.app.data.remote.GlobalSessionHandler
import com.aura.app.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AuthSessionEvent {
    data class PaksaKeLogin(val pesan: String) : AuthSessionEvent
    data class GantiAkunBerhasil(val sesi: SesiAktif) : AuthSessionEvent
    data object LogoutBerhasil : AuthSessionEvent
}

data class GantiAkunDialogState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    @StringRes val errorResId: Int? = null,
)

class AuthSessionViewModel(
    private val authRepository: AuthRepository,
    private val globalSessionHandler: GlobalSessionHandler,
    private val koneksiChecker: KoneksiChecker,
) : ViewModel() {

    val sesiAktif: StateFlow<SesiAktif?> = authRepository.sesiAktif.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val riwayatAkun: StateFlow<List<RiwayatAkunLogin>> = authRepository.riwayatAkun.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _startupChecked = MutableStateFlow(false)
    val startupChecked: StateFlow<Boolean> = _startupChecked.asStateFlow()

    private val _gantiAkunState = MutableStateFlow(GantiAkunDialogState())
    val gantiAkunState: StateFlow<GantiAkunDialogState> = _gantiAkunState.asStateFlow()

    private val _events = MutableSharedFlow<AuthSessionEvent>(extraBufferCapacity = 2)
    val events: SharedFlow<AuthSessionEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            try {
                authRepository.cekSesiSaatBuka()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Abaikan gangguan jaringan sementara saat verifikasi startup.
            } finally {
                _startupChecked.value = true
            }
        }
        viewModelScope.launch {
            globalSessionHandler.sessionExpiredEvents.collect { pesan ->
                _events.emit(AuthSessionEvent.PaksaKeLogin(pesan))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _events.emit(AuthSessionEvent.LogoutBerhasil)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _events.emit(AuthSessionEvent.LogoutBerhasil)
            }
        }
    }

    fun clearGantiAkunError() {
        _gantiAkunState.update { it.copy(errorMsg = null, errorResId = null) }
    }

    fun gantiAkun(akun: RiwayatAkunLogin, kataSandi: String) {
        if (!koneksiChecker.isOnline()) {
            _gantiAkunState.update { it.copy(errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        if (kataSandi.isBlank()) {
            _gantiAkunState.update { it.copy(errorResId = R.string.auth_error_password_required, errorMsg = null) }
            return
        }
        viewModelScope.launch {
            _gantiAkunState.update { it.copy(isLoading = true, errorMsg = null, errorResId = null) }
            try {
                val sesi = when (akun.peran) {
                    Peran.OWNER -> {
                        val email = akun.email.orEmpty()
                        if (email.isBlank()) {
                            _gantiAkunState.update {
                                it.copy(isLoading = false, errorResId = R.string.auth_error_email_required)
                            }
                            return@launch
                        }
                        authRepository.loginOwner(email, kataSandi)
                    }

                    Peran.KASIR -> authRepository.loginKasir(akun.idToko, akun.nama, kataSandi)
                }
                _gantiAkunState.update { it.copy(isLoading = false) }
                _events.emit(AuthSessionEvent.GantiAkunBerhasil(sesi))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _gantiAkunState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message,
                        errorResId = if (e.message.isNullOrBlank()) R.string.auth_error_generic else null,
                    )
                }
            }
        }
    }

    companion object {
        fun factory(
            authRepository: AuthRepository,
            globalSessionHandler: GlobalSessionHandler,
            koneksiChecker: KoneksiChecker,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AuthSessionViewModel(authRepository, globalSessionHandler, koneksiChecker) as T
        }
    }
}
