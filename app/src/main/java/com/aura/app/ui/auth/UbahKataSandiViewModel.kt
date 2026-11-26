package com.aura.app.ui.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aura.app.R
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

data class UbahKataSandiUiState(
    val wajibGantiSandi: Boolean = false,
    val kataSandiSaatIni: String = "",
    val kataSandiBaru: String = "",
    val konfirmasiKataSandi: String = "",
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val errorMsg: String? = null,
    @StringRes val errorResId: Int? = null,
)

sealed interface UbahKataSandiEvent {
    data class Berhasil(val wajibSebelumnya: Boolean) : UbahKataSandiEvent
}

class UbahKataSandiViewModel(
    private val authRepository: AuthRepository,
    private val isOnlineProvider: () -> Boolean,
    wajibGantiSandi: Boolean,
    private val scopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val workScope: CoroutineScope get() = scopeOverride ?: viewModelScope

    private val _uiState = MutableStateFlow(
        UbahKataSandiUiState(
            wajibGantiSandi = wajibGantiSandi,
            isOnline = isOnlineProvider(),
        ),
    )
    val uiState: StateFlow<UbahKataSandiUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UbahKataSandiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UbahKataSandiEvent> = _events.asSharedFlow()

    fun refreshKoneksi() {
        _uiState.update { it.copy(isOnline = isOnlineProvider()) }
    }

    fun ubahKataSandiSaatIni(value: String) {
        _uiState.update { it.copy(kataSandiSaatIni = value, errorMsg = null, errorResId = null) }
    }

    fun ubahKataSandiBaru(value: String) {
        _uiState.update { it.copy(kataSandiBaru = value, errorMsg = null, errorResId = null) }
    }

    fun ubahKonfirmasiKataSandi(value: String) {
        _uiState.update { it.copy(konfirmasiKataSandi = value, errorMsg = null, errorResId = null) }
    }

    fun simpan() {
        val online = isOnlineProvider()
        val state = _uiState.value
        if (!online) {
            _uiState.update { it.copy(isOnline = false, errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        val errRes = when {
            state.kataSandiSaatIni.isBlank() -> R.string.auth_error_current_password_required
            state.kataSandiBaru.length < 8 -> R.string.auth_error_password_min_8
            state.kataSandiBaru != state.konfirmasiKataSandi -> R.string.auth_error_password_mismatch
            else -> null
        }
        if (errRes != null) {
            _uiState.update { it.copy(isOnline = true, errorResId = errRes, errorMsg = null) }
            return
        }

        workScope.launch {
            _uiState.update { it.copy(isLoading = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                val wasMandatory = state.wajibGantiSandi
                authRepository.ubahKataSandi(state.kataSandiSaatIni, state.kataSandiBaru)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        wajibGantiSandi = false,
                        kataSandiSaatIni = "",
                        kataSandiBaru = "",
                        konfirmasiKataSandi = "",
                    )
                }
                _events.emit(UbahKataSandiEvent.Berhasil(wasMandatory))
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

    companion object {
        fun factory(
            authRepository: AuthRepository,
            koneksiChecker: KoneksiChecker,
            wajibGantiSandi: Boolean,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                UbahKataSandiViewModel(authRepository, { koneksiChecker.isOnline() }, wajibGantiSandi) as T
        }
    }
}
