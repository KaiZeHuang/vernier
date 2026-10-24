package com.kaizehuang.vernier.features.tee.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.features.tee.presentation.model.TeeCardModel
import com.kaizehuang.vernier.features.tee.presentation.model.TeeFactRowModel

fun TeeCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = buildList {
        if (highlightSignals.isNotEmpty()) {
            add(ReportBlock.Verbatim("Highlight Signals", highlightSignals.map { "    • ${it.label}: ${it.value}" }))
        }
        factGroups.forEach { group ->
            add(ReportBlock.Verbatim(group.title, group.rows.flatMap(TeeFactRowModel::toReportLines)))
        }
        add(
            ReportBlock.Verbatim(
                title = "Environment & Network",
                lines = buildList {
                    add("    • Network Status    : ${networkState.summary}")
                    add("    • Certificate Count : ${certificateSummary.count}")
                    certificateSummary.certificates.forEachIndexed { index, certificate ->
                        add("    • Certificate ${index + 1}       : ${certificate.slotLabel}")
                        add("        Subject           : ${certificate.subject}")
                        add("        Issuer            : ${certificate.issuer}")
                        add("        Serial Number     : ${certificate.serialNumber}")
                        add("        Validity          : ${certificate.validFrom} to ${certificate.validUntil}")
                        add("        Signature          : ${certificate.signatureAlgorithm}")
                        add("        Public Key        : ${certificate.publicKeySummary}")
                    }
                },
            ),
        )
        if (exportText.isNotBlank()) {
            add(ReportBlock.Verbatim("TEE Detailed Export", exportText.trimEnd().lines().map { "    $it" }))
        }
    },
)

private fun TeeFactRowModel.toReportLines(): List<String> {
    val lines = value.trim().lines().map { it.trimEnd() }.filter { it.isNotBlank() }
    return if (lines.size <= 1) {
        listOf("    • $label: ${value.trim()}")
    } else {
        listOf("    • $label:") + lines.map { "        $it" }
    }
}
