package com.kaizehuang.vernier.features.memory.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

data class MemoryNativeFinding(
    val section: String,
    val category: String,
    val label: String,
    val severity: String,
    val detail: String,
)

data class MemoryNativeSnapshot(
    val available: Boolean = false,
    val hookChecksRan: Boolean = false,
    val entryChecksSupported: Boolean = false,
    val gotPltHook: Boolean = false,
    val inlineHook: Boolean = false,
    val prologueModified: Boolean = false,
    val trampoline: Boolean = false,
    val suspiciousJump: Boolean = false,
    val modifiedFunctionCount: Int = 0,
    val writableExec: Boolean = false,
    val anonymousExec: Boolean = false,
    val systemAnonymousExec: Boolean = false,
    val systemCopyModified: Boolean = false,
    val systemCopyUnverified: Boolean = false,
    val swappedExec: Boolean = false,
    val sharedDirtyExec: Boolean = false,
    val deletedSo: Boolean = false,
    val suspiciousMemfd: Boolean = false,
    val execAshmem: Boolean = false,
    val devZeroExec: Boolean = false,
    val signalHandler: Boolean = false,
    val fridaSignal: Boolean = false,
    val anonymousSignal: Boolean = false,
    val vdsoChecksRan: Boolean = false,
    val vdsoRemapped: Boolean = false,
    val vdsoUnusualBase: Boolean = false,
    val deletedLibrary: Boolean = false,
    val hiddenModule: Boolean = false,
    val mapsOnlyModule: Boolean = false,
    val criticalCount: Int = 0,
    val highCount: Int = 0,
    val mediumCount: Int = 0,
    val lowCount: Int = 0,
    val findings: List<MemoryNativeFinding> = emptyList(),
    /**
     * Why this snapshot is or is not usable. [available] alone cannot distinguish "the probe ran and
     * found nothing" from "the probe never ran", so the reason is carried here.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
)
