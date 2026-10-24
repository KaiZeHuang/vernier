package com.kaizehuang.vernier.features.tee.data.report

import com.kaizehuang.vernier.features.tee.data.verification.keystore.ImportKeyRetainedAttestationAnomalyKind
import com.kaizehuang.vernier.features.tee.data.verification.keystore.ImportKeyRetainedAttestationNarrativeResult
import com.kaizehuang.vernier.features.tee.domain.TeeSignalLevel
import com.kaizehuang.vernier.features.tee.domain.TeeVerdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TeeReportReducerImportKeyTest {

    private val reducer = TeeReportReducer()

    @Test
    fun `importKey retained narrative becomes supplementary review without changing attestation verdict`() {
        val report = reducer.reduce(
            baseArtifacts(
                importKeyRetainedAttestationNarrative = ImportKeyRetainedAttestationNarrativeResult(
                    executed = true,
                    importSupported = true,
                    markerImportBaselineClean = true,
                    originImported = true,
                    retainedNarrativeDetected = true,
                    priorChainLength = 3,
                    postImportChainLength = 2,
                    retainedCertificateCount = 2,
                    originLabel = "IMPORTED",
                    anomalyKind = ImportKeyRetainedAttestationAnomalyKind.IMPORTED_RETAINED_PRIOR_CHAIN,
                    retainedFingerprint = "abc123def456",
                    detail = "kind=IMPORTED_RETAINED_PRIOR_CHAIN, origin=IMPORTED, retained=2",
                ),
            ),
        )

        assertEquals(TeeVerdict.CONSISTENT, report.verdict)
        assertEquals(1, report.supplementaryIndicatorCount)
        assertTrue(report.summary.contains("ImportKey retained attestation narrative", ignoreCase = true))
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "ImportKey narrative" &&
                it.body.contains("Matched", ignoreCase = true) &&
                it.level == TeeSignalLevel.FAIL
        })
    }

    @Test
    fun `stale generated importKey retained narrative becomes supplementary review`() {
        val report = reducer.reduce(
            baseArtifacts(
                importKeyRetainedAttestationNarrative = ImportKeyRetainedAttestationNarrativeResult(
                    executed = true,
                    importSupported = true,
                    markerImportBaselineClean = true,
                    originImported = false,
                    postImportLeafMatchesMarker = false,
                    retainedNarrativeDetected = true,
                    priorChainLength = 3,
                    postImportChainLength = 3,
                    retainedCertificateCount = 3,
                    originLabel = "GENERATED",
                    anomalyKind = ImportKeyRetainedAttestationAnomalyKind.STALE_GENERATED_AFTER_IMPORT,
                    retainedFingerprint = "abc123def456",
                    detail = "kind=STALE_GENERATED_AFTER_IMPORT, origin=GENERATED, retained=3",
                ),
            ),
        )

        assertEquals(TeeVerdict.CONSISTENT, report.verdict)
        assertEquals(1, report.supplementaryIndicatorCount)
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "ImportKey narrative" &&
                it.body.contains("Matched", ignoreCase = true) &&
                it.body.contains("STALE_GENERATED_AFTER_IMPORT") &&
                it.level == TeeSignalLevel.FAIL
        })
    }

    @Test
    fun `importKey retained narrative unavailable state stays informational`() {
        val report = reducer.reduce(
            baseArtifacts(
                importKeyRetainedAttestationNarrative = ImportKeyRetainedAttestationNarrativeResult(
                    executed = false,
                    detail = "Keystore2 getKeyEntry metadata unavailable.",
                ),
            ),
        )

        assertEquals(0, report.supplementaryIndicatorCount)
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "ImportKey narrative" &&
                it.body.contains("Unavailable", ignoreCase = true) &&
                it.level == TeeSignalLevel.INFO
        })
    }

    @Test
    fun `importKey unsupported state stays informational`() {
        val report = reducer.reduce(
            baseArtifacts(
                importKeyRetainedAttestationNarrative = ImportKeyRetainedAttestationNarrativeResult(
                    executed = false,
                    importSupported = false,
                    anomalyKind = ImportKeyRetainedAttestationAnomalyKind.IMPORT_UNSUPPORTED,
                    detail = "ImportKey support gate failed.",
                ),
            ),
        )

        assertEquals(0, report.supplementaryIndicatorCount)
        assertTrue(report.sections.single { it.title == "Checks" }.items.any {
            it.title == "ImportKey narrative" &&
                it.body.contains("Unavailable", ignoreCase = true) &&
                it.body.contains("ImportKey support gate failed") &&
                it.level == TeeSignalLevel.INFO
        })
    }
}
