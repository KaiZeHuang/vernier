package com.kaizehuang.vernier.features.systemproperties.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.systemproperties.presentation.model.SystemPropertiesCardModel
import com.kaizehuang.vernier.features.systemproperties.presentation.model.SystemPropertiesDetailRowModel

fun SystemPropertiesCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = listOf(
        ReportBlock.Rows("Core", coreRows.toReportRows()),
        ReportBlock.Rows("Boot", bootRows.toReportRows()),
        ReportBlock.Rows("Build", buildRows.toReportRows()),
        ReportBlock.Rows("Source", sourceRows.toReportRows()),
        ReportBlock.Rows("Consistency", consistencyRows.toReportRows()),
        ReportBlock.Rows("Info", infoRows.toReportRows()),
        ReportBlock.Rows("Methods", methodRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
        ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }),
    ),
)

private fun List<SystemPropertiesDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
