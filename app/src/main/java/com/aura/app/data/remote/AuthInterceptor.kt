package com.aura.app.data.remote

import com.aura.app.data.local.AppDatabase
import com.aura.app.data.preferences.SesiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Interceptor OkHttp untuk menyisipkan header Bearer token dan mendeteksi HTTP 401 global (K-32).
 * Endpoint login/register publik dikecualikan agar gagal login tidak memicu pembersihan sesi global.
 */
class AuthInterceptor(
    private val tokenProvider: () -> String?,
    private val onUnauthorized: () -> Unit,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath
        val isPublicAuth = PUBLIC_AUTH_SUFFIXES.any { path.endsWith(it) }

        val token = if (!isPublicAuth) tokenProvider()?.takeIf { it.isNotBlank() } else null
        val request = if (token != null && original.header("Authorization") == null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        val response = chain.proceed(request)
        if (!isPublicAuth && response.code == 401) {
            onUnauthorized()
        }
        return response
    }

    companion object {
        private val PUBLIC_AUTH_SUFFIXES = listOf(
            "/auth/owner/register",
            "/auth/owner/login",
            "/auth/kasir/login",
        )
    }
}

/**
 * Menangani event 401 global: menghapus sesi aktif DataStore dan cache AppDatabase,
 * lalu mengirim pesan event ke UI agar user diarahkan ke layar Login.
 */
class GlobalSessionHandler(
    private val sesiRepository: SesiRepository,
    private val appDatabase: AppDatabase,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val clearing = AtomicBoolean(false)

    private val _sessionExpiredEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val sessionExpiredEvents: SharedFlow<String> = _sessionExpiredEvents.asSharedFlow()

    fun onUnauthorized() {
        if (!clearing.compareAndSet(false, true)) return
        scope.launch {
            try {
                sesiRepository.hapusSesiAktif()
                appDatabase.clearAllCache()
                _sessionExpiredEvents.tryEmit("Sesi Anda telah berakhir. Silakan masuk kembali.")
            } finally {
                clearing.set(false)
            }
        }
    }
}
