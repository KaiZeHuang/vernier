package com.kaizehuang.vernier.features.memory.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class MemoryCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<MemoryHeaderFactModel>,
    val hookRows: List<MemoryDetailRowModel>,
    val mappingRows: List<MemoryDetailRowModel>,
    val loaderRows: List<MemoryDetailRowModel>,
    val impactItems: List<MemoryImpactItemModel>,
    val methodRows: List<MemoryDetailRowModel>,
    val scanRows: List<MemoryDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class MemoryHeaderFact(val label: String) {
    CRITICAL("Critical"),
    REVIEW("Review"),
    HOOKS("Hooks"),
    RUNTIME("Runtime"),
}

data class MemoryHeaderFactModel(
    val fact: MemoryHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

/** A row icon that names what the row is about; rows without one show their status icon. */
enum class MemoryRowIcon {
    VDSO,
    SIGNAL_HANDLER,
}

data class MemoryDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
    val icon: MemoryRowIcon? = null,
)

data class MemoryImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
