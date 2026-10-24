package com.kaizehuang.vernier.features.virtualization.data.probes

import android.os.Build
import com.kaizehuang.vernier.core.platform.HiddenSystemProperties
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignal
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalGroup
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity

open class VirtualizationPropertyProbe {

    open fun probe(): List<VirtualizationSignal> {
        val signals = mutableListOf<VirtualizationSignal>()

        val roKernelQemu = readProperty("ro.kernel.qemu")
        if (roKernelQemu == "1") {
            signals += VirtualizationSignal(
                id = "virt_prop_ro_kernel_qemu",
                label = "ro.kernel.qemu",
                value = "Guest",
                group = VirtualizationSignalGroup.ENVIRONMENT,
                severity = VirtualizationSignalSeverity.DANGER,
                detail = "ro.kernel.qemu=1 is a direct emulator guest property.",
            )
        }

        val qemuGuestProps = listOf(
            "ro.boot.qemu" to readProperty("ro.boot.qemu"),
            "ro.boot.qemu.avd_name" to readProperty("ro.boot.qemu.avd_name"),
            "qemu.sf.lcd_density" to readProperty("qemu.sf.lcd_density"),
        ).filter { (_, value) -> value.isNotBlank() && value != "0" }
        if (qemuGuestProps.isNotEmpty()) {
            signals += VirtualizationSignal(
                id = "virt_prop_qemu_guest_props",
                label = "QEMU guest properties",
                value = "${qemuGuestProps.size} hit(s)",
                group = VirtualizationSignalGroup.ENVIRONMENT,
                severity = VirtualizationSignalSeverity.DANGER,
                detail = qemuGuestProps.joinToString(separator = "\n") { "${it.first}=${it.second}" },
                detailMonospace = true,
            )
        }

        val hardwareHits = listOf(
            "ro.hardware" to readProperty("ro.hardware"),
            "ro.boot.hardware" to readProperty("ro.boot.hardware"),
            "ro.product.board" to readProperty("ro.product.board"),
            "ro.board.platform" to readProperty("ro.board.platform"),
        ).filter { (_, value) ->
            value.contains("goldfish", ignoreCase = true) ||
                    value.contains("ranchu", ignoreCase = true)
        }
        if (hardwareHits.isNotEmpty()) {
            signals += VirtualizationSignal(
                id = "virt_prop_hardware_cluster",
                label = "Emulator hardware props",
                value = hardwareHits.joinToString(separator = ", ") { it.second },
                group = VirtualizationSignalGroup.ENVIRONMENT,
                severity = VirtualizationSignalSeverity.DANGER,
                detail = hardwareHits.joinToString(separator = "\n") { "${it.first}=${it.second}" },
                detailMonospace = true,
            )
        }

        val nativeBridge = readProperty("ro.dalvik.vm.native.bridge")
        if (nativeBridge.isNotBlank() && nativeBridge != "0") {
            signals += VirtualizationSignal(
                id = "virt_prop_native_bridge",
                label = "ro.dalvik.vm.native.bridge",
                value = nativeBridge,
                group = VirtualizationSignalGroup.TRANSLATION,
                severity = VirtualizationSignalSeverity.WARNING,
                detail = "ART native bridge is configured for translated execution.",
                detailMonospace = true,
            )

            val abiList = readProperty("ro.product.cpu.abilist")
            if (
                abiList.contains("x86", ignoreCase = true) &&
                Build.SUPPORTED_ABIS.any { abi -> abi.startsWith("arm") || abi.startsWith("armeabi") }
            ) {
                signals += VirtualizationSignal(
                    id = "virt_prop_native_bridge_abi_surface",
                    label = "Translated ABI surface",
                    value = "Mixed",
                    group = VirtualizationSignalGroup.TRANSLATION,
                    severity = VirtualizationSignalSeverity.WARNING,
                    detail = "ro.product.cpu.abilist=$abiList while Build.SUPPORTED_ABIS=${Build.SUPPORTED_ABIS.joinToString()}",
                    detailMonospace = true,
                )
            }
        }

        val hypervisorCapability = readProperty("ro.boot.hypervisor.vm.supported")
        if (hypervisorCapability.isNotBlank() && hypervisorCapability != "0") {
            signals += VirtualizationSignal(
                id = "virt_prop_hypervisor_capability",
                label = "Hypervisor capability",
                value = hypervisorCapability,
                group = VirtualizationSignalGroup.ENVIRONMENT,
                severity = VirtualizationSignalSeverity.INFO,
                detail = "Capability-only signal. This does not mean the current app is running inside a guest.",
            )
        }

        return signals.distinctBy { it.id }
    }

    protected open fun readProperty(name: String): String {
        return HiddenSystemProperties.read(name).getOrNull().orEmpty().trim()
    }
}
