package com.kaizehuang.vernier.features.selinux.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxCardModel
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxDetailRowModel

fun SelinuxCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = buildList {
        add(ReportBlock.Rows("State", stateRows.toReportRows()))
        add(ReportBlock.Rows("Policy", policyRows.toReportRows()))
        add(ReportBlock.Rows("Audit", auditRows.toReportRows()))
        add(ReportBlock.Rows("Device", deviceRows.toReportRows()))
        add(ReportBlock.Rows("Methods", methodRows.toReportRows()))
        add(ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }))
        add(ReportBlock.Bullets("Policy Notes", policyNotes.map { it.text }))
        add(ReportBlock.Bullets("Audit Notes", auditNotes.map { it.text }))
        if (references.isNotEmpty()) {
            add(ReportBlock.Verbatim("References", references.map { "    • $it" }))
        }
    },
)

private fun List<SelinuxDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
