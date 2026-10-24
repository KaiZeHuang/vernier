package com.kaizehuang.vernier.features.bootloader.presentation

import com.kaizehuang.vernier.capability.attestation.domain.TeeTier
import com.kaizehuang.vernier.capability.attestation.domain.TeeTrustRoot
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderEvidenceMode
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingSeverity
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderReport
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderState
import com.kaizehuang.vernier.features.bootloader.presentation.model.BootloaderCardAssessment

internal fun proofLabel(mode: BootloaderEvidenceMode): String {
    return when (mode) {
        BootloaderEvidenceMode.ATTESTATION -> "Attest"
        BootloaderEvidenceMode.PROPERTIES_ONLY -> "Props"
        BootloaderEvidenceMode.UNAVAILABLE -> "N/A"
    }
}

internal fun stateLabel(state: BootloaderState): String {
    return when (state) {
        BootloaderState.VERIFIED -> "Verified"
        BootloaderState.SELF_SIGNED -> "Custom"
        BootloaderState.UNLOCKED -> "Unlocked"
        BootloaderState.FAILED_VERIFICATION -> "Failed"
        BootloaderState.LOCKED_UNKNOWN -> "Locked?"
        BootloaderState.UNKNOWN -> "Unknown"
    }
}

internal fun tierLabel(tier: TeeTier): String {
    return when (tier) {
        TeeTier.STRONGBOX -> "StrongBox"
        TeeTier.TEE -> "TEE"
        TeeTier.SOFTWARE -> "Software"
        TeeTier.NONE -> "None"
        TeeTier.UNKNOWN -> "Unknown"
    }
}

internal fun trustLabel(trustRoot: TeeTrustRoot): String {
    return when (trustRoot) {
        TeeTrustRoot.GOOGLE -> "Google"
        TeeTrustRoot.GOOGLE_RKP -> "RKP"
        TeeTrustRoot.AOSP -> "AOSP"
        TeeTrustRoot.FACTORY -> "Factory"
        TeeTrustRoot.UNKNOWN -> "Unknown"
    }
}

internal fun trustStatus(report: BootloaderReport): DetectorStatus {
    return when {
        report.attestationChainLength == 0 -> DetectorStatus.danger()
        report.trustRoot == TeeTrustRoot.UNKNOWN -> DetectorStatus.danger()
        report.trustRoot == TeeTrustRoot.GOOGLE || report.trustRoot == TeeTrustRoot.GOOGLE_RKP ->
            DetectorStatus.allClear()

        report.trustRoot == TeeTrustRoot.AOSP -> DetectorStatus.warning()
        report.trustRoot == TeeTrustRoot.FACTORY -> DetectorStatus.info(InfoKind.SUPPORT)
        else -> DetectorStatus.info(InfoKind.SUPPORT)
    }
}

internal fun BootloaderReport.toCardAssessment(): BootloaderCardAssessment {
    val widevineReview = findings.any { finding ->
        finding.id.startsWith(WIDEVINE_FINDING_PREFIX) &&
            finding.severity == BootloaderFindingSeverity.WARNING
    }
    return if (widevineReview) {
        BootloaderCardAssessment.CONSISTENCY_REVIEW
    } else {
        BootloaderCardAssessment.AUTHORITATIVE
    }
}

// Card severity may include Widevine; the State fact remains authoritative.
internal fun BootloaderReport.authoritativeStateStatus(): DetectorStatus {
    return when (state) {
        BootloaderState.VERIFIED -> DetectorStatus.allClear()
        BootloaderState.SELF_SIGNED,
        BootloaderState.LOCKED_UNKNOWN -> DetectorStatus.warning()

        BootloaderState.UNLOCKED,
        BootloaderState.FAILED_VERIFICATION -> DetectorStatus.danger()

        BootloaderState.UNKNOWN -> DetectorStatus.info(InfoKind.SUPPORT)
    }
}

internal const val WIDEVINE_FINDING_PREFIX = "widevine_"
