package com.kaizehuang.vernier.features.lsposed.data.probes

import com.kaizehuang.vernier.features.lsposed.domain.LSPosedProbe
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignal
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignalGroup
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignalSeverity

data class LSPosedHookCallbackProbeResult(
    val signals: List<LSPosedSignal>,
    val hitCount: Int,
)

class LSPosedHookCallbackProbe {

    fun run(): LSPosedHookCallbackProbeResult {
        return evaluate(Thread.getDefaultUncaughtExceptionHandler())
    }

    internal fun evaluate(
        handler: Thread.UncaughtExceptionHandler?,
    ): LSPosedHookCallbackProbeResult {
        val handlerClassName = handler?.javaClass?.name
            ?.takeIf(::containsFrameworkToken)
            ?: return LSPosedHookCallbackProbeResult(
                signals = emptyList(),
                hitCount = 0,
            )

        val signal = LSPosedSignal(
            id = "hook_callback_handler",
            probe = LSPosedProbe.HOOK_CALLBACK,
            label = "Default exception handler",
            value = "Injected",
            group = LSPosedSignalGroup.RUNTIME,
            severity = LSPosedSignalSeverity.DANGER,
            detail = handlerClassName,
            detailMonospace = true,
        )

        return LSPosedHookCallbackProbeResult(
            signals = listOf(signal),
            hitCount = 1,
        )
    }
}
