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

class TeeCardModelMapperSignatureTest {

    private val mapper = TeeCardModelMapper()

    @Test
    fun `tricky store timing skip signature escalates aligned tee card to danger`() {
        val model = mapper.map(
            report = TeeReport(
                stage = TeeScanStage.READY,
                verdict = TeeVerdict.CONSISTENT,
                tier = TeeTier.TEE,
                headline = "Attestation aligned; local probes need review",
                summary = "Keystore errors during timing skip matched a known keystore-interception module. Attestation and trust-path checks still aligned.",
                collapsedSummary = "Aligned • local review",
                trustRoot = TeeTrustRoot.GOOGLE,
                trustSummary = "Local trust path",
                tamperScore = 10,
                evidenceCount = 1,
                supplementaryIndicatorCount = 1,
                supplementaryReviewLevel = TeeSignalLevel.FAIL,
                signals = listOf(
                    TeeSignal(
                        "Signals",
                        "0 policy hard • 0 policy review • 1 local",
                        TeeSignalLevel.WARN,
                    ),
                ),
                sections = listOf(
                    TeeEvidenceSection(
                        kind = TeeEvidenceSectionKind.CHECKS,
                        items = listOf(
                            TeeEvidenceItem(
                                "Timing side-channel",
                                "Matched a known keystore-interception module • Register timer • bound_cpu0",
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
    fun `tee simulator timing skip signature escalates aligned tee card to danger`() {
        val model = mapper.map(
            report = TeeReport(
                stage = TeeScanStage.READY,
                verdict = TeeVerdict.CONSISTENT,
                tier = TeeTier.TEE,
                headline = "Attestation aligned; local probes need review",
                summary = "Keystore errors during timing skip matched a known keystore-interception module. Attestation and trust-path checks still aligned.",
                collapsedSummary = "Aligned • local review",
                trustRoot = TeeTrustRoot.GOOGLE,
                trustSummary = "Local trust path",
                tamperScore = 10,
                evidenceCount = 1,
                supplementaryIndicatorCount = 1,
                supplementaryReviewLevel = TeeSignalLevel.FAIL,
                signals = listOf(
                    TeeSignal(
                        "Signals",
                        "0 policy hard • 0 policy review • 1 local",
                        TeeSignalLevel.WARN,
                    ),
                ),
                sections = listOf(
                    TeeEvidenceSection(
                        kind = TeeEvidenceSectionKind.CHECKS,
                        items = listOf(
                            TeeEvidenceItem(
                                "Timing side-channel",
                                "Matched a known keystore-interception module • Fallback timer • not_requested",
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
    fun `matched tee simulator generate mode fingerprint escalates aligned tee card to danger`() {
        val model = mapper.map(
            report = TeeReport(
                stage = TeeScanStage.READY,
                verdict = TeeVerdict.CONSISTENT,
                tier = TeeTier.TEE,
                headline = "Attestation aligned; local probes need review",
                summary = "Matched TEE Simulator generate-mode fingerprint. Attestation and trust-path checks still aligned.",
                collapsedSummary = "Aligned • local review",
                trustRoot = TeeTrustRoot.GOOGLE,
                trustSummary = "Local trust path",
                tamperScore = 10,
                evidenceCount = 1,
                supplementaryIndicatorCount = 1,
                supplementaryReviewLevel = TeeSignalLevel.FAIL,
                signals = listOf(
                    TeeSignal(
                        "TEE Simulator generate-mode fingerprint",
                        "Matched",
                        TeeSignalLevel.FAIL,
                    ),
                ),
                sections = listOf(
                    TeeEvidenceSection(
                        kind = TeeEvidenceSectionKind.CHECKS,
                        items = listOf(
                            TeeEvidenceItem(
                                "TEE Simulator generate-mode fingerprint",
                                "Matched TEE Simulator generate-mode fingerprint.",
                                TeeSignalLevel.FAIL,
                                hiddenCopyText = "reply raw hex dump",
                            ),
                        ),
                    ),
                ),
                certificates = emptyList(),
            ),
            isExpanded = false,
        )

        assertEquals(DetectorStatus.danger(), model.status)
        assertEquals(
            "reply raw hex dump",
            model.factGroups.single().rows.single().hiddenCopyText,
        )
    }

    @Test
    fun `matched importKey retained narrative escalates aligned tee card to danger`() {
        val model = mapper.map(
            report = TeeReport(
                stage = TeeScanStage.READY,
                verdict = TeeVerdict.CONSISTENT,
                tier = TeeTier.TEE,
                headline = "Attestation aligned; local probes need review",
                summary = "ImportKey retained attestation narrative detected. Attestation and trust-path checks still aligned.",
                collapsedSummary = "Aligned • local review",
                trustRoot = TeeTrustRoot.GOOGLE,
                trustSummary = "Local trust path",
                tamperScore = 10,
                evidenceCount = 1,
                supplementaryIndicatorCount = 1,
                supplementaryReviewLevel = TeeSignalLevel.FAIL,
                signals = listOf(
                    TeeSignal(
                        "ImportKey narrative",
                        "Matched",
                        TeeSignalLevel.FAIL,
                    ),
                ),
                sections = listOf(
                    TeeEvidenceSection(
                        kind = TeeEvidenceSectionKind.CHECKS,
                        items = listOf(
                            TeeEvidenceItem(
                                "ImportKey narrative",
                                "Matched • kind=STALE_GENERATED_AFTER_IMPORT, origin=GENERATED, retained=3",
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
