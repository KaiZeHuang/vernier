package com.kaizehuang.vernier.features.bootloader.data.repository

import com.kaizehuang.vernier.capability.attestation.data.AttestationSnapshot
import com.kaizehuang.vernier.capability.attestation.data.CertificateTrustResult
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderEvidenceMode
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFinding
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingGroup
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingSeverity
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderState

internal fun resolveState(
    attestation: AttestationSnapshot,
    propertyContext: BootloaderPropertyContext,
): BootloaderState {
    val root = attestation.rootOfTrust
    if (root != null && (root.deviceLocked != null || root.verifiedBootState.isNullOrBlank()
            .not())
    ) {
        return when (root.verifiedBootState?.trim()) {
            "Verified" -> BootloaderState.VERIFIED
            "Self-signed" -> BootloaderState.SELF_SIGNED
            "Unverified" -> BootloaderState.UNLOCKED
            "Failed" -> BootloaderState.FAILED_VERIFICATION
            else -> when (root.deviceLocked) {
                false -> BootloaderState.UNLOCKED
                true -> BootloaderState.LOCKED_UNKNOWN
                null -> BootloaderState.UNKNOWN
            }
        }
    }

    return when (propertyContext.bootState) {
        PropertyBootState.GREEN -> BootloaderState.VERIFIED
        PropertyBootState.YELLOW -> BootloaderState.SELF_SIGNED
        PropertyBootState.ORANGE -> BootloaderState.UNLOCKED
        PropertyBootState.RED -> BootloaderState.FAILED_VERIFICATION
        PropertyBootState.UNKNOWN -> when (propertyContext.isLocked) {
            true -> BootloaderState.LOCKED_UNKNOWN
            false -> BootloaderState.UNLOCKED
            null -> BootloaderState.UNKNOWN
        }
    }
}

internal fun buildStateFindings(
    state: BootloaderState,
    evidenceMode: BootloaderEvidenceMode,
    attestation: AttestationSnapshot,
    trust: CertificateTrustResult,
    propertyContext: BootloaderPropertyContext,
): List<BootloaderFinding> {
    return buildList {
        add(
            BootloaderFinding(
                id = "boot_state",
                label = "Boot state",
                value = stateLabel(state, evidenceMode),
                group = BootloaderFindingGroup.STATE,
                severity = stateSeverity(state),
                detail = stateDetail(state, evidenceMode),
            ),
        )
        add(
            BootloaderFinding(
                id = "evidence_mode",
                label = "Evidence source",
                value = evidenceModeLabel(evidenceMode),
                group = BootloaderFindingGroup.STATE,
                severity = when (evidenceMode) {
                    BootloaderEvidenceMode.ATTESTATION -> BootloaderFindingSeverity.SAFE
                    BootloaderEvidenceMode.PROPERTIES_ONLY -> BootloaderFindingSeverity.INFO
                    BootloaderEvidenceMode.UNAVAILABLE -> BootloaderFindingSeverity.WARNING
                },
                detail = when (evidenceMode) {
                    BootloaderEvidenceMode.ATTESTATION ->
                        "RootOfTrust data was available and took priority over software-readable properties."

                    BootloaderEvidenceMode.PROPERTIES_ONLY ->
                        "Attestation root data was unavailable, so this result falls back to boot properties that can be modified on rooted systems."

                    BootloaderEvidenceMode.UNAVAILABLE ->
                        "Neither attestation RootOfTrust nor readable boot properties were available."
                },
            ),
        )
        val lockLabel = when (val locked =
            attestation.rootOfTrust?.deviceLocked ?: propertyContext.isLocked) {
            true -> "Locked"
            false -> "Unlocked"
            null -> "Unknown"
        }
        add(
            BootloaderFinding(
                id = "lock_state",
                label = "Lock state",
                value = lockLabel,
                group = BootloaderFindingGroup.STATE,
                severity = when (attestation.rootOfTrust?.deviceLocked
                    ?: propertyContext.isLocked) {
                    true -> BootloaderFindingSeverity.SAFE
                    false -> BootloaderFindingSeverity.DANGER
                    null -> BootloaderFindingSeverity.INFO
                },
                detail = lockStateDetail(attestation, propertyContext),
            ),
        )
        add(
            BootloaderFinding(
                id = "trust_root",
                label = "Trust root",
                value = trustRootLabel(trust.trustRoot),
                group = BootloaderFindingGroup.STATE,
                severity = trustRootSeverity(trust),
                detail = trustRootDetail(trust),
            ),
        )
    }
}

private fun stateLabel(
    state: BootloaderState,
    evidenceMode: BootloaderEvidenceMode,
): String {
    return when (state) {
        BootloaderState.VERIFIED -> if (evidenceMode == BootloaderEvidenceMode.PROPERTIES_ONLY) "Locked by props" else "Verified"
        BootloaderState.SELF_SIGNED -> "Self-signed"
        BootloaderState.UNLOCKED -> "Unlocked"
        BootloaderState.FAILED_VERIFICATION -> "Failed"
        BootloaderState.LOCKED_UNKNOWN -> "Locked, state unknown"
        BootloaderState.UNKNOWN -> "Unknown"
    }
}

private fun stateDetail(
    state: BootloaderState,
    evidenceMode: BootloaderEvidenceMode,
): String {
    return when (state) {
        BootloaderState.VERIFIED -> if (evidenceMode == BootloaderEvidenceMode.PROPERTIES_ONLY) {
            "Boot properties indicate a locked device and do not contradict a verified boot chain, but attestation RootOfTrust was unavailable."
        } else {
            "Attestation reported a verified boot chain rooted in a locked device state."
        }

        BootloaderState.SELF_SIGNED ->
            "Boot verification succeeded against a user-managed root of trust rather than the OEM root."

        BootloaderState.UNLOCKED ->
            "Bootloader appears unlocked or Verified Boot reported an unverified/orange state."

        BootloaderState.FAILED_VERIFICATION ->
            "Verified Boot reported a failed/red state."

        BootloaderState.LOCKED_UNKNOWN ->
            "Device appears locked, but the verified boot color/state was not exposed clearly enough to confirm OEM verification."

        BootloaderState.UNKNOWN ->
            "Neither attestation nor boot properties exposed a stable boot state."
    }
}

private fun lockStateDetail(
    attestation: AttestationSnapshot,
    propertyContext: BootloaderPropertyContext,
): String {
    val rootLocked = attestation.rootOfTrust?.deviceLocked
    return when {
        rootLocked != null -> "Derived from RootOfTrust.deviceLocked in attestation."
        propertyContext.lockEvidence.isNotEmpty() -> "Derived from ${propertyContext.lockEvidence.joinToString()}."
        else -> "No reliable lock-state evidence surfaced."
    }
}

internal fun stateSeverity(state: BootloaderState): BootloaderFindingSeverity {
    return when (state) {
        BootloaderState.VERIFIED -> BootloaderFindingSeverity.SAFE
        BootloaderState.SELF_SIGNED,
        BootloaderState.LOCKED_UNKNOWN -> BootloaderFindingSeverity.WARNING

        BootloaderState.UNLOCKED,
        BootloaderState.FAILED_VERIFICATION -> BootloaderFindingSeverity.DANGER

        BootloaderState.UNKNOWN -> BootloaderFindingSeverity.INFO
    }
}

private fun evidenceModeLabel(mode: BootloaderEvidenceMode): String {
    return when (mode) {
        BootloaderEvidenceMode.ATTESTATION -> "Attestation"
        BootloaderEvidenceMode.PROPERTIES_ONLY -> "Properties"
        BootloaderEvidenceMode.UNAVAILABLE -> "Unavailable"
    }
}
