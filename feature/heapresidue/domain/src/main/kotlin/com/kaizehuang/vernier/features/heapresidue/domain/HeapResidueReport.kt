package com.kaizehuang.vernier.features.heapresidue.domain

enum class HeapResidueStage { LOADING, READY, FAILED }
enum class HeapResidueOutcome { NOT_EVALUATED, UNSUPPORTED, UNAVAILABLE, INCONCLUSIVE, NOT_OBSERVED, OBSERVED }
enum class HeapResidueArgument { PACKAGE_NAME, NICE_NAME, APP_DATA_DIR }
enum class HeapResidueProbeFailure { BINDING, HIDDEN_API, DUMP, TIMEOUT, MALFORMED_HPROF, STREAM }
enum class HeapResidueRetention { UNKNOWN, DISABLED, SAVED, TOO_LARGE, FAILED }

/** Whether the running build was source-audited; newer and pre-release builds rely on runtime checks alone. */
enum class HeapResidueRelease { UNKNOWN, AUDITED, NEWER, PRERELEASE }

data class HeapResidueSignal(val packageName: String, val arguments: Set<HeapResidueArgument>)

/** No field represents an installed-app inventory or complete startup history. */
data class HeapResidueReport(
    val stage: HeapResidueStage,
    val outcome: HeapResidueOutcome = HeapResidueOutcome.NOT_EVALUATED,
    val signals: List<HeapResidueSignal> = emptyList(),
    val bytesRead: Long = 0,
    val candidateCount: Int = 0,
    val captureAgeMillis: Long? = null,
    val dumpDurationMillis: Long? = null,
    val gcCountBefore: Long? = null,
    val retention: HeapResidueRetention = HeapResidueRetention.UNKNOWN,
    val probeFailure: HeapResidueProbeFailure? = null,
    val errorMessage: String? = null,
    val release: HeapResidueRelease = HeapResidueRelease.UNKNOWN,
) {
    companion object {
        fun loading() = HeapResidueReport(HeapResidueStage.LOADING)
        fun failed(message: String) = HeapResidueReport(
            stage = HeapResidueStage.FAILED,
            outcome = HeapResidueOutcome.UNAVAILABLE,
            errorMessage = message,
        )
    }
}
