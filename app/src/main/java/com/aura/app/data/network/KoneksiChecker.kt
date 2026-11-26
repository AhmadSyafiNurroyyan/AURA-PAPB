package com.aura.app.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.IOException

/**
 * Exception yang dilempar saat aksi tulis dipanggil tanpa koneksi internet (K-20).
 */
class OfflineException(
    message: String = "Tidak ada koneksi internet.",
) : IOException(message)

/**
 * Memeriksa status konektivitas perangkat sebelum melakukan permintaan tulis ke server.
 */
class KoneksiChecker(private val context: Context) {
    fun isOnline(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun pastikanOnline() {
        if (!isOnline()) {
            throw OfflineException()
        }
    }
}
