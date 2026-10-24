package com.kaizehuang.vernier.core.ui.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus

public data class MetricChipModel(
    val label: String,
    val value: String,
    val status: DetectorStatus = DetectorStatus.allClear(),
)

public data class HighlightItemModel(
    val title: String,
    val detail: String,
    val status: DetectorStatus,
)

public data class ContextItemModel(
    val label: String,
    val value: String,
)

public data class ActionItemModel(
    val label: String,
    val counter: String? = null,
)
