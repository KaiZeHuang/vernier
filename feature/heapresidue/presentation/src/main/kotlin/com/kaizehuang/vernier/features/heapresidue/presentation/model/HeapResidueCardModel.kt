package com.kaizehuang.vernier.features.heapresidue.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class HeapResidueCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val signalRows: List<HeapResidueDetailRowModel>,
    val scanRows: List<HeapResidueDetailRowModel>,
) : DetectorHeadline

data class HeapResidueDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
)
