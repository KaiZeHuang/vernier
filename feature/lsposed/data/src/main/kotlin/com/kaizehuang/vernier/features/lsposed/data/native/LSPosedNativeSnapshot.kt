package com.kaizehuang.vernier.features.lsposed.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

data class LSPosedNativeTrace(
    val group: String,
    val severity: String,
    val label: String,
    val detail: String,
)

data class LSPosedNativeSnapshot(
    val available: Boolean = false,
    val mapsAvailable: Boolean = false,
    val heapAvailable: Boolean = false,
    val mapsHitCount: Int = 0,
    val mapsScannedLines: Int = 0,
    val heapHitCount: Int = 0,
    val heapScannedRegions: Int = 0,
    val traces: List<LSPosedNativeTrace> = emptyList(),
    /**
     * Why this snapshot is or is not usable. [available] alone cannot distinguish "the probe ran and
     * found nothing" from "the probe never ran", so the reason is carried here.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
)
