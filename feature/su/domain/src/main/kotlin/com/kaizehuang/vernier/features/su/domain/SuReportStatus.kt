package com.kaizehuang.vernier.features.su.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun SuReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        SuStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        SuStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        SuStage.READY -> when {
            hasRootIndicators -> DetectorStatus.danger()
            !nativeAvailable || unobservablePathCount > 0 -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}
