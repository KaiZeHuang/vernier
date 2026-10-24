package com.kaizehuang.vernier.features.playintegrityfix.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class PlayIntegrityFixCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<PlayIntegrityFixHeaderFactModel>,
    val propertyRows: List<PlayIntegrityFixDetailRowModel>,
    val consistencyRows: List<PlayIntegrityFixDetailRowModel>,
    val nativeRows: List<PlayIntegrityFixDetailRowModel>,
    val impactItems: List<PlayIntegrityFixImpactItemModel>,
    val methodRows: List<PlayIntegrityFixDetailRowModel>,
    val scanRows: List<PlayIntegrityFixDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class PlayIntegrityFixHeaderFact(val label: String) {
    DIRECT("Direct"),
    REVIEW("Review"),
    PROPS("Props"),
    NATIVE("Native"),
}

data class PlayIntegrityFixHeaderFactModel(
    val fact: PlayIntegrityFixHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

data class PlayIntegrityFixDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
)

data class PlayIntegrityFixImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
