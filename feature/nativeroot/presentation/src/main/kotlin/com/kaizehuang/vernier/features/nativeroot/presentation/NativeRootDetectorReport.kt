package com.kaizehuang.vernier.features.nativeroot.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.nativeroot.presentation.model.NativeRootCardModel
import com.kaizehuang.vernier.features.nativeroot.presentation.model.NativeRootDetailRowModel

fun NativeRootCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = listOf(
        ReportBlock.Rows("Native", nativeRows.toReportRows()),
        ReportBlock.Rows("Runtime", runtimeRows.toReportRows()),
        ReportBlock.Rows("Kernel", kernelRows.toReportRows()),
        ReportBlock.Rows("Properties", propertyRows.toReportRows()),
        ReportBlock.Rows("Methods", methodRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
        ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }),
    ),
)

private fun List<NativeRootDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
