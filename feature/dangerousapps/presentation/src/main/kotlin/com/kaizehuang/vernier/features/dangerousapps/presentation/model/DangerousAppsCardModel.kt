package com.kaizehuang.vernier.features.dangerousapps.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class DangerousAppsCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<DangerousAppsHeaderFactModel>,
    val hmaAlert: DangerousAppsHmaAlertModel? = null,
    val packageItems: List<DangerousAppsPackageItemModel>,
    val context: List<DangerousAppsContextItemModel>,
    val targetApps: List<DangerousAppsTargetAppModel>,
) : DetectorHeadline {
    /** Rendering-only severity for the Packages section. */
    val packageSectionSeverity: DetectionSeverity?
        get() {
            val hidden = headerFacts.firstOrNull { it.fact == DangerousAppsHeaderFact.HIDDEN }?.status?.severity
            val hits = headerFacts.firstOrNull { it.fact == DangerousAppsHeaderFact.HITS }?.status?.severity
            return when {
                hidden == DetectionSeverity.DANGER -> DetectionSeverity.DANGER
                hits == DetectionSeverity.WARNING -> DetectionSeverity.WARNING
                else -> null
            }
        }
}

/** The facts in the card's header, in the order the export lists them. */
enum class DangerousAppsHeaderFact(val label: String) {
    TARGETS("Targets"),
    PM("PM"),
    HITS("Hits"),
    HIDDEN("Hidden"),
}

data class DangerousAppsHeaderFactModel(
    val fact: DangerousAppsHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

data class DangerousAppsContextItemModel(
    val label: String,
    val value: String,
)

data class DangerousAppsHmaAlertModel(
    val title: String,
    val summary: String,
    val hiddenPackages: List<DangerousAppsHiddenPackageItemModel>,
)

data class DangerousAppsHiddenPackageItemModel(
    val appName: String,
    val packageName: String,
    val methods: List<String>,
)

data class DangerousAppsPackageItemModel(
    val appName: String,
    val packageName: String,
    val methods: List<String>,
)

data class DangerousAppsTargetAppModel(
    val appName: String,
    val packageName: String,
    val category: String,
)
