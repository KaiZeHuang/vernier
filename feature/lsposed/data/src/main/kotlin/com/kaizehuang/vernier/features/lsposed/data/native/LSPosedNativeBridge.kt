package com.kaizehuang.vernier.features.lsposed.data.native

import com.kaizehuang.vernier.core.native.NativePayloadCodec
import com.kaizehuang.vernier.core.native.NativePayloadContract
import com.kaizehuang.vernier.core.native.NativeSnapshotCollector

class LSPosedNativeBridge(
    private val collector: NativeSnapshotCollector = NativeSnapshotCollector.Default,
) {

    fun collectSnapshot(): LSPosedNativeSnapshot = collector.collect(
        readPayload = ::nativeCollectSnapshot,
        parse = ::parse,
        unavailable = { status -> LSPosedNativeSnapshot(collection = status) },
    )

    internal fun parse(raw: String): LSPosedNativeSnapshot {
        if (raw.isBlank()) {
            return LSPosedNativeSnapshot()
        }

        NativePayloadContract.requireKeys(raw, "AVAILABLE")

        var snapshot = LSPosedNativeSnapshot()
        val traces = mutableListOf<LSPosedNativeTrace>()

        raw.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { line ->
                when {
                    line.startsWith("TRACE=") -> {
                        val parts = line.removePrefix("TRACE=").split('\t', limit = 4)
                        if (parts.size == 4) {
                            traces += LSPosedNativeTrace(
                                group = parts[0],
                                severity = parts[1],
                                label = parts[2].decodeValue(),
                                detail = parts[3].decodeValue(),
                            )
                        }
                    }

                    line.contains('=') -> {
                        val key = line.substringBefore('=')
                        val value = line.substringAfter('=')
                        snapshot = snapshot.applyEntry(key, value)
                    }
                }
            }

        return snapshot.copy(traces = traces)
    }

    private fun LSPosedNativeSnapshot.applyEntry(
        key: String,
        value: String,
    ): LSPosedNativeSnapshot {
        return when (key) {
            "AVAILABLE" -> copy(available = value.asBool())
            "MAPS_AVAILABLE" -> copy(mapsAvailable = value.asBool())
            "HEAP_AVAILABLE" -> copy(heapAvailable = value.asBool())
            "MAPS_HITS" -> copy(mapsHitCount = value.toIntOrNull() ?: mapsHitCount)
            "MAPS_SCANNED" -> copy(mapsScannedLines = value.toIntOrNull() ?: mapsScannedLines)
            "HEAP_HITS" -> copy(heapHitCount = value.toIntOrNull() ?: heapHitCount)
            "HEAP_SCANNED" -> copy(heapScannedRegions = value.toIntOrNull() ?: heapScannedRegions)
            else -> this
        }
    }

    private fun String.asBool(): Boolean = NativePayloadCodec.decodeFlag(this)

    private fun String.decodeValue(): String = NativePayloadCodec.decodeValue(this)

    private external fun nativeCollectSnapshot(): String
}
