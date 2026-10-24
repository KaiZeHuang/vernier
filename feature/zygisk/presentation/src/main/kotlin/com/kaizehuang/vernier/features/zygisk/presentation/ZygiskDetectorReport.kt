package com.kaizehuang.vernier.features.zygisk.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.zygisk.presentation.model.ZygiskCardModel
import com.kaizehuang.vernier.features.zygisk.presentation.model.ZygiskDetailRowModel

fun ZygiskCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = buildList {
        add(ReportBlock.Rows("State", stateRows.toReportRows()))
        add(ReportBlock.Rows("Signals", signalRows.toReportRows()))
        add(ReportBlock.Rows("Methods", methodRows.toReportRows()))
        add(ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }))
        if (references.isNotEmpty()) {
            add(ReportBlock.Verbatim("References", references.map { "    • $it" }))
        }
    },
)

private fun List<ZygiskDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
