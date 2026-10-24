package com.kaizehuang.vernier.features.zygisk.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskMethod
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskReport
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskSignal
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskSignalGroup
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskSignalSeverity
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskStage
import com.kaizehuang.vernier.features.zygisk.presentation.model.ZygiskDetailRowModel
import com.kaizehuang.vernier.features.zygisk.presentation.model.ZygiskRowIcon

internal fun buildSignalRows(
    report: ZygiskReport,
): List<ZygiskDetailRowModel> {
    return when (report.stage) {
        ZygiskStage.LOADING -> placeholderSignalRows(DetectorStatus.info(InfoKind.SUPPORT), "Pending")

        ZygiskStage.FAILED -> placeholderSignalRows(DetectorStatus.info(InfoKind.ERROR), "Error")

        ZygiskStage.READY -> {
            if (report.signals.isEmpty()) {
                listOf(
                    ZygiskDetailRowModel(
                        label = "Signals",
                        value = if (report.fullyClean) "Clean" else "Unavailable",
                        status = if (report.fullyClean) {
                            DetectorStatus.allClear()
                        } else {
                            DetectorStatus.info(InfoKind.SUPPORT)
                        },
                        detail = if (report.fullyClean) {
                            "No positive runtime signal surfaced in the current process."
                        } else {
                            "No positive signal surfaced, but a major scan path was unavailable so the result is support-only."
                        },
                    ),
                )
            } else {
                report.signals.map(::signalRow)
            }
        }
    }
}

private fun signalRow(
    signal: ZygiskSignal,
): ZygiskDetailRowModel {
    return ZygiskDetailRowModel(
        label = signal.label,
        value = signal.value,
        status = when (signal.severity) {
            ZygiskSignalSeverity.DANGER -> DetectorStatus.danger()
            ZygiskSignalSeverity.WARNING -> DetectorStatus.warning()
        },
        detail = signal.detail,
        detailMonospace = signal.detailMonospace,
        icon = signal.group.rowIcon(),
    )
}

private fun placeholderSignalRows(
    status: DetectorStatus,
    value: String,
): List<ZygiskDetailRowModel> {
    return ZygiskSignalGroup.entries.map { group ->
        ZygiskDetailRowModel(label = group.placeholderLabel(), value = value, status = status, icon = group.rowIcon())
    }
}

private fun ZygiskSignalGroup.placeholderLabel(): String = when (this) {
    ZygiskSignalGroup.CROSS_PROCESS -> "Cross-process"
    ZygiskSignalGroup.RUNTIME -> "Runtime"
    ZygiskSignalGroup.LINKER -> "Linker"
    ZygiskSignalGroup.MAPS -> "Maps"
    ZygiskSignalGroup.HEAP -> "Heap"
    ZygiskSignalGroup.THREADS -> "Threads"
    ZygiskSignalGroup.FD -> "FDs"
}

internal fun ZygiskSignalGroup.rowIcon(): ZygiskRowIcon? = when (this) {
    ZygiskSignalGroup.CROSS_PROCESS -> ZygiskRowIcon.CROSS_PROCESS
    ZygiskSignalGroup.LINKER -> ZygiskRowIcon.LINKER
    ZygiskSignalGroup.MAPS,
    ZygiskSignalGroup.HEAP -> ZygiskRowIcon.MEMORY
    ZygiskSignalGroup.RUNTIME,
    ZygiskSignalGroup.THREADS,
    ZygiskSignalGroup.FD -> null
}

internal fun ZygiskMethod.rowIcon(): ZygiskRowIcon? = when (this) {
    ZygiskMethod.CROSS_PROCESS_FD_TRAP -> ZygiskRowIcon.CROSS_PROCESS
    ZygiskMethod.LINKER_AND_NAMESPACE -> ZygiskRowIcon.LINKER
    ZygiskMethod.MAPS_AND_SMAPS,
    ZygiskMethod.SOLIST_ATEXIT_HEAP -> ZygiskRowIcon.MEMORY
    ZygiskMethod.NATIVE_SNAPSHOT,
    ZygiskMethod.THREADS_AND_FDS -> null
}
