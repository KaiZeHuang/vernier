package com.kaizehuang.vernier.core.detector

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ConsentIdTest {

    @Test
    fun `accepts snake case identifiers`() {
        listOf("revocation_network", "network", "crl2").forEach { value ->
            assertEquals(value, ConsentId(value).value)
        }
    }

    @Test
    fun `rejects identifiers that look like presentation text`() {
        listOf("", "Revocation network", "revocation-network", "_network", "network_", "TEE").forEach { value ->
            assertThrows(IllegalArgumentException::class.java) { ConsentId(value) }
        }
    }
}
