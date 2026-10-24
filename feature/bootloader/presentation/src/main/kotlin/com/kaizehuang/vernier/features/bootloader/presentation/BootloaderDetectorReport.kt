package com.kaizehuang.vernier.features.bootloader.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.core.report.ReportRow
import com.kaizehuang.vernier.features.bootloader.presentation.model.BootloaderCardModel
import com.kaizehuang.vernier.features.bootloader.presentation.model.BootloaderDetailRowModel

fun BootloaderCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = listOf(
        ReportBlock.Rows("State", stateRows.toReportRows()),
        ReportBlock.Rows("Attestation", attestationRows.toReportRows()),
        ReportBlock.Rows("Properties", propertyRows.toReportRows()),
        ReportBlock.Rows("Consistency", consistencyRows.toReportRows()),
        ReportBlock.Rows("Methods", methodRows.toReportRows()),
        ReportBlock.Rows("Scan", scanRows.toReportRows()),
        ReportBlock.Bullets("Impact & Guidance", impactItems.map { it.text }),
    ),
)

private fun List<BootloaderDetailRowModel>.toReportRows(): List<ReportRow> =
    map { ReportRow(it.label, it.value, it.detail) }
