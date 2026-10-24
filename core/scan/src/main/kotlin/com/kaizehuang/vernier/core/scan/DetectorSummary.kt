package com.kaizehuang.vernier.core.scan

import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

/**
 * What one detector publishes about its current scan.
 *
 * This is the only view of a detector that scan coordination, the overview and notifications
 * need, so none of them depend on a detector's own report or UI state.
 */
public data class DetectorSummary(
    val id: DetectorId,
    val title: String,
    val status: DetectorStatus,
    val headline: String,
    val summary: String,
    val ready: Boolean,
    /** Optional shorter text for the overview's findings; the detector card keeps its full detail. */
    val findingDetail: String? = null,
)

/** The summary a detector publishes for a card stating this headline; [ready] once a scan has finished. */
public fun DetectorHeadline.summarize(id: DetectorId, ready: Boolean): DetectorSummary = DetectorSummary(
    id = id,
    title = title,
    status = status,
    headline = verdict,
    summary = summary,
    ready = ready,
    findingDetail = findingDetail,
)
