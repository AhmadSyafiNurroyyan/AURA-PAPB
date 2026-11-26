package com.aura.app.data.remote.api

import com.aura.app.data.remote.dto.AccountEnvelopeDto
import com.aura.app.data.remote.dto.ChangePasswordRequestDto
import com.aura.app.data.remote.dto.CreateKasirRequestDto
import com.aura.app.data.remote.dto.KasirListResponseDto
import com.aura.app.data.remote.dto.KasirLoginRequestDto
import com.aura.app.data.remote.dto.MeResponseDto
import com.aura.app.data.remote.dto.OwnerDeleteRequestDto
import com.aura.app.data.remote.dto.OwnerDeleteResponseDto
import com.aura.app.data.remote.dto.OwnerLoginRequestDto
import com.aura.app.data.remote.dto.OwnerRegisterRequestDto
import com.aura.app.data.remote.dto.ResetKasirPasswordRequestDto
import com.aura.app.data.remote.dto.RevokeSessionsResponseDto
import com.aura.app.data.remote.dto.SessionResponseDto
import com.aura.app.data.remote.dto.SetKasirStatusRequestDto
import com.aura.app.data.remote.dto.TokoDto
import com.aura.app.data.remote.dto.TokoEnvelopeDto
import com.aura.app.data.remote.dto.UpdateOpeningBalanceRequestDto
import com.aura.app.data.remote.dto.UpdateOpeningBalanceResponseDto
import com.aura.app.data.remote.dto.UpdateProfileRequestDto
import com.aura.app.data.remote.dto.UpdateStoreRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @POST("auth/owner/register")
    suspend fun registerOwner(@Body request: OwnerRegisterRequestDto): Response<SessionResponseDto>

    @POST("auth/owner/login")
    suspend fun loginOwner(@Body request: OwnerLoginRequestDto): Response<SessionResponseDto>

    @POST("auth/kasir/login")
    suspend fun loginKasir(@Body request: KasirLoginRequestDto): Response<SessionResponseDto>

    @GET("auth/me")
    suspend fun me(): Response<MeResponseDto>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<Unit>

    @POST("auth/sessions/revoke-others")
    suspend fun revokeOtherSessions(): Response<RevokeSessionsResponseDto>

    @HTTP(method = "DELETE", path = "auth/owner/account", hasBody = true)
    suspend fun deleteOwnerAccount(@Body request: OwnerDeleteRequestDto): Response<OwnerDeleteResponseDto>

    @GET("kasir")
    suspend fun listKasir(): Response<KasirListResponseDto>

    @POST("kasir")
    suspend fun createKasir(@Body request: CreateKasirRequestDto): Response<AccountEnvelopeDto>

    @PATCH("kasir/{akunId}")
    suspend fun setKasirStatus(
        @Path("akunId") akunId: String,
        @Body request: SetKasirStatusRequestDto,
    ): Response<AccountEnvelopeDto>

    @POST("kasir/{akunId}/reset-password")
    suspend fun resetKasirPassword(
        @Path("akunId") akunId: String,
        @Body request: ResetKasirPasswordRequestDto,
    ): Response<AccountEnvelopeDto>

    @DELETE("kasir/{akunId}")
    suspend fun deleteKasir(@Path("akunId") akunId: String): Response<AccountEnvelopeDto>

    @PATCH("profil")
    suspend fun updateProfil(@Body request: UpdateProfileRequestDto): Response<AccountEnvelopeDto>

    @GET("toko")
    suspend fun getToko(): Response<TokoDto>

    @PATCH("toko")
    suspend fun updateToko(@Body request: UpdateStoreRequestDto): Response<TokoEnvelopeDto>

    @PATCH("toko/saldo-awal")
    suspend fun updateSaldoAwal(
        @Body request: UpdateOpeningBalanceRequestDto,
    ): Response<UpdateOpeningBalanceResponseDto>
}
