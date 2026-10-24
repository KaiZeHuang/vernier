package com.kaizehuang.vernier.features.mount.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.mount.domain.MountFinding
import com.kaizehuang.vernier.features.mount.domain.MountFindingSeverity
import com.kaizehuang.vernier.features.mount.domain.MountMethodOutcome
import com.kaizehuang.vernier.features.mount.domain.MountMethodResult
import com.kaizehuang.vernier.features.mount.domain.MountReport
import com.kaizehuang.vernier.features.mount.domain.MountStage
import com.kaizehuang.vernier.features.mount.presentation.model.MountDetailRowModel

internal fun buildRows(
    stage: MountStage,
    rows: List<MountFinding>,
    placeholders: List<String>,
): List<MountDetailRowModel> {
    return when (stage) {
        MountStage.LOADING -> placeholderRows(
            placeholders,
            "Pending",
            DetectorStatus.info(InfoKind.SUPPORT)
        )

        MountStage.FAILED -> placeholderRows(
            placeholders,
            "Error",
            DetectorStatus.info(InfoKind.ERROR)
        )

        MountStage.READY -> if (rows.isEmpty()) {
            listOf(
                MountDetailRowModel(
                    label = "Status",
                    value = "Clean",
                    status = DetectorStatus.allClear(),
                    detail = "No findings were produced for this section.",
                ),
            )
        } else {
            rows.map(::findingRow)
        }
    }
}

private fun findingRow(finding: MountFinding): MountDetailRowModel {
    return MountDetailRowModel(
        label = finding.label,
        value = badgeValue(finding.value),
        status = severityStatus(finding.severity),
        detail = finding.detail,
        detailMonospace = finding.detailMonospace,
    )
}

private fun badgeValue(value: String): String {
    return if (value.length > 18) value.take(17) + "…" else value
}

internal fun placeholderRows(
    labels: List<String>,
    value: String,
    status: DetectorStatus
): List<MountDetailRowModel> {
    return labels.map { label ->
        MountDetailRowModel(
            label = label,
            value = value,
            status = status,
        )
    }
}

internal fun severityStatus(severity: MountFindingSeverity): DetectorStatus {
    return when (severity) {
        MountFindingSeverity.SAFE -> DetectorStatus.allClear()
        MountFindingSeverity.WARNING -> DetectorStatus.warning()
        MountFindingSeverity.DANGER -> DetectorStatus.danger()
        MountFindingSeverity.INFO -> DetectorStatus.info(InfoKind.SUPPORT)
    }
}

internal fun buildMethodRows(report: MountReport): List<MountDetailRowModel> {
    return when (report.stage) {
        MountStage.LOADING -> placeholderRows(
            methodLabels(),
            "Pending",
            DetectorStatus.info(InfoKind.SUPPORT)
        )

        MountStage.FAILED -> placeholderRows(
            methodLabels(),
            "Failed",
            DetectorStatus.info(InfoKind.ERROR)
        )

        MountStage.READY -> report.methods.map { result ->
            MountDetailRowModel(
                label = result.label,
                value = result.summary,
                status = methodStatus(result),
                detail = result.detail,
                detailMonospace = true,
            )
        }
    }
}

private fun methodLabels(): List<String> = listOf(
    "Startup preload",
    "Zygote next mount view",
    "Path probes",
    "Shell tmp view",
    "/proc/self/mounts",
    "/proc/self/maps",
    "/proc/self/mountinfo",
    "Filesystem probes",
    "statx cross-check",
)

private fun methodStatus(result: MountMethodResult): DetectorStatus {
    return when (result.outcome) {
        MountMethodOutcome.CLEAN -> DetectorStatus.allClear()
        MountMethodOutcome.WARNING -> DetectorStatus.warning()
        MountMethodOutcome.DANGER -> DetectorStatus.danger()
        MountMethodOutcome.SUPPORT -> DetectorStatus.info(InfoKind.SUPPORT)
    }
}
