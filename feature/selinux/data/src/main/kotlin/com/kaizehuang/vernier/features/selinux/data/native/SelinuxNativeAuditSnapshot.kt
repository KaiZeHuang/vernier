package com.kaizehuang.vernier.features.selinux.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

data class SelinuxNativeAuditSnapshot(
    val available: Boolean = false,
    val callbackInstalled: Boolean = false,
    val probeRan: Boolean = false,
    val denialObserved: Boolean = false,
    val allowObserved: Boolean = false,
    val probeMarker: String? = null,
    val failureReason: String? = null,
    val callbackLines: List<String> = emptyList(),
    /**
     * Why this snapshot is or is not usable. [available] alone cannot distinguish "the probe ran and
     * found nothing" from "the probe never ran", so the reason is carried here. [failureReason] holds
     * the same information in the wording the report shows.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
)
