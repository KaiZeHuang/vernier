package com.kaizehuang.vernier.features.adbruntime.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun AdbRuntimeReport.toDetectorStatus(): DetectorStatus = when (stage) {
    AdbRuntimeStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
    AdbRuntimeStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
    AdbRuntimeStage.READY -> {
        val findings = findings()
        when {
            findings.isNotEmpty() -> DetectorStatus.danger()
            isAdbActive() -> DetectorStatus.warning()
            !probed -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}
