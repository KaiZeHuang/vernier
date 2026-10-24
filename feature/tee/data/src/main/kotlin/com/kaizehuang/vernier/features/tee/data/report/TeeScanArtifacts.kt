package com.kaizehuang.vernier.features.tee.data.report

import com.kaizehuang.vernier.capability.attestation.data.AttestationSnapshot
import com.kaizehuang.vernier.features.tee.data.native.NativeTeeSnapshot
import com.kaizehuang.vernier.capability.attestation.data.BootConsistencyResult
import com.kaizehuang.vernier.features.tee.data.verification.certificate.ChainStructureResult
import com.kaizehuang.vernier.capability.attestation.data.CertificateTrustResult
import com.kaizehuang.vernier.features.tee.data.verification.certificate.DualAlgorithmChainResult
import com.kaizehuang.vernier.features.tee.data.verification.crl.CrlStatusResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.IdAttestationResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.AesGcmRoundTripResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderChainConsistencyResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderHookBootstrapResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.BinderPatchModeResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.BiometricTeeIntegrationResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.ImportKeyRetainedAttestationNarrativeResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyboxImportResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.Keystore2GenerateModeParcelFingerprintResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.Keystore2HookResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.Keystore2PostProcessingResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyLifecycleResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMintCapabilityResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.GrantDomainFullChainSplitResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.GrantSelfDomainFullChainSplitResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.SyntheticGrantGetKeyEntryAccessVectorBlindnessResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.SyntheticGrantGranteeBlindReadbackResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.LegacyKeystorePathResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.ListEntriesBatchedResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.ListEntriesConsistencyResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMetadataSemanticsResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyMetadataShapeResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.KeyPairConsistencyResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.OperationErrorPathResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.OperationPruningResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.OversizedChallengeResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.PureCertificateResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.PureCertificateSecurityLevelResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.SupplementaryAttestationInfoResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.TimingAnomalyResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.TimingSideChannelResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.UpdateSubcomponentResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.UpdateSubcomponentStaleResponsePersistenceResult
import com.kaizehuang.vernier.features.tee.data.verification.keystore.VintfKeyMintVersionResult
import com.kaizehuang.vernier.features.tee.data.verification.strongbox.StrongBoxBehaviorResult
import com.kaizehuang.vernier.features.tee.data.verification.rkp.RkpProvisionedManufacturerResult
import com.kaizehuang.vernier.features.tee.domain.TeeRkpState
import com.kaizehuang.vernier.features.tee.domain.TeeSoterState

data class TeeScanArtifacts(
    val snapshot: AttestationSnapshot,
    val trust: CertificateTrustResult,
    val chainStructure: ChainStructureResult,
    val rkp: TeeRkpState,
    val crl: CrlStatusResult,
    val pairConsistency: KeyPairConsistencyResult,
    val aesGcm: AesGcmRoundTripResult,
    val lifecycle: KeyLifecycleResult,
    val keyMintCapability: KeyMintCapabilityResult,
    val timing: TimingAnomalyResult,
    val timingSideChannel: TimingSideChannelResult,
    val oversizedChallenge: OversizedChallengeResult,
    val keyboxImport: KeyboxImportResult,
    val importKeyRetainedAttestationNarrative: ImportKeyRetainedAttestationNarrativeResult,
    val supplementaryAttestationInfo: SupplementaryAttestationInfoResult,
    val vintfKeyMintVersion: VintfKeyMintVersionResult,
    val keystore2Hook: Keystore2HookResult,
    val generateModeParcelFingerprint: Keystore2GenerateModeParcelFingerprintResult,
    val postProcessing: Keystore2PostProcessingResult,
    val rkpProvisionedManufacturer: RkpProvisionedManufacturerResult,
    val grantDomainFullChainSplit: GrantDomainFullChainSplitResult,
    val syntheticGrantGranteeBlindReadback: SyntheticGrantGranteeBlindReadbackResult,
    val syntheticGrantGetKeyEntryAccessVectorBlindness: SyntheticGrantGetKeyEntryAccessVectorBlindnessResult,
    val grantSelfDomainFullChainSplit: GrantSelfDomainFullChainSplitResult,
    val legacyKeystorePath: LegacyKeystorePathResult,
    val listEntriesConsistency: ListEntriesConsistencyResult,
    val listEntriesBatched: ListEntriesBatchedResult,
    val keyMetadataSemantics: KeyMetadataSemanticsResult,
    val keyMetadataShape: KeyMetadataShapeResult,
    val pureCertificate: PureCertificateResult,
    val pureCertificateSecurityLevel: PureCertificateSecurityLevelResult,
    val operationErrorPath: OperationErrorPathResult,
    val biometricIntegration: BiometricTeeIntegrationResult,
    val binderHookBootstrap: BinderHookBootstrapResult,
    val binderPatchMode: BinderPatchModeResult,
    val binderChainConsistency: BinderChainConsistencyResult,
    val updateSubcomponent: UpdateSubcomponentResult,
    val updateSubcomponentStaleResponsePersistence: UpdateSubcomponentStaleResponsePersistenceResult,
    val pruning: OperationPruningResult,
    val dualAlgorithm: DualAlgorithmChainResult,
    val idAttestation: IdAttestationResult,
    val strongBox: StrongBoxBehaviorResult,
    val native: NativeTeeSnapshot,
    val soter: TeeSoterState,
    val bootConsistency: BootConsistencyResult,
)
