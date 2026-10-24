package com.kaizehuang.vernier.features.customrom.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class CustomRomCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<CustomRomHeaderFactModel>,
    val buildRows: List<CustomRomDetailRowModel>,
    val runtimeRows: List<CustomRomDetailRowModel>,
    val frameworkRows: List<CustomRomDetailRowModel>,
    val impactItems: List<CustomRomImpactItemModel>,
    val methodRows: List<CustomRomDetailRowModel>,
    val scanRows: List<CustomRomDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class CustomRomHeaderFact(val label: String) {
    ROMS("ROMs"),
    BUILD("Build"),
    RUNTIME("Runtime"),
    NATIVE("Native"),
}

data class CustomRomHeaderFactModel(
    val fact: CustomRomHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

data class CustomRomDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
)

data class CustomRomImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
