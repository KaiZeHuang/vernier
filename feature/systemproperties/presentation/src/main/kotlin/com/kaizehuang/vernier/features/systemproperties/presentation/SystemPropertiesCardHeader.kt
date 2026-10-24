package com.kaizehuang.vernier.features.systemproperties.presentation

import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertyCategory
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySeverity
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySignal
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.systemproperties.domain.SystemPropertiesReport
import com.kaizehuang.vernier.features.systemproperties.domain.SystemPropertiesStage
import com.kaizehuang.vernier.features.systemproperties.domain.hasReducedCoverage
import com.kaizehuang.vernier.features.systemproperties.presentation.model.SystemPropertiesHeaderFact
import com.kaizehuang.vernier.features.systemproperties.presentation.model.SystemPropertiesHeaderFactModel

internal fun buildSubtitle(report: SystemPropertiesReport): String {
    return when (report.stage) {
        SystemPropertiesStage.LOADING -> "security props + raw boot + source cross-checks"
        SystemPropertiesStage.FAILED -> "local property scan failed"
        SystemPropertiesStage.READY -> buildString {
            append("${report.checkedRuleCount} rules · ${report.infoPropertyCount} info · ${report.nativeHitCount} native · ${report.buildSignalCount} Build")
            if (report.propAreaHoleCount > 0) {
                append(" · ${report.propAreaHoleCount} prop-area hole(s)")
            }
        }
    }
}

internal fun buildVerdict(report: SystemPropertiesReport): String {
    return when (report.stage) {
        SystemPropertiesStage.LOADING -> "Scanning property, boot, and source state"
        SystemPropertiesStage.FAILED -> "System Properties scan failed"
        SystemPropertiesStage.READY -> when {
            report.hasDangerSignals -> "${report.dangerSignals.size} high-risk property or coherence signal(s)"
            report.hasWarningSignals -> "${report.warningSignals.size} property signal(s) need review"
            report.hasReducedCoverage() -> "System property scan has reduced coverage"
            else -> "No risky property or coherence drift"
        }
    }
}

internal fun buildSummary(report: SystemPropertiesReport): String {
    return when (report.stage) {
        SystemPropertiesStage.LOADING ->
            "Core security, verified boot, build profile, source consistency, and raw boot cross-checks are collecting local evidence."

        SystemPropertiesStage.FAILED ->
            report.errorMessage
                ?: "System property scan failed before evidence could be assembled."

        SystemPropertiesStage.READY -> when {
            report.hasDangerSignals ->
                "Property values, raw boot contradictions, cross-source drift, cross-check drift, or raw property-area residue indicate insecure build state, spoofing risk, or modified boot context."

            report.hasWarningSignals ->
                "Cross-source drift, cross-property drift, or raw property-area residue suggests a review-worthy build or boot context, even if not every warning means active compromise."

            report.hasReducedCoverage() ->
                "No risky property or coherence drift surfaced from available probes, but raw property-area layout coverage was unavailable."

            else ->
                "Key properties, framework constants, native libc reads, raw boot parameters, and property-area layout stayed aligned."
        }
    }
}

internal fun buildHeaderFacts(report: SystemPropertiesReport): List<SystemPropertiesHeaderFactModel> {
    return when (report.stage) {
        SystemPropertiesStage.LOADING -> placeholderFacts(
            "Pending",
            DetectorStatus.info(InfoKind.SUPPORT)
        )

        SystemPropertiesStage.FAILED -> placeholderFacts(
            "Error",
            DetectorStatus.info(InfoKind.ERROR)
        )

        SystemPropertiesStage.READY -> {
            val bootSignals = report.signals.filter {
                it.category == SystemPropertyCategory.VERIFIED_BOOT ||
                        it.category == SystemPropertyCategory.PARTITION_VERITY
            }
            val buildSignals = report.signals.filter {
                it.category == SystemPropertyCategory.BUILD_PROFILE
            }
            listOf(
                SystemPropertiesHeaderFactModel(
                    fact = SystemPropertiesHeaderFact.CRITICAL,
                    value = countLabel(report.dangerSignals.size),
                    status = if (report.dangerSignals.isEmpty()) DetectorStatus.allClear() else DetectorStatus.danger(),
                ),
                SystemPropertiesHeaderFactModel(
                    fact = SystemPropertiesHeaderFact.REVIEW,
                    value = countLabel(report.warningSignals.size),
                    status = if (report.warningSignals.isEmpty()) DetectorStatus.allClear() else DetectorStatus.warning(),
                ),
                SystemPropertiesHeaderFactModel(
                    fact = SystemPropertiesHeaderFact.BOOT,
                    value = if (report.bootSignalCount > 0) report.bootSignalCount.toString() else "Clean",
                    status = categoryStatus(bootSignals),
                ),
                SystemPropertiesHeaderFactModel(
                    fact = SystemPropertiesHeaderFact.BUILD,
                    value = if (report.buildProfileSignalCount > 0) report.buildProfileSignalCount.toString() else "Clean",
                    status = categoryStatus(buildSignals),
                ),
            )
        }
    }
}

private fun placeholderFacts(
    value: String,
    status: DetectorStatus,
): List<SystemPropertiesHeaderFactModel> {
    return listOf(
        SystemPropertiesHeaderFactModel(SystemPropertiesHeaderFact.CRITICAL, value, status),
        SystemPropertiesHeaderFactModel(SystemPropertiesHeaderFact.REVIEW, value, status),
        SystemPropertiesHeaderFactModel(SystemPropertiesHeaderFact.BOOT, value, status),
        SystemPropertiesHeaderFactModel(SystemPropertiesHeaderFact.BUILD, value, status),
    )
}

private fun categoryStatus(
    signals: List<SystemPropertySignal>,
): DetectorStatus {
    return when {
        signals.any { it.severity == SystemPropertySeverity.DANGER } -> DetectorStatus.danger()
        signals.any { it.severity == SystemPropertySeverity.WARNING } -> DetectorStatus.warning()
        signals.any { it.severity == SystemPropertySeverity.SAFE } -> DetectorStatus.allClear()
        else -> DetectorStatus.info(InfoKind.SUPPORT)
    }
}

private fun countLabel(count: Int): String {
    return if (count > 0) count.toString() else "None"
}
