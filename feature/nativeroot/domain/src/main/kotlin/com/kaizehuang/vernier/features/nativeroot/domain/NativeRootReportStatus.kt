package com.kaizehuang.vernier.features.nativeroot.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

fun NativeRootReport.toDetectorStatus(): DetectorStatus {
    return when (stage) {
        NativeRootStage.LOADING -> DetectorStatus.info(InfoKind.SUPPORT)
        NativeRootStage.FAILED -> DetectorStatus.info(InfoKind.ERROR)
        NativeRootStage.READY -> when {
            hasDangerFindings -> DetectorStatus.danger()
            hasWarningFindings -> DetectorStatus.warning()
            !nativeAvailable -> DetectorStatus.info(InfoKind.SUPPORT)
            hasReducedCoverage() -> DetectorStatus.info(InfoKind.SUPPORT)
            else -> DetectorStatus.allClear()
        }
    }
}

fun NativeRootReport.hasReducedCoverage(): Boolean {
    return srcuTiming.reducesCoverage() || ksuSupercallBlocked ||
            !ksuSupercallAttempted ||
            hasRuntimeReducedCoverage()
}

// The timing experiment is opt-in. An Android or kernel it never applies to is not a gap in this
// detector's coverage, so only an applicable run that failed or stayed inconclusive counts.
private fun SrcuTimingObservation.reducesCoverage(): Boolean = when (collection) {
    SrcuTimingCollection.NOT_REQUESTED,
    SrcuTimingCollection.UNSUPPORTED_ANDROID,
    SrcuTimingCollection.UNSUPPORTED_KERNEL -> false
    else -> analysis.verdict == SrcuTimingVerdict.NOT_EVALUATED ||
        analysis.verdict == SrcuTimingVerdict.INCONCLUSIVE
}

fun NativeRootReport.hasRuntimeReducedCoverage(): Boolean {
    return !cgroupAvailable ||
            pathDeniedCount > 0 ||
            !isolatedMountProbeAvailable ||
            !ksuThroneHuntAvailable ||
            !ksuThroneHuntStimulusApplied ||
            ksuManagerVisibilityRestricted ||
            ksuManagerVisibilityUnknown
}
