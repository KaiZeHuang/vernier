package com.kaizehuang.vernier.features.customrom.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun CustomRomReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        CustomRomStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        CustomRomStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        CustomRomStage.READY -> when {
            hasIndicators -> DetectorStatus.warning()
            hasReducedCoverage() -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}

fun CustomRomReport.hasReducedCoverage(): Boolean {
    return !nativeAvailable ||
            !propertyAreaAvailable ||
            !symbolScanAvailable ||
            !serviceScanAvailable ||
            packageVisibility != CustomRomPackageVisibility.FULL
}
