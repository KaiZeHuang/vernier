package com.kaizehuang.vernier.features.tee.data.repository

import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyboxImportProbe
import com.kaizehuang.vernier.features.tee.data.verification.keystore.GrantDomainFullChainSplitResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.GrantSelfDomainFullChainSplitResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.SyntheticGrantGranteeBlindReadbackResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.OversizedChallengeProbe

internal data class DeferredChecks(
    val pairConsistency: com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyPairConsistencyResult,
    val aesGcm: com.kaizehuang.vernier.features.tee.data.verification.keystore.AesGcmRoundTripResult,
    val lifecycle: com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyLifecycleResult,
    val keyMintCapability: com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMintCapabilityResult,
    val timing: com.kaizehuang.vernier.features.tee.data.verification.keystore.TimingAnomalyResult,
    val timingSideChannel: com.kaizehuang.vernier.features.tee.data.verification.keystore.TimingSideChannelResult,
    val oversizedChallenge: com.kaizehuang.vernier.features.tee.data.verification.keystore.OversizedChallengeResult,
    val keyboxImport: com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyboxImportResult,
    val importKeyRetainedAttestationNarrative: com.kaizehuang.vernier.features.tee.data.verification.keystore.ImportKeyRetainedAttestationNarrativeResult,
    val keystore2Hook: com.kaizehuang.vernier.features.tee.data.verification.keystore.Keystore2HookResult,
    val generateModeParcelFingerprint: com.kaizehuang.vernier.features.tee.data.verification.keystore.Keystore2GenerateModeParcelFingerprintResult,
    val grantDomainFullChainSplit: com.kaizehuang.vernier.features.tee.data.verification.keystore.GrantDomainFullChainSplitResult,
    val syntheticGrantGranteeBlindReadback: com.kaizehuang.vernier.features.tee.data.verification.keystore.SyntheticGrantGranteeBlindReadbackResult,
    val syntheticGrantGetKeyEntryAccessVectorBlindness: com.kaizehuang.vernier.features.tee.data.verification.keystore.SyntheticGrantGetKeyEntryAccessVectorBlindnessResult,
    val grantSelfDomainFullChainSplit: com.kaizehuang.vernier.features.tee.data.verification.keystore.GrantSelfDomainFullChainSplitResult,
    val legacyKeystorePath: com.kaizehuang.vernier.features.tee.data.verification.keystore.LegacyKeystorePathResult,
    val listEntriesConsistency: com.kaizehuang.vernier.features.tee.data.verification.keystore.ListEntriesConsistencyResult,
    val listEntriesBatched: com.kaizehuang.vernier.features.tee.data.verification.keystore.ListEntriesBatchedResult,
    val keyMetadataSemantics: com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMetadataSemanticsResult,
    val keyMetadataShape: com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMetadataShapeResult,
    val pureCertificate: com.kaizehuang.vernier.features.tee.data.verification.keystore.PureCertificateResult,
    val pureCertificateSecurityLevel: com.kaizehuang.vernier.features.tee.data.verification.keystore.PureCertificateSecurityLevelResult,
    val operationErrorPath: com.kaizehuang.vernier.features.tee.data.verification.keystore.OperationErrorPathResult,
    val biometricIntegration: com.kaizehuang.vernier.features.tee.data.verification.keystore.BiometricTeeIntegrationResult,
    val binderHookBootstrap: com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderHookBootstrapResult,
    val binderPatchMode: com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderPatchModeResult,
    val binderChainConsistency: com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderChainConsistencyResult,
    val updateSubcomponent: com.kaizehuang.vernier.features.tee.data.verification.keystore.UpdateSubcomponentResult,
    val updateSubcomponentStaleResponsePersistence: com.kaizehuang.vernier.features.tee.data.verification.keystore.UpdateSubcomponentStaleResponsePersistenceResult,
    val pruning: com.kaizehuang.vernier.features.tee.data.verification.keystore.OperationPruningResult,
    val dualAlgorithm: com.kaizehuang.vernier.features.tee.data.verification.certificate.DualAlgorithmChainResult,
    val idAttestation: com.kaizehuang.vernier.features.tee.data.verification.keystore.IdAttestationResult,
    val strongBox: com.kaizehuang.vernier.features.tee.data.verification.strongbox.StrongBoxBehaviorResult,
) {
    companion object {
        fun skipped(
            snapshot: com.kaizehuang.vernier.capability.attestation.data.AttestationSnapshot,
            timingSideChannel: com.kaizehuang.vernier.features.tee.data.verification.keystore.TimingSideChannelResult,
        ) = DeferredChecks(
            pairConsistency = com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyPairConsistencyResult(
                executed = false,
                keyMatchesCertificate = false,
                detail = "Deep checks were skipped because hardware-backed attestation was not established.",
            ),
            aesGcm = com.kaizehuang.vernier.features.tee.data.verification.keystore.AesGcmRoundTripResult(
                executed = false,
                detail = "AES-GCM round-trip probe skipped.",
            ),
            lifecycle = com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyLifecycleResult(
                executed = false,
                created = false,
                deleteRemovedAlias = false,
                regeneratedFreshMaterial = false,
                detail = "Lifecycle probe skipped.",
            ),
            keyMintCapability = com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMintCapabilityResult(
                executed = false,
            ),
            timing = com.kaizehuang.vernier.features.tee.data.verification.keystore.TimingAnomalyResult(
                suspicious = false,
                detail = "Timing probe skipped.",
            ),
            timingSideChannel = timingSideChannel,

            oversizedChallenge = com.kaizehuang.vernier.features.tee.data.verification.keystore.OversizedChallengeResult(
                executed = false,
                acceptedOversizedChallenge = false,
                acceptedSizes = emptyList(),
                attemptedSizes = com.kaizehuang.vernier.features.tee.data.verification.keystore.OversizedChallengeProbe.CHALLENGE_SIZES,
                detail = "Oversized challenge probe skipped.",
            ),
            keyboxImport = com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyboxImportResult(
                executed = false,
                markerPreserved = true,
                marker = com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyboxImportProbe.FIXTURE_MARKER,
                detail = "Keybox import probe skipped.",
            ),
            importKeyRetainedAttestationNarrative = com.kaizehuang.vernier.features.tee.data.verification.keystore.ImportKeyRetainedAttestationNarrativeResult(
                executed = false,
                detail = "ImportKey retained attestation narrative probe skipped.",
            ),
            keystore2Hook = com.kaizehuang.vernier.features.tee.data.verification.keystore.Keystore2HookResult(
                available = false,
                detail = "Keystore2 hook probe skipped.",
            ),
            generateModeParcelFingerprint = com.kaizehuang.vernier.features.tee.data.verification.keystore.Keystore2GenerateModeParcelFingerprintResult(
                executed = false,
                detail = "Keystore2 generate-mode parcel fingerprint probe skipped.",
            ),
            grantDomainFullChainSplit = com.kaizehuang.vernier.features.tee.data.verification.keystore.GrantDomainFullChainSplitResult(
                detail = "Grant-domain full-chain split probe skipped.",
            ),
            syntheticGrantGranteeBlindReadback = com.kaizehuang.vernier.features.tee.data.verification.keystore.SyntheticGrantGranteeBlindReadbackResult(
                detail = "Grant caller-binding private binder probe skipped.",
            ),
            syntheticGrantGetKeyEntryAccessVectorBlindness =
                com.kaizehuang.vernier.features.tee.data.verification.keystore.SyntheticGrantGetKeyEntryAccessVectorBlindnessResult(
                    detail = "Grant access-vector private binder probe skipped.",
                ),
            grantSelfDomainFullChainSplit = com.kaizehuang.vernier.features.tee.data.verification.keystore.GrantSelfDomainFullChainSplitResult(
                detail = "Grant self-domain full-chain split probe skipped.",
            ),
            legacyKeystorePath = com.kaizehuang.vernier.features.tee.data.verification.keystore.LegacyKeystorePathResult(
                executed = false,
                detail = "Legacy keystore path probe skipped.",
            ),
            listEntriesConsistency = com.kaizehuang.vernier.features.tee.data.verification.keystore.ListEntriesConsistencyResult(
                executed = false,
                detail = "listEntries consistency probe skipped.",
            ),
            listEntriesBatched = com.kaizehuang.vernier.features.tee.data.verification.keystore.ListEntriesBatchedResult(
                executed = false,
                detail = "listEntriesBatched probe skipped.",
            ),
            keyMetadataSemantics = com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMetadataSemanticsResult(
                executed = false,
                detail = "KeyMetadata semantics probe skipped.",
            ),
            keyMetadataShape = com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMetadataShapeResult(
                executed = false,
                detail = "KeyMetadata shape probe skipped.",
            ),
            pureCertificate = com.kaizehuang.vernier.features.tee.data.verification.keystore.PureCertificateResult(
                executed = false,
                pureCertificateReturnsNullKey = false,
                detail = "Pure certificate probe skipped.",
            ),
            pureCertificateSecurityLevel = com.kaizehuang.vernier.features.tee.data.verification.keystore.PureCertificateSecurityLevelResult(
                executed = false,
                detail = "Pure certificate security-level probe skipped.",
            ),
            operationErrorPath = com.kaizehuang.vernier.features.tee.data.verification.keystore.OperationErrorPathResult(
                executed = false,
                detail = "Operation error-path probe skipped.",
            ),
            biometricIntegration = com.kaizehuang.vernier.features.tee.data.verification.keystore.BiometricTeeIntegrationResult(
                executed = false,
                detail = "Biometric TEE integration probe skipped.",
            ),
            binderHookBootstrap = com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderHookBootstrapResult(
                executed = false,
                detail = "Binder hook bootstrap probe skipped.",
            ),
            binderPatchMode = com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderPatchModeResult(
                executed = false,
                detail = "Binder patch-mode probe skipped.",
            ),
            binderChainConsistency = com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderChainConsistencyResult(
                executed = false,
                detail = "Binder chain consistency probe skipped.",
            ),
            updateSubcomponent = com.kaizehuang.vernier.features.tee.data.verification.keystore.UpdateSubcomponentResult(
                executed = false,
                updateSucceeded = false,
                keyNotFoundStyleFailure = false,
                detail = "Update subcomponent probe skipped.",
            ),
            updateSubcomponentStaleResponsePersistence =
                com.kaizehuang.vernier.features.tee.data.verification.keystore.UpdateSubcomponentStaleResponsePersistenceResult(
                    detail = "UpdateSubcomponent stale response persistence probe skipped.",
                ),
            pruning = com.kaizehuang.vernier.features.tee.data.verification.keystore.OperationPruningResult(
                suspicious = false,
                operationsCreated = 0,
                invalidatedOperations = 0,
                detail = "Pruning probe skipped.",
            ),
            dualAlgorithm = com.kaizehuang.vernier.features.tee.data.verification.certificate.DualAlgorithmChainResult(
                executed = false,
                mismatchDetected = false,
                detail = "Dual algorithm comparison skipped.",
            ),
            idAttestation = com.kaizehuang.vernier.features.tee.data.verification.keystore.IdAttestationResult(
                mismatches = emptyList(),
                unavailableFields = emptyList(),
                detail = "ID attestation probe skipped.",
                probeRan = false,
            ),
            strongBox = com.kaizehuang.vernier.features.tee.data.verification.strongbox.StrongBoxBehaviorResult(
                requested = false,
                advertised = false,
                available = false,
                detail = "StrongBox probe skipped.",
            ),
        )
    }
}
