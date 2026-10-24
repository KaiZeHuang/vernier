package com.kaizehuang.vernier.integration.dashboard

import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.scan.DetectorSummary
import com.kaizehuang.vernier.features.dashboard.presentation.export.DashboardExport
import com.kaizehuang.vernier.features.dashboard.presentation.export.ExportHeader
import com.kaizehuang.vernier.features.dashboard.presentation.model.buildDashboardFindings
import com.kaizehuang.vernier.features.dashboard.presentation.model.buildDashboardOverview
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoCardModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoHeaderFact
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoHeaderFactModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.toDeviceReport

/** One fully populated dashboard export built from generated card models of [detectors]. */
internal class ExportScenario(
    seed: Int,
    detectors: List<Detector<*, *>>,
    withScanTime: Boolean = false,
    minListSize: Int = 0,
) {

    private val reports: List<DetectorReport> = detectors
        .sortedBy { it.id.value }
        .map { detector -> CardFixtures(seed, minListSize, stream = detector.id.value).export(detector) }
    private val device = CardFixtures(seed, minListSize, stream = "device").create(DeviceInfoCardModel::class.java).let { generated ->
        val identity = when (seed % 3) {
            1 -> listOf(
                fact(DeviceInfoHeaderFact.BRAND, "Vernier"),
                fact(DeviceInfoHeaderFact.MODEL, "Pond 7"),
                fact(DeviceInfoHeaderFact.ANDROID, "17"),
                fact(DeviceInfoHeaderFact.SDK, "37"),
            )
            2 -> listOf(
                fact(DeviceInfoHeaderFact.BRAND, "Vernier"),
                fact(DeviceInfoHeaderFact.MODEL, "Pond 7"),
                fact(DeviceInfoHeaderFact.ANDROID, "17"),
            )
            else -> emptyList()
        }
        generated.copy(headerFacts = identity + generated.headerFacts)
    }
    private val summaries = reports.mapIndexed { index, report ->
        DetectorSummary(
            id = DetectorId("detector_$index"),
            title = report.title,
            status = DetectorStatus(report.severity, if (report.severity == DetectionSeverity.INFO) InfoKind.ERROR else null),
            headline = report.verdict,
            summary = "Summary for ${report.title}",
            ready = index % 5 != 4,
        )
    }
    private val overview = buildDashboardOverview(
        contributions = summaries,
        scanDurationMillis = if (withScanTime) 1_234L else null,
        scanCompletedAtEpochMillis = if (withScanTime) 1_700_000_000_000L else null,
    )
    private val findings = buildDashboardFindings(summaries)

    fun export(header: ExportHeader): DashboardExport = DashboardExport(
        header = header,
        overview = overview,
        topFindings = findings,
        detectors = reports,
        device = device.toDeviceReport(),
    )

    private fun fact(fact: DeviceInfoHeaderFact, value: String) = DeviceInfoHeaderFactModel(fact, value)
}
