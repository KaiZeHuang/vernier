package com.kaizehuang.vernier.features.nativeroot.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootReport
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootStage
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingVerdict
import com.kaizehuang.vernier.features.nativeroot.domain.hasReducedCoverage
import com.kaizehuang.vernier.features.nativeroot.presentation.model.NativeRootImpactItemModel

internal fun buildImpactItems(report: NativeRootReport): List<NativeRootImpactItemModel> {
    return when (report.stage) {
        NativeRootStage.LOADING -> listOf(
            NativeRootImpactItemModel(
                text = "Gathering local native root evidence.",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
        )

        NativeRootStage.FAILED -> listOf(
            NativeRootImpactItemModel(
                text = report.errorMessage ?: "Native Root scan failed.",
                status = DetectorStatus.info(InfoKind.ERROR),
            ),
        )

        NativeRootStage.READY -> buildList {
            if (report.hasDangerFindings) {
                add(
                    NativeRootImpactItemModel(
                        text = "Direct native hits are stronger than plain package or property signals because they come from syscall behavior, runtime processes, cgroup visibility mismatches, or corroborated runtime residue paths.",
                        status = DetectorStatus.danger(),
                    ),
                )
            } else if (report.hasFindingWarnings) {
                add(
                    NativeRootImpactItemModel(
                        text = "Isolated-process mount drift, manager manifest fingerprints, kernel strings, property residue, or cgroup leakage can indicate native-root history or selective runtime hiding, but they are weaker than direct syscall-side probes.",
                        status = DetectorStatus.warning(),
                    ),
                )
            } else if (report.srcuTiming.analysis.verdict == SrcuTimingVerdict.REPEATABLE_DELAY) {
                add(NativeRootImpactItemModel(
                    text = "The optional timing experiment found a repeatable delay relative to both controls. Background fsnotify readers and scheduling remain possible causes; this does not identify a root solution.",
                    status = DetectorStatus.warning(),
                ))
            } else if (report.nativeAvailable) {
                add(
                    if (report.hasReducedCoverage()) {
                        NativeRootImpactItemModel(
                            text = "No native root indicator surfaced from available probes, but one or more support-only evidence paths were unavailable or scoped.",
                            status = DetectorStatus.info(InfoKind.SUPPORT),
                        )
                    } else {
                        NativeRootImpactItemModel(
                            text = "No common KernelSU, APatch, Magisk, SUSFS, or cgroup-leak traces surfaced from the current probe set.",
                            status = DetectorStatus.allClear(),
                        )
                    },
                )
            }
            add(
                NativeRootImpactItemModel(
                    text = if (report.nativeAvailable) {
                        if (report.hasReducedCoverage()) {
                            "Reduced coverage lowers confidence without implying a positive root detection."
                        } else {
                            "A determined root can still hide or remove residue, so absence of native hits is not proof of a stock device."
                        }
                    } else {
                        "Native coverage was unavailable, so this card should not be treated as a strong clean verdict."
                    },
                    status = DetectorStatus.info(InfoKind.SUPPORT),
                ),
            )
        }
    }
}
