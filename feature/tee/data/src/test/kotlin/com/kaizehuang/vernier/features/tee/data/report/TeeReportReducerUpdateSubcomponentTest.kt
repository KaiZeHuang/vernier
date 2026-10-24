package com.kaizehuang.vernier.features.tee.data.report

import com.kaizehuang.vernier.features.tee.data.verification.keystore.UpdateSubcomponentStaleResponseAnomalyKind
import com.kaizehuang.vernier.features.tee.data.verification.keystore.UpdateSubcomponentStaleResponsePersistenceResult
import com.kaizehuang.vernier.features.tee.domain.TeeSignalLevel
import com.kaizehuang.vernier.features.tee.domain.TeeSoterState
import com.kaizehuang.vernier.features.tee.domain.TeeVerdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TeeReportReducerUpdateSubcomponentTest {

    private val reducer = TeeReportReducer()

    @Test
    fun `updateSubcomponent stale response persistence becomes supplementary failure`() {
        val report = reducer.reduce(
            baseArtifacts(
                updateSubcomponentStaleResponsePersistence =
                    UpdateSubcomponentStaleResponsePersistenceResult(
                        executed = true,
                        available = true,
                        supportGateClean = true,
                        updateSucceeded = true,
                        staleNarrativeDetected = true,
                        priorChainLength = 3,
                        postChainLength = 2,
                        retainedCertificateCount = 1,
                        postLeafMatchesMarker = false,
                        anomalyKind =
                            UpdateSubcomponentStaleResponseAnomalyKind.STALE_TEE_RESPONSE_AFTER_KEY_ID_UPDATE,
                        retainedFingerprint = "abc123def456",
                        detail = "kind=STALE_TEE_RESPONSE_AFTER_KEY_ID_UPDATE, retained=1",
                    ),
            ),
        )

        assertEquals(TeeVerdict.CONSISTENT, report.verdict)
        assertEquals(1, report.supplementaryIndicatorCount)
        assertEquals(TeeSignalLevel.FAIL, report.supplementaryReviewLevel)
        assertTrue(report.summary.contains("UpdateSubcomponent stale TEE response", ignoreCase = true))
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "Update persistence" &&
                it.level == TeeSignalLevel.FAIL &&
                it.body.contains("Matched", ignoreCase = true) &&
                it.body.contains("kind=STALE_TEE_RESPONSE_AFTER_KEY_ID_UPDATE") &&
                it.body.contains("retained=1")
        })
    }

    @Test
    fun `later supplementary failure outranks earlier soter warning`() {
        val report = reducer.reduce(
            baseArtifacts(
                soter = TeeSoterState(
                    serviceReachable = false,
                    keyPrepared = false,
                    signSessionAvailable = false,
                    available = false,
                    damaged = false,
                    abnormalEnvironment = true,
                    summary = "Abnormal Soter environment: Simplified Chinese locale on a likely Soter-supporting device, but PackageManager could not resolve com.tencent.soter.soterserver.",
                ),
                updateSubcomponentStaleResponsePersistence =
                    UpdateSubcomponentStaleResponsePersistenceResult(
                        executed = true,
                        available = true,
                        supportGateClean = true,
                        updateSucceeded = true,
                        staleNarrativeDetected = true,
                        priorChainLength = 3,
                        postChainLength = 2,
                        retainedCertificateCount = 1,
                        postLeafMatchesMarker = false,
                        anomalyKind =
                            UpdateSubcomponentStaleResponseAnomalyKind.STALE_TEE_RESPONSE_AFTER_KEY_ID_UPDATE,
                        retainedFingerprint = "abc123def456",
                        detail = "kind=STALE_TEE_RESPONSE_AFTER_KEY_ID_UPDATE, retained=1",
                    ),
            ),
        )

        assertEquals(TeeVerdict.CONSISTENT, report.verdict)
        assertEquals(2, report.supplementaryIndicatorCount)
        assertEquals(TeeSignalLevel.FAIL, report.supplementaryReviewLevel)
        assertTrue(report.signals.any {
            it.label == "Signals" && it.level == TeeSignalLevel.FAIL
        })
        assertTrue(report.summary.contains("UpdateSubcomponent stale TEE response", ignoreCase = true))
        assertFalse(report.summary.contains("abnormal soter environment", ignoreCase = true))
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "Soter" && it.level == TeeSignalLevel.WARN
        })
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "Update persistence" && it.level == TeeSignalLevel.FAIL
        })
    }

    @Test
    fun `updateSubcomponent stale response clean state stays pass`() {
        val report = reducer.reduce(
            baseArtifacts(
                updateSubcomponentStaleResponsePersistence =
                    UpdateSubcomponentStaleResponsePersistenceResult(
                        executed = true,
                        available = true,
                        supportGateClean = true,
                        updateSucceeded = true,
                        staleNarrativeDetected = false,
                        priorChainLength = 3,
                        postChainLength = 1,
                        postLeafMatchesMarker = true,
                        anomalyKind = UpdateSubcomponentStaleResponseAnomalyKind.NONE,
                        detail = "kind=NONE, marker leaf returned.",
                    ),
            ),
        )

        assertEquals(0, report.supplementaryIndicatorCount)
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "Update persistence" &&
                it.level == TeeSignalLevel.PASS &&
                it.body.contains("Clean", ignoreCase = true) &&
                it.body.contains("kind=NONE")
        })
    }

    @Test
    fun `updateSubcomponent stale response unavailable state stays informational`() {
        val report = reducer.reduce(
            baseArtifacts(
                updateSubcomponentStaleResponsePersistence =
                    UpdateSubcomponentStaleResponsePersistenceResult(
                        executed = false,
                        supportGateClean = false,
                        anomalyKind = UpdateSubcomponentStaleResponseAnomalyKind.UPDATE_SUBCOMPONENT_UNOBSERVABLE,
                        detail = "UpdateSubcomponent support gate failed.",
                    ),
            ),
        )

        assertEquals(0, report.supplementaryIndicatorCount)
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "Update persistence" &&
                it.level == TeeSignalLevel.INFO &&
                it.body.contains("Unavailable", ignoreCase = true) &&
                it.body.contains("kind=UPDATE_SUBCOMPONENT_UNOBSERVABLE")
        })
    }
}
