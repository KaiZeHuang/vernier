package com.kaizehuang.vernier.features.virtualization.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.virtualization.presentation.model.VirtualizationCardModel
import com.kaizehuang.vernier.features.virtualization.presentation.model.VirtualizationDetailRowModel

fun VirtualizationCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = buildList {
        add(ReportBlock.Rows("Environment", environmentRows.toReportRows()))
        add(ReportBlock.Rows("Runtime", runtimeRows.toReportRows()))
        add(ReportBlock.Rows("Consistency", consistencyRows.toReportRows()))
        add(ReportBlock.Rows("Honeypot", honeypotRows.toReportRows()))
        add(ReportBlock.Rows("Host apps", hostAppRows.toReportRows()))
        add(ReportBlock.Rows("Methods", methodRows.toReportRows()))
        add(ReportBlock.Rows("Scan", scanRows.toReportRows()))
        add(ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }))
        if (references.isNotEmpty()) {
            add(ReportBlock.Verbatim("References", references.map { "    • $it" }))
        }
    },
)

private fun List<VirtualizationDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
