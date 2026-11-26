package com.aura.server.auth

import java.util.UUID

internal data class OwnerRegisterRequest(
    val email: String?, val kataSandi: String?, val namaToko: String?, val jenisUsaha: String?,
    val saldoAwal: Long?, val fotoProfilFileId: UUID?,
)
internal data class OwnerLoginRequest(val email: String?, val kataSandi: String?)
internal data class KasirLoginRequest(val idToko: String?, val namaAkun: String?, val kataSandi: String?)
internal data class ChangePasswordRequest(val kataSandiSaatIni: String?, val kataSandiBaru: String?)
internal data class CreateKasirRequest(val id: UUID?, val namaAkun: String?, val kataSandiAwal: String?)
internal data class ResetKasirPasswordRequest(val kataSandiAwal: String?)
internal data class SetKasirStatusRequest(val status: String?)
internal data class UpdateProfileRequest(val nama: String?, val email: String?, val nomorHp: String?, val fotoFileId: UUID?)
internal data class OwnerDeleteRequest(val kataSandiSaatIni: String?, val konfirmasi: String?)
internal data class UpdateStoreRequest(val namaToko: String?, val jenisUsaha: String?, val alamat: String?, val logoFileId: UUID?)
internal data class UpdateOpeningBalanceRequest(val saldoAwalBaru: Long?)

internal data class AkunResponse(
    val id: UUID,
    val idToko: String,
    val email: String?,
    val namaAkun: String,
    val peran: String,
    val status: String,
    val wajibGantiSandi: Boolean,
    val nomorHp: String?,
    val fotoFileId: UUID?,
    val terakhirLogin: Long?,
)
internal data class TokoResponse(
    val idToko: String, val namaToko: String, val jenisUsaha: String?, val alamat: String?,
    val logoFileId: UUID?, val saldoAwal: Long, val saldoKumulatif: Long,
)
internal data class SessionResponse(val akun: AkunResponse, val toko: TokoResponse, val token: String, val expiresAt: Long, val wajibGantiSandi: Boolean = false)
internal data class AccountEnvelope(val akun: AkunResponse)
internal data class OwnerDeleteResponse(val penghapusanDijadwalkanPada: Long, val dapatDipulihkanSampai: Long)

internal fun Akun.toResponse() = AkunResponse(id, idToko, email, nama, peran.name, status.name, wajibGantiSandi, nomorHp, fotoFileId, terakhirLogin)
internal fun TokoRingkas.toResponse() = TokoResponse(idToko, namaToko, jenisUsaha, alamat, logoFileId, saldoAwal, saldoKumulatif)
