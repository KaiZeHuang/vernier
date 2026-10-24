package com.kaizehuang.vernier.features.zygisk.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskReport
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskStage
import com.kaizehuang.vernier.features.zygisk.domain.toDetectorStatus
import com.kaizehuang.vernier.features.zygisk.presentation.model.ZygiskImpactItemModel

internal fun buildImpactItems(
    report: ZygiskReport,
): List<ZygiskImpactItemModel> {
    return when (report.stage) {
        ZygiskStage.LOADING -> listOf(
            ZygiskImpactItemModel(
                text = "Collecting the cross-process FD trap result and the native runtime snapshot in parallel.",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
        )

        ZygiskStage.FAILED -> listOf(
            ZygiskImpactItemModel(
                text = report.errorMessage ?: "Zygisk detection failed.",
                status = DetectorStatus.info(InfoKind.ERROR),
            ),
        )

        ZygiskStage.READY -> when (report.toDetectorStatus()) {
            DetectorStatus.danger() -> listOf(
                ZygiskImpactItemModel(
                    text = "A red result means this process exposed direct runtime evidence or the cross-process specialization path behaved like a Zygisk-sanitized child process.",
                    status = DetectorStatus.danger(),
                ),
                ZygiskImpactItemModel(
                    text = "This is stronger than package residue because it touches live loader behavior, specialization side effects, or process runtime state directly.",
                    status = DetectorStatus.warning(),
                ),
            )

            DetectorStatus.warning() -> listOf(
                ZygiskImpactItemModel(
                    text = "Yellow means only weaker corroboration traces were found, not a single decisive runtime primitive on their own.",
                    status = DetectorStatus.warning(),
                ),
                ZygiskImpactItemModel(
                    text = "Read this together with Memory and Mount, which can still surface loader and mapping residue in parallel.",
                    status = DetectorStatus.info(InfoKind.SUPPORT),
                ),
            )

            DetectorStatus.allClear() -> listOf(
                ZygiskImpactItemModel(
                    text = "No direct runtime or converging heuristic signal surfaced in the current app process.",
                    status = DetectorStatus.allClear(),
                ),
                ZygiskImpactItemModel(
                    text = "A clean result reduces confidence in active Zygisk-style tampering for this process, but it does not prove the whole device is stock.",
                    status = DetectorStatus.info(InfoKind.SUPPORT),
                ),
            )

            else -> listOf(
                ZygiskImpactItemModel(
                    text = "The detector completed, but at least one major path was unavailable, so this result is support-only rather than clean.",
                    status = DetectorStatus.info(InfoKind.SUPPORT),
                ),
                ZygiskImpactItemModel(
                    text = "Unavailable service binding, an unsupported heap helper, or a missing native snapshot can all reduce confidence without implying a positive detection.",
                    status = DetectorStatus.info(InfoKind.SUPPORT),
                ),
            )
        }
    }
}
