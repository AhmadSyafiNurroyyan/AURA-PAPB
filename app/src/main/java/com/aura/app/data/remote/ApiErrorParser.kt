package com.aura.app.data.remote

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.IOException

/**
 * Exception HTTP dari server AURA dengan kode & pesan Bahasa Indonesia siap tampil.
 */
class ApiServerException(
    val httpCode: Int,
    val kode: String,
    override val message: String,
) : IOException(message)

internal data class ErrorBodyDto(
    @SerializedName("kode") val kode: String? = null,
    @SerializedName("pesan") val pesan: String? = null,
)

/**
 * Mengurai respons error JSON `{ "kode": "...", "pesan": "..." }` dari server.
 */
object ApiErrorParser {
    private val gson = Gson()

    fun parse(httpCode: Int, rawBody: String?): ApiServerException {
        val parsed = if (!rawBody.isNullOrBlank()) {
            runCatching { gson.fromJson(rawBody, ErrorBodyDto::class.java) }.getOrNull()
        } else {
            null
        }
        val kode = parsed?.kode?.takeIf { it.isNotBlank() } ?: defaultCode(httpCode)
        val pesan = parsed?.pesan?.takeIf { it.isNotBlank() } ?: defaultMessage(httpCode)
        return ApiServerException(httpCode = httpCode, kode = kode, message = pesan)
    }

    private fun defaultCode(httpCode: Int): String = when (httpCode) {
        400 -> "INPUT_TIDAK_VALID"
        401 -> "SESI_TIDAK_VALID"
        403 -> "AKSES_DITOLAK"
        404 -> "DATA_TIDAK_DITEMUKAN"
        409 -> "KONFLIK_DATA"
        413 -> "FILE_TERLALU_BESAR"
        415 -> "TIPE_FILE_TIDAK_DIDUKUNG"
        else -> "KESALAHAN_SERVER"
    }

    private fun defaultMessage(httpCode: Int): String = when (httpCode) {
        400 -> "Permintaan tidak valid."
        401 -> "Sesi tidak valid atau telah berakhir."
        403 -> "Anda tidak memiliki izin untuk aksi ini."
        404 -> "Data tidak ditemukan."
        409 -> "Terjadi konflik data."
        413 -> "Ukuran file melebihi batas maksimal 10 MiB."
        415 -> "Tipe file tidak didukung."
        else -> "Terjadi kesalahan pada server. Coba lagi nanti."
    }
}
