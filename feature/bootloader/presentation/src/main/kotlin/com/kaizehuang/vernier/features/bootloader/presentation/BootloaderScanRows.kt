package com.kaizehuang.vernier.features.bootloader.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingGroup
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderReport
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderStage
import com.kaizehuang.vernier.features.bootloader.presentation.model.BootloaderDetailRowModel

internal fun buildScanRows(report: BootloaderReport): List<BootloaderDetailRowModel> {
    return when (report.stage) {
        BootloaderStage.LOADING -> placeholderRows(
            scanPlaceholders(),
            "Pending",
            DetectorStatus.info(InfoKind.SUPPORT)
        )

        BootloaderStage.FAILED -> placeholderRows(
            scanPlaceholders(),
            "Error",
            DetectorStatus.info(InfoKind.ERROR)
        )

        BootloaderStage.READY -> listOf(
            BootloaderDetailRowModel(
                label = "Properties checked",
                value = report.checkedPropertyCount.toString(),
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
            BootloaderDetailRowModel(
                label = "Properties observed",
                value = report.observedPropertyCount.toString(),
                status = if (report.observedPropertyCount > 0) DetectorStatus.allClear() else DetectorStatus.info(
                    InfoKind.SUPPORT
                ),
            ),
            BootloaderDetailRowModel(
                label = "Native hits",
                value = report.nativePropertyHitCount.toString(),
                status = if (report.nativePropertyHitCount > 0) DetectorStatus.allClear() else DetectorStatus.info(
                    InfoKind.SUPPORT
                ),
            ),
            BootloaderDetailRowModel(
                label = "Raw boot hits",
                value = report.rawBootParamHitCount.toString(),
                status = if (report.rawBootParamHitCount > 0) DetectorStatus.allClear() else DetectorStatus.info(
                    InfoKind.SUPPORT
                ),
            ),
            BootloaderDetailRowModel(
                label = "Source mismatches",
                value = report.sourceMismatchCount.toString(),
                status = if (report.sourceMismatchCount > 0) DetectorStatus.warning() else DetectorStatus.allClear(),
            ),
            BootloaderDetailRowModel(
                label = "Cross-checks",
                value = report.consistencyFindingCount.toString(),
                status = if (report.consistencyFindingCount > 0) {
                    if (report.dangerFindings.any { it.group == BootloaderFindingGroup.CONSISTENCY }) {
                        DetectorStatus.danger()
                    } else {
                        DetectorStatus.warning()
                    }
                } else {
                    DetectorStatus.allClear()
                },
            ),
            BootloaderDetailRowModel(
                label = "Attestation chain",
                value = report.attestationChainLength.toString(),
                status = when {
                    report.attestationChainLength == 0 -> DetectorStatus.danger()
                    report.attestationAvailable -> DetectorStatus.allClear()
                    else -> DetectorStatus.info(InfoKind.SUPPORT)
                },
            ),
            BootloaderDetailRowModel(
                label = "Hardware-backed",
                value = if (report.hardwareBacked) "Yes" else "No",
                status = if (report.hardwareBacked) DetectorStatus.allClear() else DetectorStatus.info(
                    InfoKind.SUPPORT
                ),
            ),
        )
    }
}

internal fun placeholderRows(
    labels: List<String>,
    value: String,
    status: DetectorStatus,
): List<BootloaderDetailRowModel> {
    return labels.map { label ->
        BootloaderDetailRowModel(
            label = label,
            value = value,
            status = status,
        )
    }
}

private fun scanPlaceholders(): List<String> = listOf(
    "Properties checked",
    "Properties observed",
    "Native hits",
    "Raw boot hits",
    "Source mismatches",
    "Cross-checks",
    "Attestation chain",
    "Hardware-backed",
)
