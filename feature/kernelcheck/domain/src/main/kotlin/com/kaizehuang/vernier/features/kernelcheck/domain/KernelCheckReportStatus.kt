package com.kaizehuang.vernier.features.kernelcheck.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun KernelCheckReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        KernelCheckStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        KernelCheckStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        KernelCheckStage.READY -> when {
            hasHardIndicators -> DetectorStatus.danger()
            hasReviewInfoIndicators -> DetectorStatus.warning()
            hasInformationalCveState -> DetectorStatus.info(InfoKind.SUPPORT)
            cvePatchState == KernelCheckCvePatchState.INCONCLUSIVE -> DetectorStatus.info(
                InfoKind.SUPPORT
            )

            !nativeAvailable -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}
