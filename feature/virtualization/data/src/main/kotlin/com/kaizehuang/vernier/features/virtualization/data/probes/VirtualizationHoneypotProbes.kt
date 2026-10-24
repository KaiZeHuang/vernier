package com.kaizehuang.vernier.features.virtualization.data.probes

import com.kaizehuang.vernier.capability.helperprocess.data.VirtualizationNativeBridge
import com.kaizehuang.vernier.capability.helperprocess.data.VirtualizationTrapResult

open class NativeTimingTrapProbe(
    private val nativeBridge: VirtualizationNativeBridge = VirtualizationNativeBridge(),
) {
    open fun probe(): VirtualizationTrapResult = nativeBridge.runTimingTrap()
}

open class NativeSyscallParityTrapProbe(
    private val nativeBridge: VirtualizationNativeBridge = VirtualizationNativeBridge(),
) {
    open fun probe(): VirtualizationTrapResult = nativeBridge.runSyscallParityTrap()
}

open class AsmCounterTrapProbe(
    private val nativeBridge: VirtualizationNativeBridge = VirtualizationNativeBridge(),
) {
    open fun probe(): VirtualizationTrapResult = nativeBridge.runAsmCounterTrap()
}

open class AsmRawSyscallTrapProbe(
    private val nativeBridge: VirtualizationNativeBridge = VirtualizationNativeBridge(),
) {
    open fun probe(): VirtualizationTrapResult = nativeBridge.runAsmRawSyscallTrap()
}
