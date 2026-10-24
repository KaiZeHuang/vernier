package com.kaizehuang.vernier.features.mount.presentation

import com.kaizehuang.vernier.features.mount.domain.MountReport
import com.kaizehuang.vernier.features.mount.domain.toDetectorStatus
import com.kaizehuang.vernier.features.mount.presentation.model.MountCardModel

class MountCardModelMapper {
    fun map(
        report: MountReport,
    ): MountCardModel {
        return MountCardModel(
            title = "Mount",
            subtitle = buildSubtitle(report),
            status = report.toDetectorStatus(),
            verdict = buildVerdict(report),
            summary = buildSummary(report),
            headerFacts = buildHeaderFacts(report),
            procMountViewRows = listOf(
                procMountViewRow(report),
                zygoteNextMountViewRow(report),
            ),
            artifactRows = buildRows(
                report.stage,
                report.artifactRows,
                listOf("Magisk mounts", "Zygisk/Riru", "/data/adb", "Debug ramdisk")
            ),
            runtimeRows = buildRows(
                report.stage,
                report.runtimeRows,
                listOf("System RW", "Overlay mounts", "Loop devices", "dm-verity bypass")
            ),
            filesystemRows = buildRows(
                report.stage,
                report.filesystemRows,
                listOf("Overlayfs support", "System filesystem type", "Tmpfs anomaly")
            ),
            consistencyRows = buildRows(
                report.stage,
                report.consistencyRows,
                listOf(
                    "Namespace access",
                    "Shell tmp view",
                    "Mount consistency",
                    "Mount ID loophole",
                    "Bind mount root"
                )
            ),
            impactItems = buildImpactItems(report),
            methodRows = buildMethodRows(report),
            scanRows = buildScanRows(report),
        )
    }
}
