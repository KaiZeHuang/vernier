package com.kaizehuang.vernier.features.heapresidue.data.ipc

import android.os.BadParcelableException
import android.os.IBinder
import android.os.Parcel

/** Parcel.enforceNoDataAvail exists only from API 33; this is the same check on every supported release. */
internal fun Parcel.requireFullyConsumed() {
    val unread = dataAvail()
    if (unread > 0) throw BadParcelableException("Parcel data not fully consumed, unread size: $unread")
}

internal object HeapDumpProtocol {
    const val SERVICE = "com.kaizehuang.vernier.heapresidue.dump"
    const val CALLBACK = "com.kaizehuang.vernier.heapresidue.result"
    const val DUMP = IBinder.FIRST_CALL_TRANSACTION
    const val RESULT = IBinder.FIRST_CALL_TRANSACTION
}

internal enum class HeapDumpStatus(val wire: Int) {
    COMPLETE(0), HIDDEN_API_UNAVAILABLE(1), DUMP_FAILED(2), REUSED_PROCESS(3);
    companion object {
        fun fromWire(value: Int): HeapDumpStatus = entries.firstOrNull { it.wire == value }
            ?: throw IllegalArgumentException("Unknown dump status")
    }
}

internal data class HeapDumpResult(
    val status: HeapDumpStatus,
    val ageMillis: Long = 0,
    val durationMillis: Long = 0,
    val gcCountBefore: Long = -1,
)
