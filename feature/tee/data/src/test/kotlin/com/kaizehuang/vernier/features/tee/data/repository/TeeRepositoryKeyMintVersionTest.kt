package com.kaizehuang.vernier.features.tee.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TeeRepositoryKeyMintVersionTest {

    @Test
    fun `keymint runtime identity requires equal projected versions`() {
        assertTrue(keyMintRuntimeIdentityConsistent(100, 100))
        assertTrue(keyMintRuntimeIdentityConsistent(300, 300))
        assertFalse(keyMintRuntimeIdentityConsistent(100, 300))
        assertFalse(keyMintRuntimeIdentityConsistent(300, 100))
    }

    @Test
    fun `legacy keymaster runtime identity follows aosp conversion table`() {
        assertTrue(keyMintRuntimeIdentityConsistent(1, 2))
        assertTrue(keyMintRuntimeIdentityConsistent(2, 3))
        assertTrue(keyMintRuntimeIdentityConsistent(3, 4))
        assertTrue(keyMintRuntimeIdentityConsistent(4, 41))
        assertFalse(keyMintRuntimeIdentityConsistent(2, 41))
        assertFalse(keyMintRuntimeIdentityConsistent(4, 4))
    }

    @Test
    fun `missing projection does not invent a runtime conflict`() {
        assertTrue(keyMintRuntimeIdentityConsistent(null, 300))
        assertTrue(keyMintRuntimeIdentityConsistent(300, null))
    }
}
