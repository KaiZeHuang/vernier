package com.kaizehuang.vernier.features.playintegrityfix.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.playintegrityfix.domain.PlayIntegrityFixReport
import com.kaizehuang.vernier.features.playintegrityfix.domain.PlayIntegrityFixSignalSeverity
import com.kaizehuang.vernier.features.playintegrityfix.domain.PlayIntegrityFixStage
import com.kaizehuang.vernier.features.playintegrityfix.presentation.model.PlayIntegrityFixImpactItemModel

internal fun buildImpactItems(report: PlayIntegrityFixReport): List<PlayIntegrityFixImpactItemModel> {
    return when (report.stage) {
        PlayIntegrityFixStage.LOADING -> listOf(
            PlayIntegrityFixImpactItemModel(
                text = "Gathering residue properties and runtime trace evidence for Play Integrity spoof frameworks.",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
        )

        PlayIntegrityFixStage.FAILED -> listOf(
            PlayIntegrityFixImpactItemModel(
                text = report.errorMessage ?: "Play Integrity Fix scan failed.",
                status = DetectorStatus.info(InfoKind.ERROR),
            ),
        )

        PlayIntegrityFixStage.READY -> buildList {
            if (report.propertySignals.isNotEmpty()) {
                add(
                    PlayIntegrityFixImpactItemModel(
                        text = "Persisted spoof properties are relatively strong evidence because they survive process restarts and are readable from multiple layers.",
                        status = if (report.propertySignals.any { it.severity == PlayIntegrityFixSignalSeverity.DANGER }) {
                            DetectorStatus.danger()
                        } else {
                            DetectorStatus.warning()
                        },
                    ),
                )
            }
            if (report.consistencySignals.isNotEmpty()) {
                add(
                    PlayIntegrityFixImpactItemModel(
                        text = "Source mismatches mean property APIs disagree. That often points to hook-based translation, cleanup drift, or framework/native divergence.",
                        status = DetectorStatus.warning(),
                    ),
                )
            }
            if (report.nativeSignals.isNotEmpty()) {
                add(
                    PlayIntegrityFixImpactItemModel(
                        text = "Runtime traces in current-process maps can indicate bypass code, deleted artifacts, or keystore-adjacent tampering still touching the app process.",
                        status = if (report.nativeSignals.any { it.severity == PlayIntegrityFixSignalSeverity.DANGER }) {
                            DetectorStatus.danger()
                        } else {
                            DetectorStatus.warning()
                        },
                    ),
                )
            }
            if (isEmpty() && report.nativeAvailable) {
                add(
                    PlayIntegrityFixImpactItemModel(
                        text = "No common Play Integrity Fix residue surfaced from the current property catalog or runtime trace heuristics.",
                        status = DetectorStatus.allClear(),
                    ),
                )
            }
            if (!report.nativeAvailable) {
                add(
                    PlayIntegrityFixImpactItemModel(
                        text = "No Play Integrity Fix residue surfaced from Java-side checks, but native libc and maps coverage was unavailable.",
                        status = DetectorStatus.info(InfoKind.SUPPORT),
                    ),
                )
            } else {
                add(
                    PlayIntegrityFixImpactItemModel(
                        text = "Absence of residue is not proof of stock state. A determined bypass can clean properties and avoid obvious in-process traces.",
                        status = DetectorStatus.info(InfoKind.SUPPORT),
                    ),
                )
            }
        }
    }
}
