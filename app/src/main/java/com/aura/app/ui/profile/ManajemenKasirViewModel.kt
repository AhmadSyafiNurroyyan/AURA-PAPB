package com.aura.app.ui.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aura.app.R
import com.aura.app.data.local.entity.AkunEntity
import com.aura.app.data.network.KoneksiChecker
import com.aura.app.data.repository.AkunRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom

data class InfoLoginKasir(
    val idToko: String,
    val namaAkun: String,
    val kataSandi: String,
    val isReset: Boolean,
)

data class ManajemenKasirUiState(
    val idToko: String = "",
    val daftarKasir: List<AkunEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isOnline: Boolean = true,
    val errorMsg: String? = null,
    @StringRes val errorResId: Int? = null,
    val infoLoginBaru: InfoLoginKasir? = null,
)

class ManajemenKasirViewModel(
    private val akunRepository: AkunRepository,
    private val isOnlineProvider: () -> Boolean,
    private val scopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val workScope: CoroutineScope get() = scopeOverride ?: viewModelScope

    private val _uiState = MutableStateFlow(ManajemenKasirUiState(isOnline = isOnlineProvider()))
    val uiState: StateFlow<ManajemenKasirUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var loadedStoreId: String? = null

    fun muat(idToko: String) {
        if (idToko.isBlank()) return
        if (loadedStoreId != idToko) {
            loadedStoreId = idToko
            _uiState.update { it.copy(idToko = idToko, isLoading = true) }
            observeJob?.cancel()
            observeJob = workScope.launch {
                akunRepository.observeKasir(idToko).collect { list ->
                    _uiState.update { it.copy(daftarKasir = list) }
                }
            }
        }
        refresh()
    }

    fun refresh() {
        val storeId = loadedStoreId ?: return
        val online = isOnlineProvider()
        if (!online) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOnline = false,
                    errorResId = R.string.auth_error_offline,
                    errorMsg = null,
                )
            }
            return
        }
        workScope.launch {
            _uiState.update { it.copy(isLoading = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                akunRepository.refreshKasir(storeId)
                _uiState.update { it.copy(isLoading = false) }
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

    fun buatSandiOtomatis(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789"
        val random = SecureRandom()
        return buildString(10) {
            repeat(10) { append(chars[random.nextInt(chars.length)]) }
        }
    }

    fun tambahKasir(namaAkun: String, kataSandiAwal: String, onSuccess: () -> Unit = {}) {
        val storeId = loadedStoreId ?: return
        if (!isOnlineProvider()) {
            _uiState.update { it.copy(isOnline = false, errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        if (namaAkun.isBlank()) {
            _uiState.update { it.copy(errorResId = R.string.kasir_error_username_required, errorMsg = null) }
            return
        }
        if (kataSandiAwal.length < 8) {
            _uiState.update { it.copy(errorResId = R.string.auth_error_password_min_8, errorMsg = null) }
            return
        }
        workScope.launch {
            _uiState.update { it.copy(isSubmitting = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                val created = akunRepository.tambahKasir(namaAkun, kataSandiAwal)
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        infoLoginBaru = InfoLoginKasir(
                            idToko = storeId,
                            namaAkun = created.namaAkun,
                            kataSandi = kataSandiAwal,
                            isReset = false,
                        ),
                    )
                }
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMsg = e.message,
                        errorResId = if (e.message.isNullOrBlank()) R.string.auth_error_generic else null,
                    )
                }
            }
        }
    }

    fun ubahStatus(akunId: String, aktif: Boolean) {
        if (!isOnlineProvider()) {
            _uiState.update { it.copy(isOnline = false, errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        workScope.launch {
            _uiState.update { it.copy(isSubmitting = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                akunRepository.ubahStatusKasir(akunId, aktif)
                _uiState.update { it.copy(isSubmitting = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMsg = e.message,
                        errorResId = if (e.message.isNullOrBlank()) R.string.auth_error_generic else null,
                    )
                }
            }
        }
    }

    fun resetKataSandi(akunId: String, namaAkun: String, kataSandiBaru: String, onSuccess: () -> Unit = {}) {
        val storeId = loadedStoreId ?: return
        if (!isOnlineProvider()) {
            _uiState.update { it.copy(isOnline = false, errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        if (kataSandiBaru.length < 8) {
            _uiState.update { it.copy(errorResId = R.string.auth_error_password_min_8, errorMsg = null) }
            return
        }
        workScope.launch {
            _uiState.update { it.copy(isSubmitting = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                akunRepository.resetKataSandiKasir(akunId, kataSandiBaru)
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        infoLoginBaru = InfoLoginKasir(
                            idToko = storeId,
                            namaAkun = namaAkun,
                            kataSandi = kataSandiBaru,
                            isReset = true,
                        ),
                    )
                }
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMsg = e.message,
                        errorResId = if (e.message.isNullOrBlank()) R.string.auth_error_generic else null,
                    )
                }
            }
        }
    }

    fun hapusKasir(akunId: String) {
        if (!isOnlineProvider()) {
            _uiState.update { it.copy(isOnline = false, errorResId = R.string.auth_error_offline, errorMsg = null) }
            return
        }
        workScope.launch {
            _uiState.update { it.copy(isSubmitting = true, isOnline = true, errorMsg = null, errorResId = null) }
            try {
                akunRepository.hapusKasir(akunId)
                _uiState.update { it.copy(isSubmitting = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMsg = e.message,
                        errorResId = if (e.message.isNullOrBlank()) R.string.auth_error_generic else null,
                    )
                }
            }
        }
    }

    fun tutupDialogInfoLogin() {
        _uiState.update { it.copy(infoLoginBaru = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMsg = null, errorResId = null) }
    }

    companion object {
        fun factory(
            akunRepository: AkunRepository,
            koneksiChecker: KoneksiChecker,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ManajemenKasirViewModel(
                    akunRepository = akunRepository,
                    isOnlineProvider = { koneksiChecker.isOnline() },
                ) as T
        }
    }
}
