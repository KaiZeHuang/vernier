package com.kaizehuang.vernier.features.rootmanagers.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.rootmanagers.presentation.model.RootManagersCardModel
import com.kaizehuang.vernier.features.rootmanagers.presentation.model.RootManagersDetailRowModel

fun RootManagersCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = emptyList(),
    blocks = listOf(
        ReportBlock.Rows("Matched root managers", entryRows.toReportRows()),
        ReportBlock.Rows("Profiles", profileRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
    ),
)

private fun List<RootManagersDetailRowModel>.toReportRows(): List<ReportRow> =
    map { row -> ReportRow(row.label, row.value, row.detail) }
