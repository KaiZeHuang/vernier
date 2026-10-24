package com.kaizehuang.vernier.features.systemproperties.presentation

import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertyCategory
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySeverity
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySignal
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.systemproperties.domain.SystemPropertiesReport
import com.kaizehuang.vernier.features.systemproperties.domain.SystemPropertiesStage
import com.kaizehuang.vernier.features.systemproperties.domain.hasReducedCoverage
import com.kaizehuang.vernier.features.systemproperties.presentation.model.SystemPropertiesImpactItemModel

internal fun buildImpactItems(report: SystemPropertiesReport): List<SystemPropertiesImpactItemModel> {
    return when (report.stage) {
        SystemPropertiesStage.LOADING -> listOf(
            SystemPropertiesImpactItemModel(
                text = "Gathering property, boot, and native cross-check evidence.",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
        )

        SystemPropertiesStage.FAILED -> listOf(
            SystemPropertiesImpactItemModel(
                text = report.errorMessage ?: "System Properties scan failed.",
                status = DetectorStatus.info(InfoKind.ERROR),
            ),
        )

        SystemPropertiesStage.READY -> buildList {
            val crossCheckSignals = report.crossCheckSignals
            if (crossCheckSignals.isNotEmpty()) {
                add(
                    SystemPropertiesImpactItemModel(
                        text = "Cross-check contradictions mean different system layers disagree about the same boot or build state, which is stronger than a single suspicious value.",
                        status = if (crossCheckSignals.any { it.severity == SystemPropertySeverity.DANGER }) {
                            DetectorStatus.danger()
                        } else {
                            DetectorStatus.warning()
                        },
                    ),
                )
            }
            if (report.propAreaHoleCount > 0) {
                add(
                    SystemPropertiesImpactItemModel(
                        text = "Raw /dev/__properties__ hole residue means the property storage layout no longer matches a normal append-only allocation pattern.",
                        status = if (report.hasDangerPropAreaHole) {
                            DetectorStatus.danger()
                        } else {
                            DetectorStatus.warning()
                        },
                    ),
                )
            }
            if (report.sourceMismatchCount > 0) {
                add(
                    SystemPropertiesImpactItemModel(
                        text = "Different property APIs returned different values. That can indicate hook-based spoofing, translation issues, or framework/native drift.",
                        status = if (report.dangerSignals.any { it.category == SystemPropertyCategory.SOURCE_CONSISTENCY }) {
                            DetectorStatus.danger()
                        } else {
                            DetectorStatus.warning()
                        },
                    ),
                )
            }
            if (isEmpty()) {
                add(
                    if (report.hasReducedCoverage()) {
                        SystemPropertiesImpactItemModel(
                            text = "Observed properties matched conservative expectations, but raw property-area layout coverage was unavailable.",
                            status = DetectorStatus.info(InfoKind.SUPPORT),
                        )
                    } else {
                        SystemPropertiesImpactItemModel(
                            text = "Observed key properties matched conservative production expectations across multiple read paths.",
                            status = DetectorStatus.allClear(),
                        )
                    },
                )
            }
            add(
                SystemPropertiesImpactItemModel(
                    text = "System properties are still software-readable values, so even aligned results should be combined with kernel, SU, TEE, and package-level signals.",
                    status = DetectorStatus.info(InfoKind.SUPPORT),
                ),
            )
        }
    }
}

internal val SystemPropertiesReport.crossCheckSignals: List<SystemPropertySignal>
    get() = propertySignals.filter { it.category == SystemPropertyCategory.PROPERTY_CONSISTENCY }

internal val SystemPropertiesReport.hasDangerPropAreaHole: Boolean
    get() = propAreaSignals.any { it.severity == SystemPropertySeverity.DANGER }
