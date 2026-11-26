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

data class RegisterUiState(
    val namaLengkap: String = "",
    val email: String = "",
    val nomorHp: String = "",
    val kataSandi: String = "",
    val namaToko: String = "",
    val jenisUsaha: String = "Retail / Sembako",
    val saldoAwalText: String = "0",
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val errorMsg: String? = null,
    @StringRes val errorResId: Int? = null,
) {
    val stepOneValid: Boolean
        get() = namaLengkap.isNotBlank() &&
            EMAIL_REGEX.matches(email.trim()) &&
            kataSandi.length >= MIN_PASSWORD_LENGTH

    val stepTwoValid: Boolean
        get() {
            val saldo = saldoAwalText.ifBlank { "0" }.toLongOrNull()
            return namaToko.isNotBlank() && saldo != null && saldo >= 0L
        }

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
        private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}

sealed interface RegisterEvent {
    data class RegistrasiBerhasil(val pesanPeringatanProfil: String?) : RegisterEvent
}

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val isOnlineProvider: () -> Boolean,
    private val scopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val workScope: CoroutineScope get() = scopeOverride ?: viewModelScope

    private val _uiState = MutableStateFlow(RegisterUiState(isOnline = isOnlineProvider()))
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegisterEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<RegisterEvent> = _events.asSharedFlow()

    fun refreshKoneksi() {
        _uiState.update { it.copy(isOnline = isOnlineProvider()) }
    }

    fun ubahNamaLengkap(value: String) {
        _uiState.update { it.copy(namaLengkap = value, errorMsg = null, errorResId = null) }
    }

    fun ubahEmail(value: String) {
        _uiState.update { it.copy(email = value, errorMsg = null, errorResId = null) }
    }

    fun ubahNomorHp(value: String) {
        _uiState.update { it.copy(nomorHp = value, errorMsg = null, errorResId = null) }
    }

    fun ubahKataSandi(value: String) {
        _uiState.update { it.copy(kataSandi = value, errorMsg = null, errorResId = null) }
    }

    fun ubahNamaToko(value: String) {
        _uiState.update { it.copy(namaToko = value, errorMsg = null, errorResId = null) }
    }

    fun ubahJenisUsaha(value: String) {
        _uiState.update { it.copy(jenisUsaha = value, errorMsg = null, errorResId = null) }
    }

    fun ubahSaldoAwal(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _uiState.update { it.copy(saldoAwalText = digitsOnly, errorMsg = null, errorResId = null) }
    }

    fun selesaikanRegistrasi() {
        val online = isOnlineProvider()
        val state = _uiState.value
        if (!online) {
            _uiState.update { it.copy(isOnline = false, errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        if (!state.stepOneValid) {
            _uiState.update { it.copy(isOnline = true, errorResId = R.string.auth_error_register_step1, errorMsg = null) }
            return
        }
        val saldoAwal = state.saldoAwalText.ifBlank { "0" }.toLongOrNull()
        if (state.namaToko.isBlank() || saldoAwal == null || saldoAwal < 0L) {
            _uiState.update { it.copy(isOnline = true, errorResId = R.string.auth_error_register_step2, errorMsg = null) }
            return
        }

        workScope.launch {
            _uiState.update { it.copy(isLoading = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                val hasil = authRepository.registerOwner(
                    nama = state.namaLengkap,
                    email = state.email,
                    nomorHp = state.nomorHp,
                    kataSandi = state.kataSandi,
                    namaToko = state.namaToko,
                    jenisUsaha = state.jenisUsaha,
                    saldoAwal = saldoAwal,
                )
                _uiState.update { it.copy(isLoading = false, kataSandi = "") }
                _events.emit(RegisterEvent.RegistrasiBerhasil(hasil.pesanPeringatanProfil))
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
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RegisterViewModel(
                    authRepository = authRepository,
                    isOnlineProvider = { koneksiChecker.isOnline() },
                ) as T
        }
    }
}
