package com.kaizehuang.vernier.notifications

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewModel
import com.kaizehuang.vernier.features.dashboard.presentation.model.OverviewVerdict
import kotlin.math.roundToInt

data class ScanProgressNotificationSnapshot(
    val totalDetectorCount: Int,
    val readyDetectorCount: Int,
    val dashboardOverview: DashboardOverviewModel,
    val scanning: Boolean,
)

data class ScanProgressNotificationModel(
    val title: String,
    val text: String,
    val subText: String?,
    val shortCriticalText: String?,
    val progressPercent: Int,
)

class ScanProgressNotificationFormatter {

    fun format(snapshot: ScanProgressNotificationSnapshot): ScanProgressNotificationModel {
        val clampedTotal = snapshot.totalDetectorCount.coerceAtLeast(1)
        val clampedReady = snapshot.readyDetectorCount.coerceIn(0, clampedTotal)
        val progressPercent = ((clampedReady * 100f) / clampedTotal)
            .roundToInt()
            .coerceIn(0, 100)
        val overview = snapshot.dashboardOverview
        return if (snapshot.scanning) {
            ScanProgressNotificationModel(
                title = "Scanning $clampedReady/$clampedTotal",
                text = "${overview.headline} \u00b7 ${overview.summary}",
                subText = "Vernier",
                shortCriticalText = "$clampedReady/$clampedTotal",
                progressPercent = progressPercent,
            )
        } else {
            ScanProgressNotificationModel(
                title = overview.headline,
                text = overview.summary,
                subText = overview.title.takeIf { overview.titleDescribesCompletedScan },
                shortCriticalText = shortCriticalTextFor(overview),
                progressPercent = progressPercent,
            )
        }
    }

    private fun shortCriticalTextFor(
        overview: DashboardOverviewModel,
    ): String? {
        return when (overview.verdict) {
            OverviewVerdict.DANGER,
            OverviewVerdict.WARNING,
            OverviewVerdict.INFO,
            OverviewVerdict.OK -> overview.headline

            OverviewVerdict.READY,
            OverviewVerdict.PENDING -> null
        }
    }
}
