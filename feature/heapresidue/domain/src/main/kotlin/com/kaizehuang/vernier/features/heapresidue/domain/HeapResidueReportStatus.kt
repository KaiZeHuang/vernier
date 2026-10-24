package com.kaizehuang.vernier.features.heapresidue.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

/** Even a complete dump has unknown historical coverage; a negative scan is never all-clear. */
fun HeapResidueReport.toDetectorStatus(): DetectorStatus = when {
    stage == HeapResidueStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
    stage == HeapResidueStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
    outcome == HeapResidueOutcome.OBSERVED && signals.isNotEmpty() -> DetectorStatus.warning()
    outcome == HeapResidueOutcome.UNAVAILABLE -> DetectorStatus.info(InfoKind.ERROR)
    else -> DetectorStatus.info(InfoKind.SUPPORT)
}

/** Snapshot completion and candidate visibility are required even for the narrow not-observed state. */
fun heapResidueOutcome(complete: Boolean, candidates: Int, signals: List<HeapResidueSignal>): HeapResidueOutcome = when {
    !complete -> HeapResidueOutcome.UNAVAILABLE
    signals.isNotEmpty() -> HeapResidueOutcome.OBSERVED
    candidates == 0 -> HeapResidueOutcome.INCONCLUSIVE
    else -> HeapResidueOutcome.NOT_OBSERVED
}
