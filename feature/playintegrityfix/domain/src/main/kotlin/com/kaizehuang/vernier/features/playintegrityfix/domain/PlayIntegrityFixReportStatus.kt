package com.kaizehuang.vernier.features.playintegrityfix.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun PlayIntegrityFixReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        PlayIntegrityFixStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        PlayIntegrityFixStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        PlayIntegrityFixStage.READY -> when {
            dangerSignalCount > 0 -> DetectorStatus.danger()
            warningSignalCount > 0 -> DetectorStatus.warning()
            !nativeAvailable -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}
