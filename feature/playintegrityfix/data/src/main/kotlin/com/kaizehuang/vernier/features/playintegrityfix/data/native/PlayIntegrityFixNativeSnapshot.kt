package com.kaizehuang.vernier.features.playintegrityfix.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

data class PlayIntegrityFixNativeTrace(
    val severity: String,
    val label: String,
    val detail: String,
)

data class PlayIntegrityFixNativeSnapshot(
    val available: Boolean = false,
    val nativeProperties: Map<String, String> = emptyMap(),
    val runtimeTraces: List<PlayIntegrityFixNativeTrace> = emptyList(),
    /**
     * Why this snapshot is or is not usable. [available] alone cannot distinguish "the probe ran and
     * found nothing" from "the probe never ran", so the reason is carried here.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
) {
    val nativePropertyHitCount: Int
        get() = nativeProperties.values.count { it.isNotBlank() }
}
