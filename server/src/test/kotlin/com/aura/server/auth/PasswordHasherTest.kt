package com.aura.server.auth

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHasherTest {
    @Test
    fun `hashes with independent salt and verifies password`() {
        val first = PasswordHasher.hash("KataSandiAman123")
        val second = PasswordHasher.hash("KataSandiAman123")

        assertNotEquals(first.salt, second.salt)
        assertTrue(PasswordHasher.verify("KataSandiAman123", first.hash, first.salt))
        assertFalse(PasswordHasher.verify("kata-salah", first.hash, first.salt))
    }
}
