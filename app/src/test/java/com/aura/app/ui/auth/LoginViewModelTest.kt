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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun `login kasir mengirim tiga field dan mengarahkan ke ganti sandi wajib`() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val repository = FakeAuthRepository(
            SesiAktif(
                token = "token",
                expiresAt = 1L,
                akunId = "akun-1",
                idToko = "TOKO1234",
                peran = Peran.KASIR,
                nama = "kasir",
                wajibGantiSandi = true,
            ),
        )
        try {
            val viewModel = LoginViewModel(repository, { true }, scope)
            viewModel.pilihPeran(Peran.KASIR)
            viewModel.ubahIdToko("toko1234")
            viewModel.ubahNamaAkun(" kasir ")
            viewModel.ubahKataSandi("SandiAwal123")
            val event = async(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
                viewModel.events.first()
            }

            viewModel.masuk()

            assertEquals(LoginEvent.WajibGantiSandi, event.await())
            assertEquals(listOf("TOKO1234", " kasir ", "SandiAwal123"), repository.loginKasirArguments)
            assertEquals("", viewModel.uiState.value.kataSandi)
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun `login owner tanpa email menampilkan validasi tanpa memanggil repository`() {
        val repository = FakeAuthRepository()
        val viewModel = LoginViewModel(repository, { true }, CoroutineScope(SupervisorJob() + Dispatchers.Unconfined))

        viewModel.ubahKataSandi("kata-sandi")
        viewModel.masuk()

        assertEquals(R.string.auth_error_email_required, viewModel.uiState.value.errorResId)
        assertNull(repository.loginKasirArguments)
        assertTrue(repository.loginOwnerCalled.not())
    }

    private class FakeAuthRepository(
        private val loginResult: SesiAktif = SesiAktif(
            token = "token",
            expiresAt = 1L,
            akunId = "akun-1",
            idToko = "TOKO1234",
            peran = Peran.OWNER,
            nama = "owner",
        ),
    ) : AuthRepository {
        override val sesiAktif = MutableStateFlow<SesiAktif?>(null)
        override val riwayatAkun = MutableStateFlow<List<RiwayatAkunLogin>>(emptyList())
        var loginKasirArguments: List<String>? = null
        var loginOwnerCalled = false

        override suspend fun cekSesiSaatBuka(): SesiAktif? = null
        override suspend fun loginOwner(email: String, kataSandi: String): SesiAktif {
            loginOwnerCalled = true
            return loginResult
        }
        override suspend fun loginKasir(idToko: String, namaAkun: String, kataSandi: String): SesiAktif {
            loginKasirArguments = listOf(idToko, namaAkun, kataSandi)
            return loginResult
        }
        override suspend fun registerOwner(
            nama: String,
            email: String,
            nomorHp: String,
            kataSandi: String,
            namaToko: String,
            jenisUsaha: String,
            saldoAwal: Long,
        ): HasilRegistrasiOwner = error("Tidak dipakai di test")

        override suspend fun ubahKataSandi(kataSandiSaatIni: String, kataSandiBaru: String) = Unit
        override suspend fun logout() = Unit
    }
}
