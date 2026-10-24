package com.kaizehuang.vernier.features.tee.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

data class NativeTeeSnapshot(
    val tracingDetected: Boolean = false,
    val suspiciousMappings: List<String> = emptyList(),
    val trickyStoreDetected: Boolean = false,
    val trickyStoreMapsHitDetected: Boolean = false,
    val gotHookDetected: Boolean = false,
    val syscallMismatchDetected: Boolean = false,
    val inlineHookDetected: Boolean = false,
    val honeypotDetected: Boolean = false,
    val trickyStoreMethods: List<String> = emptyList(),
    val trickyStoreDetails: String = "Native probe unavailable",
    val leafDerPrimaryDetected: Boolean = false,
    val leafDerSecondaryDetected: Boolean = false,
    val leafDerFindings: List<String> = emptyList(),
    val pageSize: Int? = null,
    val timingSummary: String? = null,
    val trickyStoreTimerSource: String = "unknown",
    val trickyStoreTimerFallbackReason: String? = null,
    val trickyStoreAffinityStatus: String = "not_requested",
    val trickyStoreTimingRunCount: Int? = null,
    val trickyStoreTimingSuspiciousRunCount: Int? = null,
    val trickyStoreTimingMedianGapNs: Long? = null,
    val trickyStoreTimingGapMadNs: Long? = null,
    val trickyStoreTimingMedianNoiseFloorNs: Long? = null,
    val trickyStoreTimingMedianRatioPercent: Int? = null,
    /**
     * Why this snapshot is or is not usable. Every other field here defaults to "nothing detected",
     * which on its own cannot distinguish a clean device from a probe that never ran.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
)
