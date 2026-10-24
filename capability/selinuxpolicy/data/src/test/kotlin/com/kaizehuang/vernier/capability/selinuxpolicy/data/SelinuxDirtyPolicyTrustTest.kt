package com.kaizehuang.vernier.capability.selinuxpolicy.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelinuxDirtyPolicyTrustTest {

    private val trusted = SelinuxContextValiditySnapshot(
        dirtyPolicyAvailable = true,
        dirtyPolicyProbeAttempted = true,
        dirtyPolicyCarrierMatchesExpected = true,
        dirtyPolicyStable = true,
        dirtyPolicyControlsPassed = true,
        dirtyPolicyAccessControlAllowed = true,
        dirtyPolicyNegativeControlRejected = true,
        javaDirtyPolicyAvailable = true,
        javaDirtyPolicyProbeAttempted = true,
        javaDirtyPolicyCarrierMatchesExpected = true,
        javaDirtyPolicyStable = true,
        javaDirtyPolicyControlsPassed = true,
        javaDirtyPolicyAccessControlAllowed = true,
        javaDirtyPolicyNegativeControlRejected = true,
    )

    @Test
    fun `an oracle that passed every check is trusted`() {
        assertTrue(trusted.dirtyPolicyTrusted)
        assertTrue(trusted.javaDirtyPolicyTrusted)
    }

    @Test
    fun `any failed check withdraws trust from that oracle only`() {
        listOf(
            trusted.copy(dirtyPolicyCarrierMatchesExpected = false),
            trusted.copy(dirtyPolicyStable = false),
            trusted.copy(dirtyPolicyControlsPassed = false),
            trusted.copy(dirtyPolicyAccessControlAllowed = null),
            trusted.copy(dirtyPolicyNegativeControlRejected = false),
        ).forEach { snapshot ->
            assertFalse(snapshot.toString(), snapshot.dirtyPolicyTrusted)
            assertTrue(snapshot.javaDirtyPolicyTrusted)
        }
        assertFalse(trusted.copy(javaDirtyPolicyNegativeControlRejected = null).javaDirtyPolicyTrusted)
    }
}
