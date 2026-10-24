package com.kaizehuang.vernier.features.su.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.su.presentation.model.SuCardModel
import com.kaizehuang.vernier.features.su.presentation.model.SuDetailRowModel

fun SuCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = listOf(
        ReportBlock.Rows("Artifacts", artifactRows.toReportRows()),
        ReportBlock.Rows("Context", contextRows.toReportRows()),
        ReportBlock.Rows("Methods", methodRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
        ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }),
    ),
)

private fun List<SuDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
