package com.kaizehuang.vernier.features.mount.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.mount.domain.MountFindingSeverity
import com.kaizehuang.vernier.features.mount.domain.MountReport
import com.kaizehuang.vernier.features.mount.domain.MountStage
import com.kaizehuang.vernier.features.mount.presentation.model.MountImpactItemModel

internal fun buildImpactItems(report: MountReport): List<MountImpactItemModel> {
    return when (report.stage) {
        MountStage.LOADING -> listOf(
            MountImpactItemModel(
                text = "Gathering runtime mount and filesystem evidence.",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
        )

        MountStage.FAILED -> buildList {
            if (report.zygoteNext.leakDetected) {
                add(
                    MountImpactItemModel(
                        text = "A root-managed mount remained visible from the Android 17 zygote_next native isolated process.",
                        status = DetectorStatus.danger(),
                    ),
                )
            }
            if (report.procMountViewTokenHit) {
                add(
                    MountImpactItemModel(
                        text = "A direct root token in a visible process mount table is strong evidence of a root-managed mount layer.",
                        status = DetectorStatus.danger(),
                    ),
                )
            } else if (report.procMountViewDivergent) {
                add(
                    MountImpactItemModel(
                        text = "Different processes expose different mount tables to the isolated observer, which can indicate selective mount hiding.",
                        status = DetectorStatus.warning(),
                    ),
                )
            }
            add(
                MountImpactItemModel(
                    text = report.errorMessage ?: "Mount scan failed.",
                    status = DetectorStatus.info(InfoKind.ERROR),
                ),
            )
        }

        MountStage.READY -> buildList {
            if (report.zygoteNext.leakDetected) {
                add(
                    MountImpactItemModel(
                        text = "A root-managed mount remained visible from the Android 17 zygote_next native isolated process.",
                        status = DetectorStatus.danger(),
                    ),
                )
            }
            if (report.procMountViewTokenHit) {
                add(
                    MountImpactItemModel(
                        text = "A direct root token in a visible process mount table is strong evidence of a root-managed mount layer.",
                        status = DetectorStatus.danger(),
                    ),
                )
            } else if (report.procMountViewDivergent) {
                add(
                    MountImpactItemModel(
                        text = "Different processes expose different mount tables to the isolated observer, which can indicate selective mount hiding.",
                        status = DetectorStatus.warning(),
                    ),
                )
            }
            report.impacts
                .filterNot { impact ->
                    (report.zygoteNext.leakDetected ||
                            report.procMountViewTokenHit ||
                            report.procMountViewDivergent) &&
                            impact.severity == MountFindingSeverity.SAFE
                }
                .mapTo(this) {
                    MountImpactItemModel(
                        text = it.text,
                        status = severityStatus(it.severity),
                    )
                }
        }
    }
}
