package com.kaizehuang.vernier.notifications

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewMetric
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewMetricModel
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewModel
import com.kaizehuang.vernier.features.dashboard.presentation.model.OverviewVerdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScanProgressNotificationFormatterTest {

    private val formatter = ScanProgressNotificationFormatter()

    @Test
    fun `scanning snapshot uses progress title and dashboard summary`() {
        val model = formatter.format(
            ScanProgressNotificationSnapshot(
                totalDetectorCount = 15,
                readyDetectorCount = 6,
                dashboardOverview = overview(
                    title = "Security overview",
                    headline = "Danger",
                    summary = "Start with Mount and TEE.",
                    verdict = OverviewVerdict.DANGER,
                    completedScan = false,
                ),
                scanning = true,
            ),
        )

        assertEquals("Scanning 6/15", model.title)
        assertEquals("Danger · Start with Mount and TEE.", model.text)
        assertEquals("6/15", model.shortCriticalText)
        assertEquals(40, model.progressPercent)
    }

    @Test
    fun `finished snapshot reuses dashboard headline and summary`() {
        val model = formatter.format(
            ScanProgressNotificationSnapshot(
                totalDetectorCount = 15,
                readyDetectorCount = 15,
                dashboardOverview = overview(
                    title = "Scan time 4.2s",
                    headline = "OK",
                    summary = "Use the detector cards below to inspect local evidence in detail.",
                    verdict = OverviewVerdict.OK,
                    completedScan = true,
                ),
                scanning = false,
            ),
        )

        assertEquals("OK", model.title)
        assertEquals(
            "Use the detector cards below to inspect local evidence in detail.",
            model.text,
        )
        assertEquals("Scan time 4.2s", model.subText)
        assertEquals("OK", model.shortCriticalText)
        assertEquals(100, model.progressPercent)
    }

    @Test
    fun `pending verdicts carry no short critical text`() {
        listOf(OverviewVerdict.READY to "Ready", OverviewVerdict.PENDING to "Pending").forEach { (verdict, headline) ->
            val model = formatter.format(
                ScanProgressNotificationSnapshot(
                    totalDetectorCount = 15,
                    readyDetectorCount = 15,
                    dashboardOverview = overview(
                        title = "Security overview",
                        headline = headline,
                        summary = "summary",
                        verdict = verdict,
                        completedScan = false,
                    ),
                    scanning = false,
                ),
            )

            assertNull(model.shortCriticalText)
            assertNull(model.subText)
        }
    }

    @Test
    fun `sub text follows the completed scan flag rather than the title wording`() {
        val model = formatter.format(
            ScanProgressNotificationSnapshot(
                totalDetectorCount = 15,
                readyDetectorCount = 15,
                dashboardOverview = overview(
                    title = "Any reworded overview title",
                    headline = "Warning",
                    summary = "summary",
                    verdict = OverviewVerdict.WARNING,
                    completedScan = false,
                ),
                scanning = false,
            ),
        )

        assertNull(model.subText)
        assertEquals("Warning", model.shortCriticalText)
    }

    private fun overview(
        title: String,
        headline: String,
        summary: String,
        verdict: OverviewVerdict,
        completedScan: Boolean,
    ) = DashboardOverviewModel(
        title = title,
        headline = headline,
        summary = summary,
        status = DetectorStatus.allClear(),
        verdict = verdict,
        titleDescribesCompletedScan = completedScan,
        metrics = listOf(
            DashboardOverviewMetricModel(
                metric = DashboardOverviewMetric.READY,
                value = "15",
                status = DetectorStatus.allClear(),
            ),
        ),
    )
}
