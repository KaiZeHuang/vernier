package com.kaizehuang.vernier.features.adbruntime.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.adbruntime.presentation.model.AdbRuntimeCardModel
import com.kaizehuang.vernier.features.adbruntime.presentation.model.AdbRuntimeDetailRowModel

fun AdbRuntimeCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = emptyList(),
    blocks = listOf(
        ReportBlock.Rows("Inconsistencies", signalRows.toReportRows()),
        ReportBlock.Rows("Sources", scanRows.toReportRows()),
    ),
)

private fun List<AdbRuntimeDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
