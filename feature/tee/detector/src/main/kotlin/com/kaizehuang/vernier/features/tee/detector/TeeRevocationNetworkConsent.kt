package com.kaizehuang.vernier.features.tee.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.ConsentDecision
import com.kaizehuang.vernier.core.detector.ConsentId
import com.kaizehuang.vernier.core.detector.DetectorConsent
import com.kaizehuang.vernier.features.tee.data.preferences.TeeNetworkConsentStore
import com.kaizehuang.vernier.features.tee.data.preferences.TeeNetworkPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Whether TEE's revocation check may fetch Google's attestation revocation feed. Undecided or
 * declined, the check uses only the built-in snapshot, and the TEE report says which of the two
 * applies. Every decision also drops the cached feed, as the store always has.
 */
public object TeeRevocationNetworkConsent : DetectorConsent {
    override val id: ConsentId = ConsentId("revocation_network")

    override fun decisions(context: Context): Flow<ConsentDecision> =
        TeeNetworkConsentStore.getInstance(context).prefs
            .map { prefs -> prefs.consentDecision() }
            .distinctUntilChanged()

    override suspend fun decide(context: Context, granted: Boolean) {
        TeeNetworkConsentStore.getInstance(context).setConsent(granted)
    }
}

/** The same order `CrlStatusService` reads the preferences in: first whether asked, then the answer. */
internal fun TeeNetworkPrefs.consentDecision(): ConsentDecision = when {
    !consentAsked -> ConsentDecision.UNDECIDED
    consentGranted -> ConsentDecision.GRANTED
    else -> ConsentDecision.DECLINED
}
