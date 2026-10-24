package com.kaizehuang.vernier.core.report

import com.kaizehuang.vernier.core.evidence.DetectorStatus

/**
 * What the top of a detector's card states. The dashboard summarizes a detector from it, and a
 * headless run takes the detector's verdict from [status].
 */
public interface DetectorHeadline {
    public val title: String

    /** The detector's verdict on the described report. */
    public val status: DetectorStatus

    public val verdict: String

    public val summary: String

    /** Shorter text for the dashboard's top findings; the card keeps its full detail. */
    public val findingDetail: String?
        get() = null
}
