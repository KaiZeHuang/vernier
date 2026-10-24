package com.kaizehuang.vernier.features.kernelcheck.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.kernelcheck.presentation.model.KernelCheckCardModel
import com.kaizehuang.vernier.features.kernelcheck.presentation.model.KernelCheckDetailRowModel

fun KernelCheckCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = listOf(
        ReportBlock.Rows("Identity", identityRows.toReportRows()),
        ReportBlock.Rows("Anomalies", anomalyRows.toReportRows()),
        ReportBlock.Rows("Behavior", behaviorRows.toReportRows()),
        ReportBlock.Rows("Methods", methodRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
        ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }),
    ),
)

private fun List<KernelCheckDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
