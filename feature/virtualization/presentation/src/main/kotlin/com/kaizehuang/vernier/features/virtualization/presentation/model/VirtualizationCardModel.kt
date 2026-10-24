package com.kaizehuang.vernier.features.virtualization.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class VirtualizationCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val headerFacts: List<VirtualizationHeaderFactModel>,
    val environmentRows: List<VirtualizationDetailRowModel>,
    val runtimeRows: List<VirtualizationDetailRowModel>,
    val consistencyRows: List<VirtualizationDetailRowModel>,
    val honeypotRows: List<VirtualizationDetailRowModel>,
    val hostAppRows: List<VirtualizationDetailRowModel>,
    val impactItems: List<VirtualizationImpactItemModel>,
    val methodRows: List<VirtualizationDetailRowModel>,
    val scanRows: List<VirtualizationDetailRowModel>,
    val references: List<String>,
) : DetectorHeadline

data class VirtualizationHeaderFactModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
)

data class VirtualizationDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
)

data class VirtualizationImpactItemModel(
    val text: String,
    val status: DetectorStatus,
)
