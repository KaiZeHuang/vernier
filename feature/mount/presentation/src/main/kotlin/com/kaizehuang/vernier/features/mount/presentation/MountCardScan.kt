package com.kaizehuang.vernier.features.mount.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.mount.domain.MountFindingOrigin
import com.kaizehuang.vernier.features.mount.domain.MountFindingSeverity
import com.kaizehuang.vernier.features.mount.domain.MountReport
import com.kaizehuang.vernier.features.mount.domain.MountStage
import com.kaizehuang.vernier.features.mount.presentation.model.MountDetailRowModel

internal fun buildScanRows(report: MountReport): List<MountDetailRowModel> {
    return when (report.stage) {
        MountStage.LOADING -> placeholderRows(
            scanLabels(),
            "Pending",
            DetectorStatus.info(InfoKind.SUPPORT)
        )

        MountStage.FAILED -> placeholderRows(
            scanLabels(),
            "Error",
            DetectorStatus.info(InfoKind.ERROR)
        )

        MountStage.READY -> listOf(
            MountDetailRowModel(
                "Startup preload",
                preloadStateLabel(report),
                preloadStatus(report),
            ),
            MountDetailRowModel(
                "Preload context",
                preloadContextLabel(report),
                preloadContextStatus(report),
            ),
            MountDetailRowModel(
                "Preload findings",
                if (report.earlyPreloadAvailable) report.earlyPreloadFindingCount.toString() else "N/A",
                preloadStatus(report),
            ),
            MountDetailRowModel(
                "Mount entries",
                report.mountEntryCount.toString(),
                DetectorStatus.info(InfoKind.SUPPORT)
            ),
            MountDetailRowModel(
                "Mountinfo entries",
                report.mountInfoEntryCount.toString(),
                DetectorStatus.info(InfoKind.SUPPORT)
            ),
            MountDetailRowModel(
                "Map lines",
                report.mapLineCount.toString(),
                DetectorStatus.info(InfoKind.SUPPORT)
            ),
            MountDetailRowModel(
                "Permission denied",
                report.permissionDenied.toString(),
                when {
                    report.permissionDenied == 0 -> DetectorStatus.allClear()
                    else -> DetectorStatus.info(InfoKind.SUPPORT)
                },
            ),
            MountDetailRowModel(
                "Coverage",
                "${coveragePercent(report)}%",
                when {
                    report.permissionDenied == 0 -> DetectorStatus.allClear()
                    report.permissionDenied * 2 >= report.permissionTotal && report.permissionTotal > 0 -> DetectorStatus.warning()
                    else -> DetectorStatus.info(InfoKind.SUPPORT)
                },
            ),
            MountDetailRowModel(
                "Mounts readable",
                yesNo(report.mountsReadable),
                boolStatus(report.mountsReadable),
            ),
            MountDetailRowModel(
                "Mountinfo readable",
                yesNo(report.mountInfoReadable),
                boolStatus(report.mountInfoReadable),
            ),
            MountDetailRowModel(
                "Maps readable",
                yesNo(report.mapsReadable),
                boolStatus(report.mapsReadable),
            ),
            MountDetailRowModel(
                "statx support",
                yesNo(report.statxSupported),
                if (report.statxSupported) DetectorStatus.allClear() else DetectorStatus.info(
                    InfoKind.SUPPORT
                ),
            ),
        )
    }
}

private fun scanLabels(): List<String> = listOf(
    "Startup preload",
    "Preload context",
    "Preload findings",
    "Mount entries",
    "Mountinfo entries",
    "Map lines",
    "Permission denied",
    "Coverage",
    "Mounts readable",
    "Mountinfo readable",
    "Maps readable",
    "statx support",
)

private fun yesNo(value: Boolean): String = if (value) "Yes" else "No"

private fun boolStatus(value: Boolean): DetectorStatus {
    return if (value) DetectorStatus.allClear() else DetectorStatus.info(InfoKind.SUPPORT)
}

internal fun coveragePercent(report: MountReport): Int {
    return if (report.permissionTotal <= 0) {
        100
    } else {
        ((report.permissionAccessible.toDouble() / report.permissionTotal.toDouble()) * 100.0).toInt()
    }
}

private fun preloadStateLabel(report: MountReport): String {
    return when {
        !report.earlyPreloadAvailable -> "Unavailable"
        report.earlyPreloadDetected -> "Detected"
        else -> "Clean"
    }
}

private fun preloadContextLabel(report: MountReport): String {
    return when {
        !report.earlyPreloadAvailable -> "N/A"
        report.earlyPreloadContextValid -> "Fresh"
        else -> "Stale"
    }
}

private fun preloadStatus(report: MountReport): DetectorStatus {
    return when {
        !report.earlyPreloadAvailable -> DetectorStatus.info(InfoKind.SUPPORT)
        hasDangerPreloadEvidence(report) -> DetectorStatus.danger()
        hasWarningPreloadEvidence(report) -> DetectorStatus.warning()
        else -> DetectorStatus.allClear()
    }
}

private fun preloadContextStatus(report: MountReport): DetectorStatus {
    return when {
        !report.earlyPreloadAvailable -> DetectorStatus.info(InfoKind.SUPPORT)
        report.earlyPreloadContextValid -> DetectorStatus.allClear()
        else -> DetectorStatus.info(InfoKind.SUPPORT)
    }
}

private fun hasDangerPreloadEvidence(report: MountReport): Boolean {
    return report.findings.any { finding ->
        finding.origin != MountFindingOrigin.RUNTIME && finding.severity == MountFindingSeverity.DANGER
    }
}

private fun hasWarningPreloadEvidence(report: MountReport): Boolean {
    return report.findings.any { finding ->
        finding.origin == MountFindingOrigin.STARTUP_PRELOAD && finding.severity == MountFindingSeverity.WARNING
    }
}
