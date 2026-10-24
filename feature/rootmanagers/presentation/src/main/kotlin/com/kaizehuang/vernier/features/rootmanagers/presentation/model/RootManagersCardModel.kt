package com.kaizehuang.vernier.features.rootmanagers.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class RootManagersCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val entryRows: List<RootManagersDetailRowModel>,
    val profileRows: List<RootManagersDetailRowModel>,
    val scanRows: List<RootManagersDetailRowModel>,
) : DetectorHeadline

data class RootManagersDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
    val detailMonospace: Boolean = false,
    /** Full node detail copied on a double tap; null when the row has nothing more to share. */
    val hiddenCopyText: String? = null,
)
