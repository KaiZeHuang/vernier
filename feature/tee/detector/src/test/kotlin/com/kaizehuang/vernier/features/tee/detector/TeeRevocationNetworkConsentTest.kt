package com.kaizehuang.vernier.features.tee.detector

import com.kaizehuang.vernier.core.detector.ConsentDecision
import com.kaizehuang.vernier.features.tee.data.preferences.TeeNetworkPrefs
import org.junit.Assert.assertEquals
import org.junit.Test

class TeeRevocationNetworkConsentTest {

    @Test
    fun `an unasked consent is undecided whatever the stored answer`() {
        assertEquals(ConsentDecision.UNDECIDED, prefs(asked = false, granted = false).consentDecision())
        assertEquals(ConsentDecision.UNDECIDED, prefs(asked = false, granted = true).consentDecision())
    }

    @Test
    fun `an asked consent follows the stored answer`() {
        assertEquals(ConsentDecision.GRANTED, prefs(asked = true, granted = true).consentDecision())
        assertEquals(ConsentDecision.DECLINED, prefs(asked = true, granted = false).consentDecision())
    }

    @Test
    fun `TEE declares the consent`() {
        assertEquals(listOf(TeeRevocationNetworkConsent), TeeDetector.consents)
    }

    private fun prefs(asked: Boolean, granted: Boolean) = TeeNetworkPrefs(
        consentAsked = asked,
        consentGranted = granted,
        crlCacheJson = null,
        crlFetchedAt = 0L,
    )
}
