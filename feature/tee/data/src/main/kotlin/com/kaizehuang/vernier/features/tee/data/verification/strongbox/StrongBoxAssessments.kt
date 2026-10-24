package com.kaizehuang.vernier.features.tee.data.verification.strongbox

import android.security.keystore.KeyInfo
import android.security.keystore.StrongBoxUnavailableException
import com.kaizehuang.vernier.capability.attestation.domain.TeeTier
import java.security.InvalidAlgorithmParameterException
import java.security.ProviderException

/**
 * Classifies a StrongBox key generation attempt by which layer refused it, from what it threw.
 *
 * `checkValidKeySize` and the rest of `AndroidKeyStoreKeyPairGeneratorSpi.initialize` report a
 * parameter set they will not pass on as [InvalidAlgorithmParameterException], before Keystore is
 * called at all. `generateKeyPair` reports a Keystore or KeyMint refusal as a [ProviderException],
 * and `StrongBoxUnavailableException` is one of those. Anything else means the attempt broke down
 * without either layer answering, so it must not be read as a refusal.
 *
 * [failure] is null when the key was created.
 */
internal fun classifyStrongBoxAcceptance(failure: Throwable?): StrongBoxAcceptance {
    return when (failure) {
        null -> StrongBoxAcceptance.ACCEPTED
        is InvalidAlgorithmParameterException -> StrongBoxAcceptance.REFUSED_BY_FRAMEWORK
        is ProviderException -> StrongBoxAcceptance.REFUSED_BY_KEYSTORE
        else -> StrongBoxAcceptance.INCONCLUSIVE
    }
}

/**
 * Turns a finished counting loop into an observation, separating "the platform stopped granting
 * handles" from "the probe ran out of attempts".
 */
internal fun describeConcurrentSigningHandles(
    granted: Int,
    ceiling: Int,
    failureDescription: String?,
): ConcurrentSigningHandleObservation {
    if (failureDescription == null) {
        return ConcurrentSigningHandleObservation(
            granted = granted,
            stop = ConcurrentHandleStop.PROBE_CEILING,
            detail = "All $ceiling attempted handles were granted; no ceiling was reached.",
        )
    }
    // The refusal is recorded, not classified further. keystore2 reports an exhausted and unprunable
    // operation database as BACKEND_BUSY while KeyMint reports TOO_MANY_OPERATIONS, and how each
    // surfaces through the JCA layer has not been verified per Android version, so naming a cause
    // here would exceed what this probe establishes.
    return ConcurrentSigningHandleObservation(
        granted = granted,
        stop = ConcurrentHandleStop.REFUSED,
        detail = "Refused after $granted handles: $failureDescription",
    )
}

internal fun assessStrongBoxAttestation(
    available: Boolean,
    attestationTier: TeeTier,
): StrongBoxAttestationAssessment {
    return when {
        available && attestationTier == TeeTier.UNKNOWN -> StrongBoxAttestationAssessment(
            warning = "StrongBox key generation succeeded, but dedicated attestation did not expose a tier.",
        )

        available && attestationTier != TeeTier.STRONGBOX -> StrongBoxAttestationAssessment(
            hardFailure = "StrongBox key generation succeeded, but attestation tier came back as $attestationTier.",
        )

        !available && attestationTier == TeeTier.STRONGBOX -> StrongBoxAttestationAssessment(
            hardFailure = "Attestation claimed StrongBox, but local KeyInfo could not confirm a StrongBox key.",
        )

        else -> StrongBoxAttestationAssessment()
    }
}

internal data class StrongBoxAttestationAssessment(
    val hardFailure: String? = null,
    val warning: String? = null,
)
