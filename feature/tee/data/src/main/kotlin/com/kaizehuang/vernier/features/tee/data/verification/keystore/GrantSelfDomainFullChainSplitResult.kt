package com.kaizehuang.vernier.features.tee.data.verification.keystore

data class GrantSelfDomainFullChainSplitResult(
    val executed: Boolean = false,
    val available: Boolean = false,
    val splitDetected: Boolean = false,
    val ownerChainLength: Int = 0,
    val grantChainLength: Int = 0,
    val mismatchIndex: Int? = null,
    val grantIdPresent: Boolean = false,
    val anomalyKind: GrantSelfDomainAnomalyKind = GrantSelfDomainAnomalyKind.UNAVAILABLE,
    val detail: String = "",
    val diagnosticCopyText: String = "",
)

enum class GrantSelfDomainAnomalyKind {
    NONE,
    SELF_CHAIN_SPLIT,
    SELF_GRANT_KEY_NOT_FOUND_AFTER_OWNER_CHAIN,
    SELF_GRANT_ATTESTATION_APP_KEY_NOT_FOUND,
    UNAVAILABLE,
}
