package com.kaizehuang.vernier.features.tee.data.verification.keystore

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SyntheticGrantGetKeyEntryAccessVectorBlindnessProbeTest {

    @Test
    fun `successful grantee readback without get info is an access vector anomaly`() {
        val result = SyntheticGrantGetKeyEntryAccessVectorBlindnessProbe.evaluateGranteeReadback(
            granteeUid = 99001,
            accessVector = Keystore2PrivateGrantClient.KEY_PERMISSION_USE_FALLBACK,
            granteeRead = TeeGrantDomainGranteeChainResult(
                available = true,
                chain = GrantDomainCertificateChain(
                    certificates = listOf(
                        GrantDomainCertificateFingerprint(derLength = 3, sha256 = "abc"),
                    ),
                ),
            ),
        )

        assertTrue(result.executed)
        assertTrue(result.available)
        assertTrue(result.granteeReadSucceeded)
        assertEquals(
            SyntheticGrantGetKeyEntryAccessVectorBlindnessAnomalyKind.GET_KEY_ENTRY_WITHOUT_GET_INFO_ALLOWED,
            result.anomalyKind,
        )
    }

    @Test
    fun `permission denied grantee readback is the clean contract result`() {
        val result = SyntheticGrantGetKeyEntryAccessVectorBlindnessProbe.evaluateGranteeReadback(
            granteeUid = 99001,
            accessVector = Keystore2PrivateGrantClient.KEY_PERMISSION_USE_FALLBACK,
            granteeRead = TeeGrantDomainGranteeChainResult(
                available = false,
                errorKind = Keystore2PrivateGrantErrorKind.PERMISSION_DENIED,
                detail = "isolated binder call blocked: PERMISSION_DENIED",
            ),
        )

        assertTrue(result.executed)
        assertTrue(result.available)
        assertFalse(result.granteeReadSucceeded)
        assertEquals(SyntheticGrantGetKeyEntryAccessVectorBlindnessAnomalyKind.NONE, result.anomalyKind)
    }

    @Test
    fun `key not found grantee readback stays unavailable rather than reporting clean`() {
        val result = SyntheticGrantGetKeyEntryAccessVectorBlindnessProbe.evaluateGranteeReadback(
            granteeUid = 99001,
            accessVector = Keystore2PrivateGrantClient.KEY_PERMISSION_USE_FALLBACK,
            granteeRead = TeeGrantDomainGranteeChainResult(
                available = false,
                errorKind = Keystore2PrivateGrantErrorKind.KEY_NOT_FOUND,
                detail = "isolated binder call blocked: KEY_NOT_FOUND",
            ),
        )

        assertTrue(result.executed)
        assertFalse(result.available)
        assertEquals(SyntheticGrantGetKeyEntryAccessVectorBlindnessAnomalyKind.UNAVAILABLE, result.anomalyKind)
        assertEquals(Keystore2PrivateGrantErrorKind.KEY_NOT_FOUND, result.granteeReadErrorKind)
    }

    @Test
    fun `existing grant danger produces an explicit skipped result`() {
        val result = SyntheticGrantGetKeyEntryAccessVectorBlindnessProbe.skippedAfterExistingGrantDanger()

        assertFalse(result.executed)
        assertEquals(
            SyntheticGrantGetKeyEntryAccessVectorBlindnessAnomalyKind.SKIPPED_AFTER_EXISTING_GRANT_DANGER,
            result.anomalyKind,
        )
    }
}
