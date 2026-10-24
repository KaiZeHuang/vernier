package com.kaizehuang.vernier.features.mount.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class MountCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<MountHeaderFactModel>,
    val procMountViewRows: List<MountDetailRowModel>,
    val artifactRows: List<MountDetailRowModel>,
    val runtimeRows: List<MountDetailRowModel>,
    val filesystemRows: List<MountDetailRowModel>,
    val consistencyRows: List<MountDetailRowModel>,
    val impactItems: List<MountImpactItemModel>,
    val methodRows: List<MountDetailRowModel>,
    val scanRows: List<MountDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class MountHeaderFact(val label: String) {
    CRITICAL("Critical"),
    REVIEW("Review"),
    COVERAGE("Coverage"),
    NATIVE("Native"),
}

data class MountHeaderFactModel(
    val fact: MountHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

data class MountDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
    val hiddenCopyText: String? = null,
)

data class MountImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
