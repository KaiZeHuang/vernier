package com.kaizehuang.vernier.features.mount.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.mount.presentation.model.MountCardModel
import com.kaizehuang.vernier.features.mount.presentation.model.MountDetailRowModel

fun MountCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = listOf(
        ReportBlock.Rows("Artifacts", artifactRows.toReportRows()),
        ReportBlock.Rows("Runtime", runtimeRows.toReportRows()),
        ReportBlock.Rows("Filesystem", filesystemRows.toReportRows()),
        ReportBlock.Rows("Consistency", consistencyRows.toReportRows()),
        ReportBlock.Rows("Methods", methodRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
        ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }),
    ),
)

private fun List<MountDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
