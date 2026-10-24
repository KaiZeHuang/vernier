package com.kaizehuang.vernier.features.tee.presentation

import com.kaizehuang.vernier.capability.attestation.domain.TeeTier
import com.kaizehuang.vernier.capability.attestation.domain.TeeTrustRoot
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.features.tee.domain.TeeEvidenceItem
import com.kaizehuang.vernier.features.tee.domain.TeeEvidenceSection
import com.kaizehuang.vernier.features.tee.domain.TeeEvidenceSectionKind
import com.kaizehuang.vernier.features.tee.domain.TeeReport
import com.kaizehuang.vernier.features.tee.domain.TeeScanStage
import com.kaizehuang.vernier.features.tee.domain.TeeSignal
import com.kaizehuang.vernier.features.tee.domain.TeeSignalLevel
import com.kaizehuang.vernier.features.tee.domain.TeeVerdict
import org.junit.Assert.assertEquals
import org.junit.Test

class TeeCardModelMapperSupplementaryTest {

    private val mapper = TeeCardModelMapper()

    @Test
    fun `matched update persistence stale narrative escalates aligned tee card to danger`() {
        val model = mapper.map(
            report = TeeReport(
                stage = TeeScanStage.READY,
                verdict = TeeVerdict.CONSISTENT,
                tier = TeeTier.TEE,
                headline = "Attestation aligned; local probes need review",
                summary = "UpdateSubcomponent stale TEE response persistence detected. Attestation and trust-path checks still aligned.",
                collapsedSummary = "Aligned • local review",
                trustRoot = TeeTrustRoot.GOOGLE,
                trustSummary = "Local trust path",
                tamperScore = 10,
                evidenceCount = 1,
                supplementaryIndicatorCount = 1,
                supplementaryReviewLevel = TeeSignalLevel.FAIL,
                signals = listOf(
                    TeeSignal(
                        "Update persistence",
                        "Matched",
                        TeeSignalLevel.FAIL,
                    ),
                ),
                sections = listOf(
                    TeeEvidenceSection(
                        kind = TeeEvidenceSectionKind.CHECKS,
                        items = listOf(
                            TeeEvidenceItem(
                                "Update persistence",
                                "Matched kind=STALE_TEE_RESPONSE_AFTER_KEY_ID_UPDATE retained=1 prior=3 post=2",
                                TeeSignalLevel.FAIL,
                            ),
                        ),
                    ),
                ),
                certificates = emptyList(),
            ),
            isExpanded = false,
        )

        assertEquals(DetectorStatus.danger(), model.status)
    }

    @Test
    fun `structured supplementary failure drives danger even when warning row appears first`() {
        val model = mapper.map(
            report = TeeReport(
                stage = TeeScanStage.READY,
                verdict = TeeVerdict.CONSISTENT,
                tier = TeeTier.TEE,
                headline = "Attestation aligned; local probes need review",
                summary = "UpdateSubcomponent stale TEE response persistence detected. Attestation and trust-path checks still aligned.",
                collapsedSummary = "Aligned • local review",
                trustRoot = TeeTrustRoot.GOOGLE,
                trustSummary = "Local trust path",
                tamperScore = 10,
                evidenceCount = 2,
                supplementaryIndicatorCount = 2,
                supplementaryReviewLevel = TeeSignalLevel.FAIL,
                signals = listOf(
                    TeeSignal(
                        "Signals",
                        "0 policy hard • 0 policy review • 2 local",
                        TeeSignalLevel.FAIL,
                    ),
                ),
                sections = listOf(
                    TeeEvidenceSection(
                        kind = TeeEvidenceSectionKind.CHECKS,
                        items = listOf(
                            TeeEvidenceItem(
                                "Soter",
                                "Review abnormal Soter environment.",
                                TeeSignalLevel.WARN,
                            ),
                            TeeEvidenceItem(
                                "Update persistence",
                                "Matched kind=STALE_TEE_RESPONSE_AFTER_KEY_ID_UPDATE retained=1",
                                TeeSignalLevel.FAIL,
                            ),
                        ),
                    ),
                ),
                certificates = emptyList(),
            ),
            isExpanded = false,
        )

        assertEquals(DetectorStatus.danger(), model.status)
    }

    @Test
    fun `structured supplementary failure drives danger even under suspicious verdict`() {
        val model = mapper.map(
            report = TeeReport(
                stage = TeeScanStage.READY,
                verdict = TeeVerdict.SUSPICIOUS,
                tier = TeeTier.TEE,
                headline = "Policy-backed attestation evidence needs review",
                summary = "Provisioning info was not adjacent to the trusted attestation certificate.",
                collapsedSummary = "1 policy review",
                trustRoot = TeeTrustRoot.GOOGLE,
                trustSummary = "Google root, chain needs review",
                tamperScore = 18,
                evidenceCount = 2,
                supplementaryIndicatorCount = 1,
                supplementaryReviewLevel = TeeSignalLevel.FAIL,
                signals = listOf(
                    TeeSignal(
                        "Signals",
                        "0 policy hard • 1 policy review • 1 local",
                        TeeSignalLevel.FAIL,
                    ),
                ),
                sections = listOf(
                    TeeEvidenceSection(
                        kind = TeeEvidenceSectionKind.TRUST,
                        items = listOf(
                            TeeEvidenceItem(
                                "Chain layout",
                                "Provisioning info was not adjacent to the trusted attestation certificate.",
                                TeeSignalLevel.WARN,
                            ),
                        ),
                    ),
                    TeeEvidenceSection(
                        kind = TeeEvidenceSectionKind.CHECKS,
                        items = listOf(
                            TeeEvidenceItem(
                                "Update persistence",
                                "Matched kind=STALE_TEE_RESPONSE_AFTER_KEY_ID_UPDATE retained=1",
                                TeeSignalLevel.FAIL,
                            ),
                        ),
                    ),
                ),
                certificates = emptyList(),
            ),
            isExpanded = false,
        )

        assertEquals(DetectorStatus.danger(), model.status)
    }
}
