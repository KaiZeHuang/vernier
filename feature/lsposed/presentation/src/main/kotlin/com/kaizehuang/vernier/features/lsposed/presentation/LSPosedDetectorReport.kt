package com.kaizehuang.vernier.features.lsposed.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.lsposed.presentation.model.LSPosedCardModel
import com.kaizehuang.vernier.features.lsposed.presentation.model.LSPosedDetailRowModel

fun LSPosedCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = listOf(
        ReportBlock.Rows("Runtime", runtimeRows.toReportRows()),
        ReportBlock.Rows("Binder", binderRows.toReportRows()),
        ReportBlock.Rows("Package", packageRows.toReportRows()),
        ReportBlock.Rows("SELinux policy", policyRows.toReportRows()),
        ReportBlock.Rows("Native", nativeRows.toReportRows()),
        ReportBlock.Rows("Methods", methodRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
        ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }),
    ),
)

private fun List<LSPosedDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
