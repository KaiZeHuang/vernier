package com.kaizehuang.vernier.features.tee.data.report

import com.kaizehuang.vernier.features.tee.data.repository.DeferredChecks
import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyLifecycleResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyPairConsistencyResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.PureCertificateResult
import com.kaizehuang.vernier.features.tee.domain.TeeSignalLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TeeReportReducerDeepCheckTest {

    private val reducer = TeeReportReducer()

    @Test
    fun `skipped deep checks read as skipped rather than passed`() {
        val base = baseArtifacts()
        val skipped = DeferredChecks.skipped(base.snapshot, base.timingSideChannel)
        val report = reducer.reduce(
            base.copy(
                pairConsistency = skipped.pairConsistency,
                aesGcm = skipped.aesGcm,
                lifecycle = skipped.lifecycle,
                oversizedChallenge = skipped.oversizedChallenge,
                pureCertificate = skipped.pureCertificate,
                updateSubcomponent = skipped.updateSubcomponent,
                dualAlgorithm = skipped.dualAlgorithm,
            ),
        )

        val checks = report.sections.single { it.title == "Checks" }.items
        listOf(
            "Key pair",
            "AES-GCM",
            "Lifecycle",
            "Oversized challenge",
            "Pure cert",
            "Update path",
            "Dual algorithm",
        ).forEach { title ->
            val row = checks.single { it.title == title }
            assertTrue("$title: ${row.body}", row.body.startsWith("Skipped"))
            assertEquals(title, TeeSignalLevel.INFO, row.level)
        }
        assertEquals(0, report.supplementaryIndicatorCount)
    }

    @Test
    fun `deep checks that threw are not reported as contradictions`() {
        val report = reducer.reduce(
            baseArtifacts().copy(
                pairConsistency = KeyPairConsistencyResult(
                    executed = false,
                    keyMatchesCertificate = false,
                    probeError = "Keystore backend busy",
                    detail = "Keystore backend busy",
                ),
                lifecycle = KeyLifecycleResult(
                    executed = false,
                    created = false,
                    deleteRemovedAlias = false,
                    regeneratedFreshMaterial = false,
                    probeError = "Keystore backend busy",
                    detail = "Keystore backend busy",
                ),
                pureCertificate = PureCertificateResult.notCompleted("Keystore backend busy"),
            ),
        )

        val checks = report.sections.single { it.title == "Checks" }.items
        listOf("Key pair", "Lifecycle", "Pure cert").forEach { title ->
            val row = checks.single { it.title == title }
            assertEquals(title, "Did not complete • Keystore backend busy", row.body)
            assertEquals(title, TeeSignalLevel.INFO, row.level)
        }
        assertEquals(0, report.supplementaryIndicatorCount)
    }

    @Test
    fun `observed key mismatch still fails`() {
        val report = reducer.reduce(
            baseArtifacts().copy(
                pairConsistency = KeyPairConsistencyResult(
                    keyMatchesCertificate = false,
                    detail = "Leaf certificate public key failed to verify locally signed data.",
                ),
            ),
        )

        val row = report.sections.single { it.title == "Checks" }.items.single { it.title == "Key pair" }
        assertEquals(TeeSignalLevel.FAIL, row.level)
        assertEquals(1, report.supplementaryIndicatorCount)
    }
}
