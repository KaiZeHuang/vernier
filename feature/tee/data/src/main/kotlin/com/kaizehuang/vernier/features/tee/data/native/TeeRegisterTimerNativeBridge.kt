package com.kaizehuang.vernier.features.tee.data.native

import com.kaizehuang.vernier.core.native.VernierNativeLibrary
import com.kaizehuang.vernier.core.native.NativePayloadCodec

class TeeRegisterTimerNativeBridge {

    fun isNativeAvailable(): Boolean = nativeLoaded

    fun isRegisterTimerAvailable(): Boolean {
        if (!nativeLoaded) {
            return false
        }
        return runCatching {
            nativeIsRegisterTimerAvailable()
        }.getOrDefault(false)
    }

    fun readRegisterTimerNs(): Long? {
        if (!nativeLoaded) {
            return null
        }
        return runCatching {
            nativeReadRegisterTimerNs().takeIf { it >= 0L }
        }.getOrNull()
    }

    fun bindCurrentThreadToCpu0(): Boolean {
        if (!nativeLoaded) {
            return false
        }
        return runCatching {
            nativeBindCurrentThreadToCpu0()
        }.getOrDefault(false)
    }

    fun restoreCurrentThreadAffinity(): Boolean {
        if (!nativeLoaded) {
            return false
        }
        return runCatching {
            nativeRestoreCurrentThreadAffinity()
        }.getOrDefault(false)
    }

    fun selectPreferredTimer(requestCpu0Affinity: Boolean = true): TeeRegisterTimerSelection {
        if (!nativeLoaded) {
            return TeeRegisterTimerSelection(
                fallbackReason = "native bridge unavailable",
                affinityStatus = if (requestCpu0Affinity) "native_unavailable" else "not_requested",
            )
        }
        return runCatching {
            parseSelection(nativeSelectPreferredTimer(requestCpu0Affinity))
        }.getOrDefault(
            TeeRegisterTimerSelection(
                fallbackReason = "timer selection failed",
                affinityStatus = if (requestCpu0Affinity) "selection_failed" else "not_requested",
            ),
        )
    }

    internal fun parseSelection(raw: String): TeeRegisterTimerSelection {
        val values = parseKeyValueLines(raw)
        return TeeRegisterTimerSelection(
            registerTimerAvailable = values["REGISTER_TIMER_AVAILABLE"].asBool(),
            timerSource = values["TIMER_SOURCE"] ?: "clock_monotonic",
            fallbackReason = values["FALLBACK_REASON"]?.takeIf { it.isNotBlank() },
            affinityStatus = values["AFFINITY"] ?: "not_requested",
        )
    }

    private fun parseKeyValueLines(raw: String): Map<String, String> {
        return buildMap {
            raw.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() && it.contains('=') }
                .forEach { line ->
                    put(
                        line.substringBefore('='),
                        NativePayloadCodec.decodeValue(line.substringAfter('=')),
                    )
                }
        }
    }

    private fun String?.asBool(): Boolean = NativePayloadCodec.decodeFlag(this)

    private external fun nativeIsRegisterTimerAvailable(): Boolean

    private external fun nativeReadRegisterTimerNs(): Long

    private external fun nativeBindCurrentThreadToCpu0(): Boolean

    private external fun nativeRestoreCurrentThreadAffinity(): Boolean

    private external fun nativeSelectPreferredTimer(requestCpu0Affinity: Boolean): String

    companion object {
        private val nativeLoaded: Boolean
            get() = VernierNativeLibrary.isLoaded
    }
}
