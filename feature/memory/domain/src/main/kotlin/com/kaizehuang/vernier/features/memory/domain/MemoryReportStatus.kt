package com.kaizehuang.vernier.features.memory.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun MemoryReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        MemoryStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        MemoryStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        MemoryStage.READY -> when {
            dangerFindingCount > 0 -> DetectorStatus.danger()
            reviewFindingCount > 0 -> DetectorStatus.warning()
            !nativeAvailable -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}
