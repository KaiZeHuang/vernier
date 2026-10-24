package com.kaizehuang.vernier.features.selinux.data.native

import com.kaizehuang.vernier.core.native.NativePayloadCodec
import com.kaizehuang.vernier.core.native.NativePayloadContract
import com.kaizehuang.vernier.core.native.NativeSnapshotCollector

open class SelinuxNativeAuditBridge(
    private val collector: NativeSnapshotCollector = NativeSnapshotCollector.Default,
) {

    open fun collectSnapshot(): SelinuxNativeAuditSnapshot = collector.collect(
        readPayload = ::nativeCollectAuditSnapshot,
        parse = ::parse,
        unavailable = { status ->
            // Previously an unloadable library produced a fixed reason while a probe that threw
            // produced no reason at all, which read as a successful scan that found no denials.
            SelinuxNativeAuditSnapshot(
                failureReason = status.explain("Native SELinux audit snapshot was unavailable"),
                collection = status,
            )
        },
    )

    internal fun parse(raw: String): SelinuxNativeAuditSnapshot {
        if (raw.isBlank()) {
            return SelinuxNativeAuditSnapshot()
        }

        NativePayloadContract.requireKeys(raw, "AVAILABLE")

        var snapshot = SelinuxNativeAuditSnapshot()
        val callbackLines = mutableListOf<String>()

        raw.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { line ->
                when {
                    line.startsWith("LINE=") -> callbackLines += line.removePrefix("LINE=")
                        .decodeValue()

                    line.contains('=') -> {
                        val key = line.substringBefore('=')
                        val value = line.substringAfter('=')
                        snapshot = snapshot.applyEntry(key, value)
                    }
                }
            }

        return snapshot.copy(callbackLines = callbackLines)
    }

    private fun SelinuxNativeAuditSnapshot.applyEntry(
        key: String,
        value: String,
    ): SelinuxNativeAuditSnapshot {
        return when (key) {
            "AVAILABLE" -> copy(available = value.asBool())
            "CALLBACK_INSTALLED" -> copy(callbackInstalled = value.asBool())
            "PROBE_RAN" -> copy(probeRan = value.asBool())
            "DENIAL_OBSERVED" -> copy(denialObserved = value.asBool())
            "ALLOW_OBSERVED" -> copy(allowObserved = value.asBool())
            "PROBE_MARKER" -> copy(probeMarker = value.decodeValue())
            "FAILURE_REASON" -> copy(failureReason = value.decodeValue())
            else -> this
        }
    }

    private fun String.asBool(): Boolean = NativePayloadCodec.decodeFlag(this)

    private fun String.decodeValue(): String = NativePayloadCodec.decodeValue(this)

    private external fun nativeCollectAuditSnapshot(): String
}
