package com.kaizehuang.vernier.features.kernelcheck.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class KernelCheckCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<KernelCheckHeaderFactModel>,
    val identityRows: List<KernelCheckDetailRowModel>,
    val anomalyRows: List<KernelCheckDetailRowModel>,
    val behaviorRows: List<KernelCheckDetailRowModel>,
    val impactItems: List<KernelCheckImpactItemModel>,
    val methodRows: List<KernelCheckDetailRowModel>,
    val scanRows: List<KernelCheckDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class KernelCheckHeaderFact(val label: String) {
    IDENTITY("Identity"),
    BOOT("Boot"),
    BEHAVIOR("Behavior"),
    NATIVE("Native"),
}

data class KernelCheckHeaderFactModel(
    val fact: KernelCheckHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

data class KernelCheckDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
)

data class KernelCheckImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
