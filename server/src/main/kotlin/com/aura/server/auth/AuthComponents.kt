package com.aura.server.auth

internal data class AuthComponents(
    val repository: AuthRepository,
    val jwt: JwtService,
    val service: AuthService,
)

internal fun authComponents(
    secret: String,
    repository: AuthRepository = ExposedAuthRepository(),
): AuthComponents {
    val jwt = JwtService(secret)
    return AuthComponents(repository, jwt, AuthService(repository, jwt))
}
