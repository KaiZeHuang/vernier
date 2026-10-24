package com.kaizehuang.vernier.features.playintegrityfix.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.playintegrityfix.presentation.model.PlayIntegrityFixCardModel
import com.kaizehuang.vernier.features.playintegrityfix.presentation.model.PlayIntegrityFixDetailRowModel

fun PlayIntegrityFixCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = listOf(
        ReportBlock.Rows("Properties", propertyRows.toReportRows()),
        ReportBlock.Rows("Consistency", consistencyRows.toReportRows()),
        ReportBlock.Rows("Native", nativeRows.toReportRows()),
        ReportBlock.Rows("Methods", methodRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
        ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }),
    ),
)

private fun List<PlayIntegrityFixDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
