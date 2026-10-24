package com.kaizehuang.vernier.features.tee.data.verification.keystore

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SyntheticGrantGranteeBlindReadbackProbeTest {

    @Test
    fun `successful owner replay is a caller binding anomaly`() {
        val result = SyntheticGrantGranteeBlindReadbackProbe.evaluateOwnerReplay(
            granteeUid = 99001,
            ownerReplay = Keystore2PrivateGrantResult(
                available = true,
                phase = Keystore2PrivateGrantPhase.PRIVATE_OWNER_REPLAY_GRANT,
            ),
        )

        assertTrue(result.executed)
        assertTrue(result.available)
        assertTrue(result.ownerReplaySucceeded)
        assertEquals(
            SyntheticGrantGranteeBlindReadbackAnomalyKind.NON_GRANTEE_READBACK_ALLOWED,
            result.anomalyKind,
        )
    }

    @Test
    fun `key not found owner replay is the clean contract result`() {
        val result = SyntheticGrantGranteeBlindReadbackProbe.evaluateOwnerReplay(
            granteeUid = 99001,
            ownerReplay = Keystore2PrivateGrantResult.unavailable(
                phase = Keystore2PrivateGrantPhase.PRIVATE_OWNER_REPLAY_GRANT,
                errorKind = Keystore2PrivateGrantErrorKind.KEY_NOT_FOUND,
                detail = "private getKeyEntry(GRANT) failed: KEY_NOT_FOUND",
            ),
        )

        assertTrue(result.executed)
        assertTrue(result.available)
        assertFalse(result.ownerReplaySucceeded)
        assertEquals(SyntheticGrantGranteeBlindReadbackAnomalyKind.NONE, result.anomalyKind)
    }

    @Test
    fun `permission denied owner replay stays unavailable rather than reporting danger`() {
        val result = SyntheticGrantGranteeBlindReadbackProbe.evaluateOwnerReplay(
            granteeUid = 99001,
            ownerReplay = Keystore2PrivateGrantResult.unavailable(
                phase = Keystore2PrivateGrantPhase.PRIVATE_OWNER_REPLAY_GRANT,
                errorKind = Keystore2PrivateGrantErrorKind.PERMISSION_DENIED,
                detail = "private getKeyEntry(GRANT) failed: PERMISSION_DENIED",
            ),
        )

        assertTrue(result.executed)
        assertFalse(result.available)
        assertEquals(SyntheticGrantGranteeBlindReadbackAnomalyKind.UNAVAILABLE, result.anomalyKind)
        assertEquals(Keystore2PrivateGrantErrorKind.PERMISSION_DENIED, result.ownerReplayErrorKind)
    }

    @Test
    fun `existing grant danger produces an explicit skipped result`() {
        val result = SyntheticGrantGranteeBlindReadbackProbe.skippedAfterExistingGrantDanger()

        assertFalse(result.executed)
        assertEquals(
            SyntheticGrantGranteeBlindReadbackAnomalyKind.SKIPPED_AFTER_EXISTING_GRANT_DANGER,
            result.anomalyKind,
        )
    }
}
