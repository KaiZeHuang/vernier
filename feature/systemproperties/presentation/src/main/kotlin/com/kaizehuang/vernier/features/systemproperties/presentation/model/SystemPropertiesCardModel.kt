package com.kaizehuang.vernier.features.systemproperties.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class SystemPropertiesCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<SystemPropertiesHeaderFactModel>,
    val coreRows: List<SystemPropertiesDetailRowModel>,
    val bootRows: List<SystemPropertiesDetailRowModel>,
    val buildRows: List<SystemPropertiesDetailRowModel>,
    val sourceRows: List<SystemPropertiesDetailRowModel>,
    val consistencyRows: List<SystemPropertiesDetailRowModel>,
    val infoRows: List<SystemPropertiesDetailRowModel>,
    val impactItems: List<SystemPropertiesImpactItemModel>,
    val methodRows: List<SystemPropertiesDetailRowModel>,
    val scanRows: List<SystemPropertiesDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class SystemPropertiesHeaderFact(val label: String) {
    CRITICAL("Critical"),
    REVIEW("Review"),
    BOOT("Boot"),
    BUILD("Build"),
}

data class SystemPropertiesHeaderFactModel(
    val fact: SystemPropertiesHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

data class SystemPropertiesDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
)

data class SystemPropertiesImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
