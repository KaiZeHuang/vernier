package com.kaizehuang.vernier.features.tee.data.verification.keystore

internal data class Measurement(
    val source: String,
    val detail: String,
    val measureMillis: (Any, StableTimeSource) -> Double,
    val timerSource: StableTimeSource,
)

internal data class PairedSampleSeries(
    val attemptedPairCount: Int,
    val attestedSamples: List<Double>,
    val nonAttestedSamples: List<Double>,
    val failedPairCount: Int,
    val failureReason: String? = null,
) {
    val pairedSampleCount: Int
        get() = minOf(attestedSamples.size, nonAttestedSamples.size)

    fun filterOutlierPairs(): PairedSampleSeries {
        val pairedDiffs = pairedDiffSeries(attestedSamples, nonAttestedSamples)
        if (pairedDiffs.size < 8) {
            return this
        }
        val median = pairedDiffs.sorted()[pairedDiffs.size / 2]
        val absoluteDeviation = pairedDiffs.map { kotlin.math.abs(it - median) }.sorted()
        val mad = absoluteDeviation[absoluteDeviation.size / 2]
        if (mad == 0.0) {
            return this
        }
        val keepIndices = pairedDiffs.mapIndexedNotNull { index, diff ->
            if (kotlin.math.abs(diff - median) <= mad * 6.0) index else null
        }
        if (keepIndices.size == pairedDiffs.size || keepIndices.isEmpty()) {
            return this
        }
        return PairedSampleSeries(
            attemptedPairCount = attemptedPairCount,
            attestedSamples = keepIndices.map { attestedSamples[it] },
            nonAttestedSamples = keepIndices.map { nonAttestedSamples[it] },
            failedPairCount = failedPairCount,
            failureReason = failureReason,
        )
    }
}

internal data class TimerMetadata(
    val timerSource: String,
    val affinity: String,
    val timerFallbackReason: String? = null,
    val timeSource: StableTimeSource,
)
