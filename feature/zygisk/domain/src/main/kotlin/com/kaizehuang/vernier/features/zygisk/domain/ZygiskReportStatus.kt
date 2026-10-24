package com.kaizehuang.vernier.features.zygisk.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun ZygiskReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        ZygiskStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        ZygiskStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        ZygiskStage.READY -> when {
            fdTrapDetected -> DetectorStatus.danger()
            nativeStrongHitCount > 0 -> DetectorStatus.danger()
            heuristicHitCount >= 2 -> DetectorStatus.danger()
            heuristicHitCount == 1 -> DetectorStatus.warning()
            fullyClean -> DetectorStatus.allClear()
            else -> DetectorStatus.info(InfoKind.SUPPORT)
        }
    }
}
