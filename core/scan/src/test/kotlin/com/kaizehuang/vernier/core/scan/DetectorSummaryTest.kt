package com.kaizehuang.vernier.core.scan

import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.core.report.DetectorHeadline
import org.junit.Assert.assertEquals
import org.junit.Test

class DetectorSummaryTest {

    @Test
    fun `summarizes a headline field by field`() {
        val headline = Headline(findingDetail = "short")

        assertEquals(
            DetectorSummary(
                id = DetectorId("probe"),
                title = "Probe",
                status = DetectorStatus.info(InfoKind.SUPPORT),
                headline = "Verdict",
                summary = "Summary",
                ready = true,
                findingDetail = "short",
            ),
            headline.summarize(DetectorId("probe"), ready = true),
        )
    }

    @Test
    fun `a headline without a finding detail summarizes without one`() {
        val summary = object : DetectorHeadline {
            override val title = "Probe"
            override val status = DetectorStatus.allClear()
            override val verdict = "Clean"
            override val summary = "Nothing found"
        }.summarize(DetectorId("probe"), ready = false)

        assertEquals(null, summary.findingDetail)
        assertEquals(false, summary.ready)
    }

    private data class Headline(override val findingDetail: String?) : DetectorHeadline {
        override val title = "Probe"
        override val status = DetectorStatus.info(InfoKind.SUPPORT)
        override val verdict = "Verdict"
        override val summary = "Summary"
    }
}
