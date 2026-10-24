package com.kaizehuang.vernier.features.lsposed.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun LSPosedReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        LSPosedStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        LSPosedStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        LSPosedStage.READY -> when {
            hasDangerSignals -> DetectorStatus.danger()
            hasWarningSignals -> DetectorStatus.warning()
            hasReducedCoverage() -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}

fun LSPosedReport.hasReducedCoverage(): Boolean {
    return !nativeAvailable ||
            !nativeMapsAvailable ||
            !nativeHeapAvailable ||
            !zygotePermissionAvailable ||
            !runtimeArtifactAvailable ||
            !logcatAvailable ||
            !dirtyPolicyAvailable ||
            packageVisibility != LSPosedPackageVisibility.FULL
}
