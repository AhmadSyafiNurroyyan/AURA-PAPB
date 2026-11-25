package com.aura.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// ============================
// Request DTOs (spec/14 §2-3)
// ============================

data class OwnerRegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("kataSandi") val kataSandi: String,
    @SerializedName("namaToko") val namaToko: String,
    @SerializedName("jenisUsaha") val jenisUsaha: String? = null,
    @SerializedName("saldoAwal") val saldoAwal: Long,
    @SerializedName("fotoProfilFileId") val fotoProfilFileId: String? = null,
)

data class OwnerLoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("kataSandi") val kataSandi: String,
)

data class KasirLoginRequestDto(
    @SerializedName("idToko") val idToko: String,
    @SerializedName("namaAkun") val namaAkun: String,
    @SerializedName("kataSandi") val kataSandi: String,
)

data class ChangePasswordRequestDto(
    @SerializedName("kataSandiSaatIni") val kataSandiSaatIni: String,
    @SerializedName("kataSandiBaru") val kataSandiBaru: String,
)

data class OwnerDeleteRequestDto(
    @SerializedName("kataSandiSaatIni") val kataSandiSaatIni: String,
    @SerializedName("konfirmasi") val konfirmasi: String,
)

data class CreateKasirRequestDto(
    @SerializedName("id") val id: String,
    @SerializedName("namaAkun") val namaAkun: String,
    @SerializedName("kataSandiAwal") val kataSandiAwal: String,
)

data class SetKasirStatusRequestDto(
    @SerializedName("status") val status: String,
)

data class ResetKasirPasswordRequestDto(
    @SerializedName("kataSandiAwal") val kataSandiAwal: String,
)

data class UpdateProfileRequestDto(
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("nomorHp") val nomorHp: String? = null,
    @SerializedName("fotoFileId") val fotoFileId: String? = null,
)

data class UpdateStoreRequestDto(
    @SerializedName("namaToko") val namaToko: String? = null,
    @SerializedName("jenisUsaha") val jenisUsaha: String? = null,
    @SerializedName("alamat") val alamat: String? = null,
    @SerializedName("logoFileId") val logoFileId: String? = null,
)

data class UpdateOpeningBalanceRequestDto(
    @SerializedName("saldoAwalBaru") val saldoAwalBaru: Long,
)

// ============================
// Response DTOs (spec/14 §2-3)
// ============================

data class AkunDto(
    @SerializedName("id") val id: String,
    @SerializedName("idToko") val idToko: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("namaAkun") val namaAkun: String,
    @SerializedName("peran") val peran: String,
    @SerializedName("status") val status: String,
    @SerializedName("wajibGantiSandi") val wajibGantiSandi: Boolean = false,
    @SerializedName("nomorHp") val nomorHp: String? = null,
    @SerializedName("fotoFileId") val fotoFileId: String? = null,
    @SerializedName("terakhirLogin") val terakhirLogin: Long? = null,
)

data class TokoDto(
    @SerializedName("idToko") val idToko: String,
    @SerializedName("namaToko") val namaToko: String,
    @SerializedName("jenisUsaha") val jenisUsaha: String? = null,
    @SerializedName("alamat") val alamat: String? = null,
    @SerializedName("logoFileId") val logoFileId: String? = null,
    @SerializedName("saldoAwal") val saldoAwal: Long,
    @SerializedName("saldoKumulatif") val saldoKumulatif: Long,
)

data class SessionResponseDto(
    @SerializedName("akun") val akun: AkunDto,
    @SerializedName("toko") val toko: TokoDto,
    @SerializedName("token") val token: String,
    @SerializedName("expiresAt") val expiresAt: Long,
    @SerializedName("wajibGantiSandi") val wajibGantiSandi: Boolean = false,
)

data class MeResponseDto(
    @SerializedName("akun") val akun: AkunDto,
    @SerializedName("tokoRingkas") val tokoRingkas: TokoDto,
)

data class AccountEnvelopeDto(
    @SerializedName("akun") val akun: AkunDto,
)

data class TokoEnvelopeDto(
    @SerializedName("toko") val toko: TokoDto,
)

data class KasirListItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("namaAkun") val namaAkun: String,
    @SerializedName("status") val status: String,
    @SerializedName("terakhirLogin") val terakhirLogin: Long? = null,
)

data class KasirListResponseDto(
    @SerializedName("data") val data: List<KasirListItemDto>,
)

data class RevokeSessionsResponseDto(
    @SerializedName("jumlahSesiDiakhiri") val jumlahSesiDiakhiri: Int,
)

data class OwnerDeleteResponseDto(
    @SerializedName("penghapusanDijadwalkanPada") val penghapusanDijadwalkanPada: Long,
    @SerializedName("dapatDipulihkanSampai") val dapatDipulihkanSampai: Long,
)

data class UpdateOpeningBalanceResponseDto(
    @SerializedName("saldoAwal") val saldoAwal: Long,
    @SerializedName("saldoKumulatif") val saldoKumulatif: Long,
)
