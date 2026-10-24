package com.kaizehuang.vernier.features.virtualization.data.probes

import com.kaizehuang.vernier.core.platform.HiddenServiceManager
import com.kaizehuang.vernier.core.platform.PlatformFailureName
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignal
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalGroup
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity

data class VirtualizationServiceProbeResult(
    val listedServiceCount: Int,
    val signals: List<VirtualizationSignal>,
    /** Null when every ServiceManager lookup answered; otherwise names the first failure. */
    val failure: String? = null,
) {
    val available: Boolean
        get() = failure == null
}

open class VirtualizationServiceProbe {

    open fun probe(): VirtualizationServiceProbeResult {
        val listed = HiddenServiceManager.listServices()
        val qemud = HiddenServiceManager.getService("qemud")
        val virtualizationService = HiddenServiceManager.getService("android.system.virtualizationservice")
        val listedServices = listed.getOrNull().orEmpty()

        val signals = mutableListOf<VirtualizationSignal>()
        if (qemud.getOrNull() != null || listedServices.any { it.equals("qemud", ignoreCase = true) }) {
            signals += VirtualizationSignal(
                id = "virt_service_qemud",
                label = "qemud service",
                value = "Present",
                group = VirtualizationSignalGroup.RUNTIME,
                severity = VirtualizationSignalSeverity.DANGER,
                detail = "ServiceManager exposed qemud, which is a direct emulator guest service.",
            )
        }
        if (
            virtualizationService.getOrNull() != null ||
            listedServices.any { it.contains("virtualizationservice", ignoreCase = true) }
        ) {
            signals += VirtualizationSignal(
                id = "virt_service_virtualizationservice",
                label = "VirtualizationService",
                value = "Present",
                group = VirtualizationSignalGroup.ENVIRONMENT,
                severity = VirtualizationSignalSeverity.INFO,
                detail = "Capability-only service. It does not imply the current process is inside a guest.",
            )
        }

        return VirtualizationServiceProbeResult(
            listedServiceCount = listedServices.size,
            signals = signals,
            failure = listOf(listed, qemud, virtualizationService)
                .firstNotNullOfOrNull { it.exceptionOrNull() }
                ?.let(PlatformFailureName::of),
        )
    }
}
