package com.kaizehuang.vernier.features.lsposed.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class LSPosedCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<LSPosedHeaderFactModel>,
    val runtimeRows: List<LSPosedDetailRowModel>,
    val binderRows: List<LSPosedDetailRowModel>,
    val packageRows: List<LSPosedDetailRowModel>,
    val policyRows: List<LSPosedDetailRowModel>,
    val nativeRows: List<LSPosedDetailRowModel>,
    val impactItems: List<LSPosedImpactItemModel>,
    val methodRows: List<LSPosedDetailRowModel>,
    val scanRows: List<LSPosedDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class LSPosedHeaderFact(val label: String) {
    CRITICAL("Critical"),
    REVIEW("Review"),
    BRIDGE("Bridge"),
    PACKAGES("Packages"),
}

data class LSPosedHeaderFactModel(
    val fact: LSPosedHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

/** A row icon that names what the row is about; rows without one show their status icon. */
enum class LSPosedRowIcon {
    BRIDGE,
    PACKAGE,
    MEMORY,
    POLICY,
    HOOK,
}

data class LSPosedDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
    val icon: LSPosedRowIcon? = null,
)

data class LSPosedImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
