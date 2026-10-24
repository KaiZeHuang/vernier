package com.kaizehuang.vernier.features.su.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.su.domain.SuReport
import com.kaizehuang.vernier.features.su.domain.SuStage
import com.kaizehuang.vernier.features.su.presentation.model.SuDetailRowModel

internal fun buildArtifactRows(report: SuReport): List<SuDetailRowModel> {
    return when (report.stage) {
        SuStage.LOADING -> listOf(
            SuDetailRowModel(
                label = "Root daemons",
                value = "Pending",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
            SuDetailRowModel(
                label = "SU binaries",
                value = "Pending",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
        )

        SuStage.FAILED -> listOf(
            SuDetailRowModel(
                label = "Root daemons",
                value = "Error",
                status = DetectorStatus.info(InfoKind.ERROR),
                detail = report.errorMessage,
            ),
            SuDetailRowModel(
                label = "SU binaries",
                value = "Error",
                status = DetectorStatus.info(InfoKind.ERROR),
                detail = report.errorMessage,
            ),
        )

        SuStage.READY -> listOf(
            SuDetailRowModel(
                label = "Root daemons",
                value = if (report.daemons.isEmpty()) "None" else daemonNames(report),
                status = if (report.daemons.isEmpty()) DetectorStatus.allClear() else DetectorStatus.danger(),
                detail = report.daemons
                    .joinToString(separator = "\n") { finding -> "${finding.name}: ${finding.path}" }
                    .ifBlank { null },
                detailMonospace = true,
            ),
            SuDetailRowModel(
                label = "SU binaries",
                value = if (report.suBinaries.isEmpty()) "None" else report.suBinaries.size.toString(),
                status = if (report.suBinaries.isEmpty()) DetectorStatus.allClear() else DetectorStatus.danger(),
                detail = report.suBinaries.joinToString(separator = "\n").ifBlank { null },
                detailMonospace = true,
            ),
        )
    }
}

internal fun buildContextRows(report: SuReport): List<SuDetailRowModel> {
    return when (report.stage) {
        SuStage.LOADING -> listOf(
            SuDetailRowModel(
                label = "Self context",
                value = "Pending",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
            SuDetailRowModel(
                label = "Suspicious processes",
                value = "Pending",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
            SuDetailRowModel(
                label = "Probe path",
                value = "Loading",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
        )

        SuStage.FAILED -> listOf(
            SuDetailRowModel(
                label = "Self context",
                value = "Error",
                status = DetectorStatus.info(InfoKind.ERROR),
                detail = report.errorMessage,
            ),
            SuDetailRowModel(
                label = "Suspicious processes",
                value = "Error",
                status = DetectorStatus.info(InfoKind.ERROR),
                detail = report.errorMessage,
            ),
            SuDetailRowModel(
                label = "Probe path",
                value = "Unavailable",
                status = DetectorStatus.info(InfoKind.ERROR),
                detail = report.errorMessage,
            ),
        )

        SuStage.READY -> listOf(
            SuDetailRowModel(
                label = "Self context",
                value = when {
                    report.selfContextAbnormal -> "Abnormal"
                    report.selfContext.isNotBlank() -> "Normal"
                    else -> "Unknown"
                },
                status = when {
                    report.selfContextAbnormal -> DetectorStatus.danger()
                    report.selfContext.isNotBlank() -> DetectorStatus.allClear()
                    else -> DetectorStatus.info(InfoKind.SUPPORT)
                },
                detail = report.selfContext.ifBlank {
                    "No SELinux context could be read from the current app process."
                },
                detailMonospace = report.selfContext.isNotBlank(),
            ),
            SuDetailRowModel(
                label = "Suspicious processes",
                value = when {
                    !report.nativeAvailable -> "Unavailable"
                    report.suspiciousProcesses.isEmpty() -> "None"
                    else -> report.suspiciousProcesses.size.toString()
                },
                status = when {
                    !report.nativeAvailable -> DetectorStatus.info(InfoKind.SUPPORT)
                    report.suspiciousProcesses.isEmpty() -> DetectorStatus.allClear()
                    else -> DetectorStatus.danger()
                },
                detail = report.suspiciousProcesses.joinToString(separator = "\n").ifBlank {
                    if (report.nativeAvailable) {
                        "No suspicious process contexts matched root-related tokens."
                    } else {
                        "Native /proc process enumeration was unavailable on this build."
                    }
                },
                detailMonospace = true,
            ),
            SuDetailRowModel(
                label = "Probe path",
                value = when {
                    report.nativeAvailable -> "JNI syscall scan"
                    report.selfContext.isNotBlank() -> "Fallback self read"
                    else -> "Unavailable"
                },
                status = when {
                    report.nativeAvailable -> DetectorStatus.allClear()
                    report.selfContext.isNotBlank() -> DetectorStatus.info(InfoKind.SUPPORT)
                    else -> DetectorStatus.info(InfoKind.ERROR)
                },
                detail = if (report.nativeAvailable) {
                    "Checked ${report.checkedProcessCount} process contexts; ${report.deniedProcessCount} /proc reads were denied. Denied reads are kept as supporting visibility evidence, not direct root-process proof."
                } else {
                    "Native library was unavailable, so only /proc/self/attr/current fallback could run."
                },
            ),
        )
    }
}

internal fun daemonNames(report: SuReport): String {
    val names = report.daemons.map { it.name }.distinct()
    return when {
        names.isEmpty() -> "None"
        names.size <= 2 -> names.joinToString("/")
        else -> names.take(2).joinToString("/") + " +${names.size - 2}"
    }
}
