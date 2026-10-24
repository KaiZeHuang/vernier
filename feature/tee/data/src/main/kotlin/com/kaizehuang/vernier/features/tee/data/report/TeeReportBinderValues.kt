package com.kaizehuang.vernier.features.tee.data.report

internal fun binderChainConsistencyValue(artifacts: TeeScanArtifacts): String {
    val result = artifacts.binderChainConsistency
    val status = when {
        !result.executed -> "Skipped"
        !result.hookInstalled -> "Hook bootstrap failed"
        result.suspiciousLeafIssuerSpki -> "Leaf SPKI matched issuer SPKI"
        !result.activeProbeSecondCycleSucceeded -> "Repeated active probe failed"
        !result.deleteEntryRemovedAlias -> "deleteEntry left alias present"
        !result.keystoreChainAvailable -> "Java chain unavailable"
        !result.binderMaterialAvailable -> "Binder chain unavailable"
        !result.generateVsGetKeyEntryLeafMatches -> "generateKey leaf differed from getKeyEntry"
        !result.generateVsGetKeyEntryChainMatches -> "generateKey chain differed from getKeyEntry"
        result.chainMatches -> "Java and binder chains aligned"
        result.leafMatches -> "Leaf matched but chain diverged"
        else -> "Leaf and chain diverged"
    }
    val detail = result.detail.takeIf { it.isNotBlank() } ?: return status
    return "$status • $detail"
}

internal fun binderHookBootstrapValue(artifacts: TeeScanArtifacts): String {
    val result = artifacts.binderHookBootstrap
    return when {
        !result.executed -> "Skipped"
        result.hookInstalled -> "Hook installed"
        else -> "Hook bootstrap failed"
    }
}

internal fun legacyKeystorePathValue(artifacts: TeeScanArtifacts): String {
    val result = artifacts.legacyKeystorePath
    return when {
        !result.executed -> "Skipped"
        !result.hookInstalled -> "Hook unavailable"
        !result.legacyMaterialAvailable -> "Legacy path not observed"
        result.chainMatches -> "Legacy path aligned"
        result.userCertCaptured || result.caCertCaptured -> "Legacy path captured but diverged"
        else -> "Legacy path unavailable"
    }
}

internal fun binderPatchModeValue(artifacts: TeeScanArtifacts): String {
    val result = artifacts.binderPatchMode
    return when {
        !result.executed -> "Skipped"
        !result.hookInstalled -> "Hook unavailable"
        result.leafDiffers -> "Leaf differed between generateKey and getKeyEntry"
        result.chainDiffers -> "Chain differed between generateKey and getKeyEntry"
        result.generateMaterialAvailable && result.keyEntryMaterialAvailable -> "generateKey/getKeyEntry aligned"
        else -> "Capture unavailable"
    }
}
