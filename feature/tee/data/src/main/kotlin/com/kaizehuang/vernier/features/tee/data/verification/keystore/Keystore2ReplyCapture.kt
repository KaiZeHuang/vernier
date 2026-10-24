package com.kaizehuang.vernier.features.tee.data.verification.keystore

import java.lang.reflect.Method

internal data class HiddenMethodInvocation(
    val target: Any,
    val method: Method,
    val args: Array<Any?>,
)

internal data class GenerateKeyReplyCaptureSnapshot(
    val rawRequest: ByteArray? = null,
    val requestPrefix: String? = null,
    val rawReply: ByteArray? = null,
    val rawPrefix: String? = null,
    val transactionCode: Int? = null,
    val transactReturned: Boolean? = null,
    val throwable: Throwable? = null,
    val failureReason: String? = null,
)

internal class GenerateKeyReplyCaptureSlot {
    var rawRequest: ByteArray? = null
    var requestPrefix: String? = null
    var rawReply: ByteArray? = null
    var rawPrefix: String? = null
    var transactionCode: Int? = null
    var transactReturned: Boolean? = null
    var failureReason: String? = null
    var completed: Boolean = false

    fun toSnapshot(
        throwable: Throwable? = null,
        defaultFailureReason: String,
    ): GenerateKeyReplyCaptureSnapshot {
        return GenerateKeyReplyCaptureSnapshot(
            rawRequest = rawRequest,
            requestPrefix = requestPrefix,
            rawReply = rawReply,
            rawPrefix = rawPrefix,
            transactionCode = transactionCode,
            transactReturned = transactReturned,
            throwable = throwable,
            failureReason = failureReason ?: if (rawReply == null) defaultFailureReason else null,
        )
    }
}
