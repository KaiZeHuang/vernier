package com.kaizehuang.vernier.features.tee.data.report

import com.kaizehuang.vernier.features.tee.domain.TeeReport
import com.kaizehuang.vernier.features.tee.domain.TeeSignalLevel
import com.kaizehuang.vernier.features.tee.domain.TeeSoterAnomaly
import com.kaizehuang.vernier.features.tee.domain.TeeSoterAnomalyKind
import com.kaizehuang.vernier.features.tee.domain.TeeSoterState
import com.kaizehuang.vernier.features.tee.domain.TeeVerdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TeeReportReducerSoterTest {

    private val reducer = TeeReportReducer()

    @Test
    fun `fabricated soter signature is a local failure without changing the attestation verdict`() {
        val report = reducer.reduce(
            baseArtifacts(
                soter = availableSoter(
                    TeeSoterAnomaly(TeeSoterAnomalyKind.D_SOTER_ZERO_SIGNATURE, TeeSignalLevel.FAIL, "all-zero signature in ASK"),
                    TeeSoterAnomaly(TeeSoterAnomalyKind.D_SOTER_ZERO_CPU_ID, TeeSignalLevel.WARN, "all-zero cpu_id in ASK"),
                ),
            ),
        )

        assertEquals(TeeVerdict.CONSISTENT, report.verdict)
        assertEquals(1, report.supplementaryIndicatorCount)
        assertEquals(TeeSignalLevel.FAIL, report.supplementaryReviewLevel)
        assertEquals(
            "Soter replies need review: all-zero signature in ASK; all-zero cpu_id in ASK. " +
                "Attestation and trust-path checks still aligned.",
            report.summary,
        )
        assertEquals(TeeSignalLevel.FAIL, soterRowLevel(report))
    }

    @Test
    fun `relay identity alone stays a local warning`() {
        val report = reducer.reduce(
            baseArtifacts(
                soter = availableSoter(
                    TeeSoterAnomaly(TeeSoterAnomalyKind.KNOWN_RELAY_CPU_ID, TeeSignalLevel.WARN, "relay cpu_id in ASK"),
                ),
            ),
        )

        assertEquals(TeeVerdict.CONSISTENT, report.verdict)
        assertEquals(TeeSignalLevel.WARN, report.supplementaryReviewLevel)
        assertEquals(TeeSignalLevel.WARN, soterRowLevel(report))
        assertTrue(report.signals.any { it.label == "Signals" && it.value.contains("1 local") })
    }

    @Test
    fun `available soter without anomalies passes without a local indicator`() {
        val report = reducer.reduce(baseArtifacts(soter = availableSoter()))

        assertEquals(0, report.supplementaryIndicatorCount)
        assertEquals(TeeSignalLevel.PASS, soterRowLevel(report))
    }

    private fun availableSoter(vararg anomalies: TeeSoterAnomaly) = TeeSoterState(
        serviceReachable = true,
        keyPrepared = true,
        signSessionAvailable = true,
        available = true,
        anomalies = anomalies.toList(),
        summary = "Soter ASK/AuthKey/initSigh calls succeeded.",
    )

    private fun soterRowLevel(report: TeeReport): TeeSignalLevel =
        report.sections.single { it.title == "Checks" }.items.single { it.title == "Soter" }.level
}
