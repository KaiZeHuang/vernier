package com.kaizehuang.vernier.features.zygisk.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class ZygiskCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<ZygiskHeaderFactModel>,
    val stateRows: List<ZygiskDetailRowModel>,
    val impactItems: List<ZygiskImpactItemModel>,
    val methodRows: List<ZygiskDetailRowModel>,
    val signalRows: List<ZygiskDetailRowModel>,
    val references: List<String>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class ZygiskHeaderFact(val label: String) {
    STATE("State"),
    CONFIDENCE("Confidence"),
    FD_TRAP("FD trap"),
    NATIVE("Native"),
}

data class ZygiskHeaderFactModel(
    val fact: ZygiskHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

/** A row icon that names what the row is about; rows without one show their status icon. */
enum class ZygiskRowIcon {
    CROSS_PROCESS,
    LINKER,
    MEMORY,
}

data class ZygiskDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
    val icon: ZygiskRowIcon? = null,
)

data class ZygiskImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
