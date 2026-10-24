package com.kaizehuang.vernier.features.tee.data.report

import com.kaizehuang.vernier.features.tee.domain.TeeEvidenceItem
import com.kaizehuang.vernier.features.tee.domain.TeeEvidenceSection
import com.kaizehuang.vernier.features.tee.domain.TeeEvidenceSectionKind
import com.kaizehuang.vernier.features.tee.domain.TeeEvidenceTopic
import com.kaizehuang.vernier.features.tee.domain.TeePatchState
import com.kaizehuang.vernier.features.tee.domain.TeeSignalLevel

internal fun buildSections(
    artifacts: TeeScanArtifacts,
    patchState: TeePatchState,
    policyHardIndicators: List<TeeEvidenceItem>,
    policySoftIndicators: List<TeeEvidenceItem>,
    supplementaryIndicators: List<TeeEvidenceItem>,
): List<TeeEvidenceSection> {
    return listOf(
        TeeEvidenceSection(
            kind = TeeEvidenceSectionKind.TRUST,
            items = trustItems(artifacts),
        ),
        TeeEvidenceSection(
            kind = TeeEvidenceSectionKind.ATTESTATION,
            items = attestationItems(artifacts),
        ),
        TeeEvidenceSection(
            kind = TeeEvidenceSectionKind.CHECKS,
            items = checkItems(artifacts, policyHardIndicators, policySoftIndicators, supplementaryIndicators),
        ),
    )
}

private fun trustItems(artifacts: TeeScanArtifacts): List<TeeEvidenceItem> = buildList {
    add(
        fact(
            "Local chain",
            if (artifacts.trust.chainSignatureValid) "Verified" else "Failed",
            if (artifacts.trust.chainSignatureValid) TeeSignalLevel.PASS else TeeSignalLevel.FAIL
        )
    )
    add(
        fact(
            "Trust root",
            trustRootLabel(artifacts.trust.trustRoot),
            trustLevel(artifacts),
            topic = TeeEvidenceTopic.TRUST_ROOT,
        )
    )
    add(
        fact(
            "Chain layout",
            chainLayoutValue(artifacts),
            chainLayoutLevel(artifacts)
        )
    )
    add(fact("RKP", rkpValue(artifacts), rkpDisplayLevel(artifacts), topic = TeeEvidenceTopic.RKP))
    add(fact("CRL", crlValue(artifacts), crlSignalLevel(artifacts), topic = TeeEvidenceTopic.CRL))
    add(
        fact(
            "Root fingerprint",
            shortFingerprint(artifacts.trust.rootFingerprint),
            TeeSignalLevel.INFO
        )
    )
}

private fun attestationItems(artifacts: TeeScanArtifacts): List<TeeEvidenceItem> = buildList {
    add(
        fact(
            "Tier",
            tierValue(artifacts),
            tierLevel(effectiveTier(artifacts))
        )
    )
    add(fact("Versions", versionsValue(artifacts.snapshot), TeeSignalLevel.INFO))
    add(
        fact(
            "Challenge",
            challengeValue(artifacts.snapshot),
            challengeLevel(artifacts.snapshot)
        )
    )
    add(
        fact(
            "Verified boot",
            verifiedBootValue(artifacts.snapshot),
            verifiedBootLevel(artifacts.snapshot),
            topic = TeeEvidenceTopic.VERIFIED_BOOT,
        )
    )
    add(
        fact(
            "Boot consistency",
            bootConsistencyValue(artifacts),
            bootSignalLevel(artifacts),
            topic = TeeEvidenceTopic.BOOT_CONSISTENCY,
        )
    )
    add(
        fact(
            "Device IDs",
            deviceInfoValue(artifacts.snapshot),
            deviceInfoLevel(artifacts.snapshot),
            topic = TeeEvidenceTopic.DEVICE_IDS,
        )
    )
    add(
        fact(
            "Key properties",
            keyPropertiesValue(artifacts.snapshot),
            TeeSignalLevel.INFO
        )
    )
    add(fact("User auth", authStateValue(artifacts.snapshot), TeeSignalLevel.INFO, topic = TeeEvidenceTopic.USER_AUTH))
    add(
        fact(
            "Application",
            applicationInfoValue(artifacts.snapshot),
            TeeSignalLevel.INFO,
            topic = TeeEvidenceTopic.APPLICATION,
        )
    )
}
