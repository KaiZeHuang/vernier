package com.kaizehuang.vernier.capability.selinuxpolicy.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SelinuxContextValidityCarrierManagerTest {

    @Test
    fun `missing context reports carrier failure across related probes`() = runBlocking {
        val snapshot = SelinuxContextValidityCarrierManager(context = null).collectSnapshot(scanStartedAt = 0L)

        assertEquals("SELinux carrier service unavailable.", snapshot.failureReason)
        assertEquals("SELinux carrier service unavailable.", snapshot.procAttrCurrentFailureReason)
        assertEquals("SELinux carrier service unavailable.", snapshot.dirtyPolicyFailureReason)
        assertEquals("SELinux carrier service unavailable.", snapshot.policyloadSeqnoFailureReason)
        assertEquals(SelinuxPolicyloadSeqnoState.UNAVAILABLE.name, snapshot.policyloadSeqnoState)
    }
}
