package com.kaizehuang.vernier.features.memory.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.memory.domain.MemoryReport
import com.kaizehuang.vernier.features.memory.domain.MemoryStage
import com.kaizehuang.vernier.features.memory.presentation.model.MemoryDetailRowModel

internal fun buildScanRows(report: MemoryReport): List<MemoryDetailRowModel> {
    return when (report.stage) {
        MemoryStage.LOADING -> placeholderRows(
            listOf(
                "Danger findings",
                "Review findings",
                "Modified functions",
                "Native library"
            ),
            DetectorStatus.info(InfoKind.SUPPORT),
            "Pending",
        )

        MemoryStage.FAILED -> placeholderRows(
            listOf(
                "Danger findings",
                "Review findings",
                "Modified functions",
                "Native library"
            ),
            DetectorStatus.info(InfoKind.ERROR),
            "Error",
        )

        MemoryStage.READY -> listOf(
            MemoryDetailRowModel(
                label = "Danger findings",
                value = if (report.nativeAvailable || report.dangerFindingCount > 0) {
                    report.dangerFindingCount.toString()
                } else {
                    "N/A"
                },
                status = when {
                    report.dangerFindingCount > 0 -> DetectorStatus.danger()
                    report.nativeAvailable -> DetectorStatus.allClear()
                    else -> DetectorStatus.info(InfoKind.SUPPORT)
                },
            ),
            MemoryDetailRowModel(
                label = "Review findings",
                value = if (report.nativeAvailable || report.reviewFindingCount > 0) {
                    report.reviewFindingCount.toString()
                } else {
                    "N/A"
                },
                status = when {
                    report.reviewFindingCount > 0 -> DetectorStatus.warning()
                    report.nativeAvailable -> DetectorStatus.allClear()
                    else -> DetectorStatus.info(InfoKind.SUPPORT)
                },
            ),
            MemoryDetailRowModel(
                label = "Modified functions",
                value = if (report.nativeAvailable || report.modifiedFunctionCount > 0) {
                    report.modifiedFunctionCount.toString()
                } else {
                    "N/A"
                },
                status = when {
                    report.modifiedFunctionCount > 0 -> DetectorStatus.warning()
                    report.nativeAvailable -> DetectorStatus.allClear()
                    else -> DetectorStatus.info(InfoKind.SUPPORT)
                },
            ),
            MemoryDetailRowModel(
                label = "Native library",
                value = if (report.nativeAvailable) "Loaded" else "Unavailable",
                status = if (report.nativeAvailable) DetectorStatus.allClear() else DetectorStatus.info(
                    InfoKind.ERROR
                ),
            ),
        )
    }
}

internal fun placeholderRows(
    labels: List<String>,
    status: DetectorStatus,
    value: String,
): List<MemoryDetailRowModel> {
    return labels.map { label ->
        MemoryDetailRowModel(
            label = label,
            value = value,
            status = status,
        )
    }
}
