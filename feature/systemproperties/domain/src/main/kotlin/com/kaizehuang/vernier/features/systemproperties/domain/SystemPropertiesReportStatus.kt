package com.kaizehuang.vernier.features.systemproperties.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun SystemPropertiesReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        SystemPropertiesStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        SystemPropertiesStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        SystemPropertiesStage.READY -> when {
            hasDangerSignals -> DetectorStatus.danger()
            hasWarningSignals -> DetectorStatus.warning()
            hasReducedCoverage() -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}

fun SystemPropertiesReport.hasReducedCoverage(): Boolean {
    return !propAreaAvailable
}
