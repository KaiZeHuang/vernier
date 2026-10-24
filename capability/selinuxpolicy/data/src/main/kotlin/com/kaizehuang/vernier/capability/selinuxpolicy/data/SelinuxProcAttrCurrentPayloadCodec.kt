package com.kaizehuang.vernier.capability.selinuxpolicy.data

import com.kaizehuang.vernier.core.native.NativePayloadCodec

/**
 * Four tab-separated columns carrying one `/proc/self/attr/current` probe result.
 *
 * Columns are escaped with [NativePayloadCodec], the same contract the native bridges use, so a tab
 * inside a value cannot be mistaken for a column boundary. These values come from the kernel and
 * from exception messages, so their contents are not ours to assume.
 */
internal object SelinuxProcAttrCurrentPayloadCodec {

    fun encode(result: SelinuxProcAttrCurrentResult): String {
        return listOf(
            result.label,
            result.targetContext,
            result.outcomeClass,
            result.rawMessage,
        ).joinToString(FIELD_SEPARATOR, transform = NativePayloadCodec::encodeValue)
    }

    /** Returns `null` when [payload] is not exactly [COLUMN_COUNT] columns, meaning it is not ours. */
    fun decode(payload: String): SelinuxProcAttrCurrentResult? {
        val parts = payload.split(FIELD_SEPARATOR)
        if (parts.size != COLUMN_COUNT) {
            return null
        }
        return SelinuxProcAttrCurrentResult(
            label = NativePayloadCodec.decodeValue(parts[0]),
            targetContext = NativePayloadCodec.decodeValue(parts[1]),
            outcomeClass = NativePayloadCodec.decodeValue(parts[2]),
            rawMessage = NativePayloadCodec.decodeValue(parts[3]),
        )
    }

    private const val FIELD_SEPARATOR = "\t"

    private const val COLUMN_COUNT = 4
}
