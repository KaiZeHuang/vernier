package com.kaizehuang.vernier.features.su.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class SuCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<SuHeaderFactModel>,
    val artifactRows: List<SuDetailRowModel>,
    val contextRows: List<SuDetailRowModel>,
    val impactItems: List<SuImpactItemModel>,
    val methodRows: List<SuDetailRowModel>,
    val scanRows: List<SuDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class SuHeaderFact(val label: String) {
    ARTIFACTS("Artifacts"),
    DAEMONS("Daemons"),
    CONTEXT("Context"),
    PROCESSES("Processes"),
}

data class SuHeaderFactModel(
    val fact: SuHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

data class SuDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
)

data class SuImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
