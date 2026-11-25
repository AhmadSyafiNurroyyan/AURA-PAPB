package com.aura.server.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal data class PasswordDigest(val hash: String, val salt: String)

internal object PasswordHasher {
    private const val ITERATIONS = 210_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_BYTES = 16
    private val random = SecureRandom()

    fun hash(password: String): PasswordDigest {
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        return PasswordDigest(derive(password, salt), Base64.getEncoder().encodeToString(salt))
    }

    fun verify(password: String, digest: String, salt: String): Boolean = try {
        val expected = Base64.getDecoder().decode(digest)
        val saltBytes = Base64.getDecoder().decode(salt)
        val actual = Base64.getDecoder().decode(derive(password, saltBytes))
        MessageDigest.isEqual(expected, actual)
    } catch (_: IllegalArgumentException) {
        false
    }

    private fun derive(password: String, salt: ByteArray): String {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        return try {
            Base64.getEncoder().encodeToString(
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded,
            )
        } finally {
            spec.clearPassword()
        }
    }
}
