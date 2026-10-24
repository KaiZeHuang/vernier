package com.kaizehuang.vernier.capability.earlypreload.data

import android.content.Intent

public object EarlyVirtualizationPreloadStore {

    @Volatile
    private var intentResult: EarlyVirtualizationPreloadResult =
        EarlyVirtualizationPreloadResult.empty()

    @Volatile
    private var bridge: EarlyVirtualizationPreloadBridge = EarlyVirtualizationPreloadBridge()

    public fun capture(intent: Intent?) {
        val captured = EarlyVirtualizationPreloadResult.fromIntent(intent)
        if (captured.hasRun) {
            intentResult = captured
        }
    }

    public fun currentResult(): EarlyVirtualizationPreloadResult {
        return selectPreferred(
            nativeResult = bridge.getStoredResult(),
            intentOnlyResult = intentResult,
        )
    }

    internal fun capture(values: Map<String, Any?>) {
        val captured = EarlyVirtualizationPreloadResult.fromCapturedValues(values)
        if (captured.hasRun) {
            intentResult = captured
        }
    }

    internal fun selectPreferred(
        nativeResult: EarlyVirtualizationPreloadResult,
        intentOnlyResult: EarlyVirtualizationPreloadResult,
    ): EarlyVirtualizationPreloadResult {
        return when {
            nativeResult.hasRun -> nativeResult
            intentOnlyResult.hasRun -> intentOnlyResult
            else -> EarlyVirtualizationPreloadResult.empty()
        }
    }

    internal fun replaceBridgeForTesting(testBridge: EarlyVirtualizationPreloadBridge) {
        bridge = testBridge
    }

    internal fun resetForTesting() {
        intentResult = EarlyVirtualizationPreloadResult.empty()
        bridge = EarlyVirtualizationPreloadBridge()
    }
}
