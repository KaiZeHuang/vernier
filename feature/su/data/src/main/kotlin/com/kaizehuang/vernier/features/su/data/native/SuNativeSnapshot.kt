package com.kaizehuang.vernier.features.su.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

data class SuNativeSnapshot(
    val available: Boolean = false,
    val selfContext: String = "",
    val selfContextAbnormal: Boolean = false,
    val suspiciousProcesses: List<String> = emptyList(),
    val checkedProcesses: Int = 0,
    val deniedProcesses: Int = 0,
    /**
     * Why this snapshot is or is not usable. [available] alone cannot distinguish "the probe ran and
     * found a healthy process table" from "the probe never ran", so the reason is carried here.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
)
