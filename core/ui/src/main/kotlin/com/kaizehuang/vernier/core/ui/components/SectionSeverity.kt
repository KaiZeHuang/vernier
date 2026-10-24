package com.kaizehuang.vernier.core.ui.components

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind

/** The tag a folded evidence section shows on its header. */
public enum class SectionSeverity {
    HIGH,
    MEDIUM,

    /** A probe in the section failed, so its rows were not evaluated rather than found clean. */
    PROBE_ERROR,
}

/**
 * Danger takes precedence over Warning, and actionable evidence over a failed probe. A failed
 * probe still gets a tag: sections start folded, and an untagged header reads as clean.
 */
public fun highestSectionSeverity(statuses: Iterable<DetectorStatus>): SectionSeverity? {
    var warning = false
    var probeError = false
    for (status in statuses) {
        when (status.severity) {
            DetectionSeverity.DANGER -> return SectionSeverity.HIGH
            DetectionSeverity.WARNING -> warning = true
            DetectionSeverity.INFO -> if (status.infoKind == InfoKind.ERROR) probeError = true
            DetectionSeverity.ALL_CLEAR -> Unit
        }
    }
    return when {
        warning -> SectionSeverity.MEDIUM
        probeError -> SectionSeverity.PROBE_ERROR
        else -> null
    }
}

/** For sections whose tag is derived from the detector's own header facts, not from rows. */
public fun DetectionSeverity.toSectionSeverity(): SectionSeverity? = when (this) {
    DetectionSeverity.DANGER -> SectionSeverity.HIGH
    DetectionSeverity.WARNING -> SectionSeverity.MEDIUM
    DetectionSeverity.INFO, DetectionSeverity.ALL_CLEAR -> null
}

internal fun SectionSeverity.representativeStatus(): DetectorStatus = when (this) {
    SectionSeverity.HIGH -> DetectorStatus.danger()
    SectionSeverity.MEDIUM -> DetectorStatus.warning()
    SectionSeverity.PROBE_ERROR -> DetectorStatus.info(InfoKind.ERROR)
}
