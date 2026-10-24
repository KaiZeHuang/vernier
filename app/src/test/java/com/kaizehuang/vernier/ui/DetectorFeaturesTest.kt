package com.kaizehuang.vernier.ui

import com.kaizehuang.vernier.sdk.DetectorCatalog
import org.junit.Assert.assertEquals
import org.junit.Test

class DetectorFeaturesTest {

    @Test
    fun `every catalogued detector has exactly one card, started in catalog order`() {
        assertEquals(DetectorCatalog.all.map { it.id }, DetectorFeatures.all.map { it.id })
    }

    @Test
    fun `every declared consent has one card, under the detector that declares it`() {
        assertEquals(
            DetectorCatalog.all.flatMap { detector -> detector.consents.map { detector.id to it.id } },
            DetectorFeatures.consentCards.map { it.detectorId to it.consent.id },
        )
    }
}
