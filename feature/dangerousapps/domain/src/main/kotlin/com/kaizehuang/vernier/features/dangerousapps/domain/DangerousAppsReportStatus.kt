package com.kaizehuang.vernier.features.dangerousapps.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun DangerousAppsReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        DangerousAppsStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        DangerousAppsStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        DangerousAppsStage.READY -> when {
            hiddenCount > 0 -> DetectorStatus.danger()
            detectedCount > 0 -> DetectorStatus.warning()
            suspiciousSharedStorageDenied -> DetectorStatus.warning()
            suspiciousLowPmInventory -> DetectorStatus.warning()
            packageVisibility == DangerousPackageVisibility.RESTRICTED -> DetectorStatus.info(
                InfoKind.ERROR
            )

            packageVisibility == DangerousPackageVisibility.UNKNOWN -> DetectorStatus.info(
                InfoKind.SUPPORT
            )

            else -> DetectorStatus.allClear()
        }
    }
}
