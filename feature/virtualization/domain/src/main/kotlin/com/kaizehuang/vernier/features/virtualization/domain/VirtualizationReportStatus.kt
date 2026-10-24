package com.kaizehuang.vernier.features.virtualization.domain

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun VirtualizationReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        VirtualizationStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        VirtualizationStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        VirtualizationStage.READY -> when {
            dangerSignals.isNotEmpty() -> DetectorStatus.danger()
            warningSignals.isNotEmpty() -> DetectorStatus.warning()
            onlyHostAppCorroboration -> DetectorStatus.info(InfoKind.SUPPORT)
            hasReducedCoverage() -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}

fun VirtualizationReport.hasReducedCoverage(): Boolean {
    return !nativeAvailable ||
            !startupPreloadAvailable ||
            !startupPreloadContextValid ||
            !crossProcessAvailable ||
            !isolatedProcessAvailable ||
            !eglAvailable ||
            !mountNamespaceAvailable ||
            !syscallPackSupported ||
            packageVisibility != InstalledPackageVisibility.FULL
}
