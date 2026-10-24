package com.kaizehuang.vernier.features.tee.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun TeeReport.toDetectorStatus(): DetectorStatus = when (verdict) {
    TeeVerdict.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
    TeeVerdict.CONSISTENT,
    TeeVerdict.SUSPICIOUS -> when {
        // Dashboard aggregates only TeeCardModel.status; consume reducer structure, not prose or row titles.
        // Dashboard 只聚合 TeeCardModel.status；这里消费 reducer 的结构化级别，不解析文案或行标题。
        supplementaryReviewLevel == TeeSignalLevel.FAIL -> DetectorStatus.danger()
        supplementaryReviewLevel == TeeSignalLevel.WARN -> DetectorStatus.warning()
        verdict == TeeVerdict.SUSPICIOUS -> DetectorStatus.warning()
        supplementaryIndicatorCount > 0 -> DetectorStatus.warning()
        !nativeProbesAvailable -> DetectorStatus.info(InfoKind.SUPPORT)
        else -> DetectorStatus.allClear()
    }
    TeeVerdict.TAMPERED, TeeVerdict.BROKEN -> DetectorStatus.danger()
    TeeVerdict.INCONCLUSIVE -> DetectorStatus.info(InfoKind.ERROR)
}
