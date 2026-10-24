package com.kaizehuang.vernier.features.tee.data.verification.crl

internal sealed interface CrlSnapshotResult {
    data class Success(val entries: Map<String, CrlEntry>) : CrlSnapshotResult

    data class Failure(val failure: CrlFailure) : CrlSnapshotResult
}

internal data class CrlFailure(
    val summary: String,
    val detail: String? = null,
) {
    fun withPreflightDetail(preflightDetail: String?): CrlFailure {
        if (preflightDetail.isNullOrBlank()) {
            return this
        }
        return copy(
            detail = listOf(preflightDetail, detail).filterNotNull().joinToString(separator = " ")
        )
    }
}

internal data class CrlEntry(
    val status: String,
    val reason: String?,
    val source: CrlEntrySource,
)

internal enum class CrlEntrySource {
    EMBEDDED,
    ONLINE,
}
