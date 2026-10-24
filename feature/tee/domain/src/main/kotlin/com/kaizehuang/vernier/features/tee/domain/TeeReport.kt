package com.kaizehuang.vernier.features.tee.domain

import com.kaizehuang.vernier.capability.attestation.domain.TeeCertificateItem
import com.kaizehuang.vernier.capability.attestation.domain.TeeTier
import com.kaizehuang.vernier.capability.attestation.domain.TeeTrustRoot

data class TeeReport(
    val stage: TeeScanStage,
    val verdict: TeeVerdict,
    val tier: TeeTier,
    val headline: String,
    val summary: String,
    val collapsedSummary: String,
    val trustRoot: TeeTrustRoot,
    val localTrustChainLevel: TeeSignalLevel = TeeSignalLevel.INFO,
    val trustSummary: String,
    val tamperScore: Int,
    val evidenceCount: Int,
    val supplementaryIndicatorCount: Int = 0,
    val supplementaryReviewLevel: TeeSignalLevel = TeeSignalLevel.INFO,
    /** False when the native process-side probes did not run, so their silence says nothing. */
    val nativeProbesAvailable: Boolean = true,
    val signals: List<TeeSignal>,
    val sections: List<TeeEvidenceSection>,
    val certificates: List<TeeCertificateItem>,
    val rkpState: TeeRkpState = TeeRkpState(),
    val patchState: TeePatchState = TeePatchState(),
    val soterState: TeeSoterState = TeeSoterState(),
    val networkState: TeeNetworkState = TeeNetworkState(),
    val exportText: String = "",
    val failureMessage: String? = null,
    /** The grant evidence of the item [summary] quotes, when it quotes a grant probe's item. */
    val summaryGrant: TeeGrantEvidence? = null,
) {
    companion object {
        fun loading(): TeeReport = TeeReport(
            stage = TeeScanStage.LOADING,
            verdict = TeeVerdict.LOADING,
            tier = TeeTier.UNKNOWN,
            headline = "TEE",
            summary = "Collecting local attestation and keystore evidence.",
            collapsedSummary = "Scanning",
            trustRoot = TeeTrustRoot.UNKNOWN,
            trustSummary = "Local trust path pending",
            tamperScore = 0,
            evidenceCount = 0,
            signals = emptyList(),
            sections = emptyList(),
            certificates = emptyList(),
        )

        fun failed(message: String): TeeReport = TeeReport(
            stage = TeeScanStage.FAILED,
            verdict = TeeVerdict.INCONCLUSIVE,
            tier = TeeTier.UNKNOWN,
            headline = "Local TEE verification failed",
            summary = message,
            collapsedSummary = "Scan failed",
            trustRoot = TeeTrustRoot.UNKNOWN,
            trustSummary = "Local trust path unavailable",
            tamperScore = 0,
            evidenceCount = 0,
            signals = emptyList(),
            sections = listOf(
                TeeEvidenceSection(
                    kind = TeeEvidenceSectionKind.CHECKS,
                    items = listOf(
                        TeeEvidenceItem(
                            title = "Collection failure",
                            body = message,
                            level = TeeSignalLevel.FAIL,
                        ),
                    ),
                ),
            ),
            certificates = emptyList(),
            failureMessage = message,
        )
    }
}
