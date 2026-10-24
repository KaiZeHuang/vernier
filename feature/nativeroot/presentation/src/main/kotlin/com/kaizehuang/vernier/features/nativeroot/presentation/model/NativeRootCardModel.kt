package com.kaizehuang.vernier.features.nativeroot.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class NativeRootCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<NativeRootHeaderFactModel>,
    val nativeRows: List<NativeRootDetailRowModel>,
    val runtimeRows: List<NativeRootDetailRowModel>,
    val kernelRows: List<NativeRootDetailRowModel>,
    val propertyRows: List<NativeRootDetailRowModel>,
    val impactItems: List<NativeRootImpactItemModel>,
    val methodRows: List<NativeRootDetailRowModel>,
    val scanRows: List<NativeRootDetailRowModel>,
) : DetectorHeadline

/** The facts in the card's header, in the order the export lists them. */
enum class NativeRootHeaderFact(val label: String) {
    FLAGS("Flags"),
    DIRECT("Direct"),
    KERNEL("Kernel"),
    RUNTIME("Runtime"),
}

data class NativeRootHeaderFactModel(
    val fact: NativeRootHeaderFact,
    val value: String,
    val status: DetectorStatus,
) {
    val label: String get() = fact.label
}

data class NativeRootDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val hiddenCopyText: String? = null,
    val detailMonospace: Boolean = false,
)

data class NativeRootImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
