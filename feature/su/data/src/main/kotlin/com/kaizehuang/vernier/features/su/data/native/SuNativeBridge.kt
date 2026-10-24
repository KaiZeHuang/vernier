package com.kaizehuang.vernier.features.su.data.native

import com.kaizehuang.vernier.core.native.NativePayloadCodec
import com.kaizehuang.vernier.core.native.NativePayloadContract
import com.kaizehuang.vernier.core.native.NativeSnapshotCollector

class SuNativeBridge(
    private val collector: NativeSnapshotCollector = NativeSnapshotCollector.Default,
) {

    fun collectSnapshot(): SuNativeSnapshot = collector.collect(
        readPayload = ::nativeCollectSnapshot,
        parse = ::parse,
        unavailable = { status -> SuNativeSnapshot(collection = status) },
    )

    internal fun parse(raw: String): SuNativeSnapshot {
        if (raw.isBlank()) {
            return SuNativeSnapshot()
        }

        NativePayloadContract.requireKeys(raw, "AVAILABLE")

        val entries = raw.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.contains('=') }
            .map { it.substringBefore('=') to it.substringAfter('=') }
            .toList()

        return SuNativeSnapshot(
            available = NativePayloadCodec.decodeFlag(entries.firstOrNull { it.first == "AVAILABLE" }?.second),
            selfContext = entries.firstOrNull { it.first == "SELF_CONTEXT" }?.second.orEmpty(),
            selfContextAbnormal = NativePayloadCodec.decodeFlag(
                entries.firstOrNull { it.first == "SELF_ABNORMAL" }?.second,
            ),
            suspiciousProcesses = entries.filter { it.first == "PROC" }.map { it.second },
            checkedProcesses = entries.firstOrNull { it.first == "PROC_CHECKED" }?.second?.toIntOrNull()
                ?: 0,
            deniedProcesses = entries.firstOrNull { it.first == "PROC_DENIED" }?.second?.toIntOrNull()
                ?: 0,
        )
    }

    private external fun nativeCollectSnapshot(): String
}
