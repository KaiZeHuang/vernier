package com.kaizehuang.vernier.features.bootloader.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun BootloaderReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        BootloaderStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        BootloaderStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        BootloaderStage.READY -> when {
            dangerFindings.isNotEmpty() -> DetectorStatus.danger()
            warningFindings.isNotEmpty() -> DetectorStatus.warning()
            evidenceMode == BootloaderEvidenceMode.UNAVAILABLE -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}
