package com.kaizehuang.vernier.features.mount.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun MountReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        MountStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        MountStage.FAILED -> when {
            dangerSignalCount > 0 -> DetectorStatus.danger()
            warningSignalCount > 0 -> DetectorStatus.warning()
            else -> DetectorStatus.info(InfoKind.ERROR)
        }
        MountStage.READY -> when {
            dangerSignalCount > 0 -> DetectorStatus.danger()
            warningSignalCount > 0 -> DetectorStatus.warning()
            else -> DetectorStatus.allClear()
        }
    }
}
