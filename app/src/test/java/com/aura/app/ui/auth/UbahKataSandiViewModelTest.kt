package com.aura.app.ui.auth

import com.aura.app.R
import com.aura.app.core.model.Peran
import com.aura.app.data.preferences.RiwayatAkunLogin
import com.aura.app.data.preferences.SesiAktif
import com.aura.app.data.repository.AuthRepository
import com.aura.app.data.repository.HasilRegistrasiOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class UbahKataSandiViewModelTest {

    @Test
    fun `kata sandi wajib hanya diperbarui setelah permintaan sukses`() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val repository = FakeAuthRepository()
        try {
            val viewModel = UbahKataSandiViewModel(
                authRepository = repository,
                isOnlineProvider = { true },
                wajibGantiSandi = true,
                scopeOverride = scope,
            )
            viewModel.ubahKataSandiSaatIni("SandiAwal123")
            viewModel.ubahKataSandiBaru("SandiBaru123")
            viewModel.ubahKonfirmasiKataSandi("SandiBaru123")
            val event = async(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
                viewModel.events.first()
            }

            viewModel.simpan()

            assertEquals(UbahKataSandiEvent.Berhasil(wajibSebelumnya = true), event.await())
            assertEquals("SandiAwal123" to "SandiBaru123", repository.changedPasswords)
            assertFalse(viewModel.uiState.value.wajibGantiSandi)
            assertEquals("", viewModel.uiState.value.kataSandiBaru)
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun `kata sandi baru di bawah delapan karakter tidak dikirim`() {
        val repository = FakeAuthRepository()
        val viewModel = UbahKataSandiViewModel(
            authRepository = repository,
            isOnlineProvider = { true },
            wajibGantiSandi = true,
            scopeOverride = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        viewModel.ubahKataSandiSaatIni("SandiAwal123")
        viewModel.ubahKataSandiBaru("1234567")
        viewModel.ubahKonfirmasiKataSandi("1234567")

        viewModel.simpan()

        assertEquals(R.string.auth_error_password_min_8, viewModel.uiState.value.errorResId)
        assertNull(repository.changedPasswords)
    }

    private class FakeAuthRepository : AuthRepository {
        override val sesiAktif = MutableStateFlow<SesiAktif?>(null)
        override val riwayatAkun = MutableStateFlow<List<RiwayatAkunLogin>>(emptyList())
        var changedPasswords: Pair<String, String>? = null

        override suspend fun cekSesiSaatBuka(): SesiAktif? = null
        override suspend fun loginOwner(email: String, kataSandi: String): SesiAktif = error("Tidak dipakai di test")
        override suspend fun loginKasir(idToko: String, namaAkun: String, kataSandi: String): SesiAktif =
            error("Tidak dipakai di test")
        override suspend fun registerOwner(
            nama: String,
            email: String,
            nomorHp: String,
            kataSandi: String,
            namaToko: String,
            jenisUsaha: String,
            saldoAwal: Long,
        ): HasilRegistrasiOwner = error("Tidak dipakai di test")
        override suspend fun ubahKataSandi(kataSandiSaatIni: String, kataSandiBaru: String) {
            changedPasswords = kataSandiSaatIni to kataSandiBaru
        }
        override suspend fun logout() = Unit
    }
}
