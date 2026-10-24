package com.kaizehuang.vernier.features.virtualization.data.repository

import com.kaizehuang.vernier.capability.earlypreload.data.EarlyVirtualizationPreloadResult
import com.kaizehuang.vernier.capability.earlypreload.data.EarlyVirtualizationPreloadSignal
import com.kaizehuang.vernier.capability.helperprocess.data.SacrificialSyscallPackResult
import com.kaizehuang.vernier.capability.helperprocess.data.VirtualizationTrapResult
import com.kaizehuang.vernier.features.virtualization.data.probes.VirtualizationHostAppProbeResult
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationImpact
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignal
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalGroup
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity

internal fun VirtualizationRepository.buildPreloadSignals(preloadResult: EarlyVirtualizationPreloadResult): List<VirtualizationSignal> {
    if (!preloadResult.hasRun) return emptyList()
    return preloadResult.activeSignals.map { signal ->
        VirtualizationSignal(
            id = "virt_preload_${signal.key.lowercase()}",
            label = "Startup preload ${signal.label}",
            value = if (signal.isDanger) "Detected" else "Review",
            group = if (signal == EarlyVirtualizationPreloadSignal.NATIVE_BRIDGE) {
                VirtualizationSignalGroup.TRANSLATION
            } else {
                VirtualizationSignalGroup.RUNTIME
            },
            severity = if (signal.isDanger) {
                VirtualizationSignalSeverity.DANGER
            } else {
                VirtualizationSignalSeverity.WARNING
            },
            detail = buildString {
                append("Source=startup preload")
                preloadResult.details.takeIf { it.isNotBlank() }?.let { detail ->
                    append(" | ")
                    append(detail)
                }
                preloadResult.mountNamespaceInode.takeIf { it.isNotBlank() }?.let {
                    append(" | mnt_ns=")
                    append(it)
                }
            },
        )
    }
}

internal fun VirtualizationRepository.buildHostAppSignals(result: VirtualizationHostAppProbeResult): List<VirtualizationSignal> {
    return result.findings.map { finding ->
        VirtualizationSignal(
            id = "virt_host_${finding.target.packageName}",
            label = finding.target.appName,
            value = "Corroboration",
            group = VirtualizationSignalGroup.HOST_APPS,
            severity = VirtualizationSignalSeverity.INFO,
            detail = finding.methods.joinToString(separator = "\n") { method ->
                method.detail?.let { "${method.kind.label}: $it" } ?: method.kind.label
            },
            detailMonospace = true,
        )
    }
}

internal fun VirtualizationRepository.buildHoneypotSignals(
    nativeTimingTrap: VirtualizationTrapResult,
    nativeSyscallParityTrap: VirtualizationTrapResult,
    asmCounterTrap: VirtualizationTrapResult,
    asmRawSyscallTrap: VirtualizationTrapResult,
    syscallPackResult: SacrificialSyscallPackResult,
): List<VirtualizationSignal> = buildList {
    addTrapSignal("Native timing trap", "virt_trap_native_timing", nativeTimingTrap)
    addTrapSignal(
        "Native syscall parity trap",
        "virt_trap_native_syscall_parity",
        nativeSyscallParityTrap,
    )
    addTrapSignal("ASM counter trap", "virt_trap_asm_counter", asmCounterTrap)
    addTrapSignal("ASM raw syscall trap", "virt_trap_asm_syscall", asmRawSyscallTrap)
    syscallPackResult.suspiciousItems.forEach { item ->
        add(
            VirtualizationSignal(
                id = "virt_trap_pack_${item.label.lowercase().replace(' ', '_')}",
                label = "Sacrificial ${item.label}",
                value = "${item.suspiciousAttempts}/${item.completedAttempts}",
                group = VirtualizationSignalGroup.HONEYPOT,
                severity = VirtualizationSignalSeverity.WARNING,
                detail = item.detail.ifBlank {
                    item.attempts.joinToString(separator = "\n") { attempt -> attempt.detail }
                },
                detailMonospace = true,
            ),
        )
    }
}

internal fun VirtualizationRepository.buildImpacts(
    signals: List<VirtualizationSignal>,
    hostAppResult: VirtualizationHostAppProbeResult,
): List<VirtualizationImpact> {
    if (signals.isEmpty()) {
        return listOf(
            VirtualizationImpact(
                text = "No direct emulator, AVF guest, translation, classpath, or consistency drift signal surfaced from the current app context.",
                severity = VirtualizationSignalSeverity.SAFE,
            ),
        )
    }
    if (
        signals.none {
            it.severity == VirtualizationSignalSeverity.DANGER ||
                    it.severity == VirtualizationSignalSeverity.WARNING
        } &&
        hostAppResult.findings.isNotEmpty()
    ) {
        return listOf(
            VirtualizationImpact(
                text = "Known virtualization host apps are installed, but current process probes did not confirm guest or translated execution.",
                severity = VirtualizationSignalSeverity.INFO,
            ),
        )
    }
    return signals.take(5).map { signal ->
        VirtualizationImpact(
            text = buildString {
                append(signal.label)
                signal.detail?.takeIf { it.isNotBlank() }?.let { detail ->
                    append(": ")
                    append(detail.lineSequence().firstOrNull().orEmpty())
                }
            },
            severity = signal.severity,
        )
    }
}

private fun MutableList<VirtualizationSignal>.addTrapSignal(
    label: String,
    id: String,
    result: VirtualizationTrapResult,
) {
    if (!result.suspicious) return
    add(
        VirtualizationSignal(
            id = id,
            label = label,
            value = "${result.suspiciousAttempts}/${result.completedAttempts}",
            group = VirtualizationSignalGroup.HONEYPOT,
            severity = VirtualizationSignalSeverity.WARNING,
            detail = result.detail,
            detailMonospace = true,
        ),
    )
}

internal fun severityPriority(severity: VirtualizationSignalSeverity): Int {
    return when (severity) {
        VirtualizationSignalSeverity.DANGER -> 0
        VirtualizationSignalSeverity.WARNING -> 1
        VirtualizationSignalSeverity.INFO -> 2
        VirtualizationSignalSeverity.SAFE -> 3
    }
}

internal fun groupPriority(group: VirtualizationSignalGroup): Int {
    return when (group) {
        VirtualizationSignalGroup.ENVIRONMENT -> 0
        VirtualizationSignalGroup.TRANSLATION -> 1
        VirtualizationSignalGroup.RUNTIME -> 2
        VirtualizationSignalGroup.CONSISTENCY -> 3
        VirtualizationSignalGroup.HONEYPOT -> 4
        VirtualizationSignalGroup.HOST_APPS -> 5
    }
}
