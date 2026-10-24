package com.kaizehuang.vernier.features.heapresidue.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.heapresidue.presentation.model.HeapResidueCardModel
import com.kaizehuang.vernier.features.heapresidue.presentation.model.HeapResidueDetailRowModel

fun HeapResidueCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = emptyList(),
    blocks = listOf(
        ReportBlock.Rows("Signals", signalRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
    ),
)

private fun List<HeapResidueDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
