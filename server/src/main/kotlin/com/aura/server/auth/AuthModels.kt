package com.aura.server.auth

import java.util.UUID
import io.ktor.server.auth.Principal

internal enum class Peran { OWNER, KASIR }
internal enum class StatusAkun { AKTIF, NONAKTIF, DIHAPUS }

internal data class AkunPrincipal(
    val akunId: UUID,
    val tokoId: String,
    val peran: Peran,
    val jti: UUID,
) : Principal

internal data class Akun(
    val id: UUID,
    val idToko: String,
    val email: String?,
    val nama: String,
    val namaNormalisasi: String,
    val peran: Peran,
    val hash: String,
    val salt: String,
    val status: StatusAkun,
    val wajibGantiSandi: Boolean,
    val nomorHp: String?,
    val fotoFileId: UUID?,
    val gagalLogin: Int,
    val terkunciSampai: Long?,
    val terakhirLogin: Long?,
    val hapusTokoPada: Long? = null,
)

internal data class TokoRingkas(
    val idToko: String,
    val namaToko: String,
    val jenisUsaha: String?,
    val alamat: String?,
    val logoFileId: UUID?,
    val saldoAwal: Long,
    val saldoKumulatif: Long,
)
