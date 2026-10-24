package com.kaizehuang.vernier.features.adbruntime.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.report.DetectorHeadline

data class AdbRuntimeCardModel(
    override val title: String,
    val subtitle: String,
    override val status: DetectorStatus,
    override val verdict: String,
    override val summary: String,
    val signalRows: List<AdbRuntimeDetailRowModel>,
    val scanRows: List<AdbRuntimeDetailRowModel>,
) : DetectorHeadline

data class AdbRuntimeDetailRowModel(
    val label: String,
    val value: String,
    val status: DetectorStatus,
    val detail: String? = null,
)
