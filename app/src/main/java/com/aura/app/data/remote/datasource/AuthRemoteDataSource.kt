package com.aura.app.data.remote.datasource

import com.aura.app.data.remote.ApiErrorParser
import com.aura.app.data.remote.ApiServerException
import com.aura.app.data.remote.api.AuthApiService
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

class AuthRemoteDataSource(
    private val api: AuthApiService,
) {
    suspend fun registerOwner(request: OwnerRegisterRequestDto): SessionResponseDto =
        unwrap(api.registerOwner(request))

    suspend fun loginOwner(request: OwnerLoginRequestDto): SessionResponseDto =
        unwrap(api.loginOwner(request))

    suspend fun loginKasir(request: KasirLoginRequestDto): SessionResponseDto =
        unwrap(api.loginKasir(request))

    suspend fun me(): MeResponseDto = unwrap(api.me())

    suspend fun logout() {
        unwrapEmpty(api.logout())
    }

    suspend fun changePassword(request: ChangePasswordRequestDto) {
        unwrapEmpty(api.changePassword(request))
    }

    suspend fun revokeOtherSessions(): RevokeSessionsResponseDto =
        unwrap(api.revokeOtherSessions())

    suspend fun deleteOwnerAccount(request: OwnerDeleteRequestDto): OwnerDeleteResponseDto =
        unwrap(api.deleteOwnerAccount(request))

    suspend fun listKasir(): KasirListResponseDto = unwrap(api.listKasir())

    suspend fun createKasir(request: CreateKasirRequestDto): AccountEnvelopeDto =
        unwrap(api.createKasir(request))

    suspend fun setKasirStatus(akunId: String, request: SetKasirStatusRequestDto): AccountEnvelopeDto =
        unwrap(api.setKasirStatus(akunId, request))

    suspend fun resetKasirPassword(akunId: String, request: ResetKasirPasswordRequestDto): AccountEnvelopeDto =
        unwrap(api.resetKasirPassword(akunId, request))

    suspend fun deleteKasir(akunId: String): AccountEnvelopeDto =
        unwrap(api.deleteKasir(akunId))

    suspend fun updateProfil(request: UpdateProfileRequestDto): AccountEnvelopeDto =
        unwrap(api.updateProfil(request))

    suspend fun getToko(): TokoDto = unwrap(api.getToko())

    suspend fun updateToko(request: UpdateStoreRequestDto): TokoEnvelopeDto =
        unwrap(api.updateToko(request))

    suspend fun updateSaldoAwal(request: UpdateOpeningBalanceRequestDto): UpdateOpeningBalanceResponseDto =
        unwrap(api.updateSaldoAwal(request))

    private fun <T> unwrap(response: Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw ApiServerException(
                httpCode = response.code(),
                kode = "RESPONS_KOSONG",
                message = "Respons server kosong.",
            )
        }
        throw ApiErrorParser.parse(response.code(), response.errorBody()?.string())
    }

    private fun unwrapEmpty(response: Response<Unit>) {
        if (response.isSuccessful) return
        throw ApiErrorParser.parse(response.code(), response.errorBody()?.string())
    }
}
