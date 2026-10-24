package com.kaizehuang.vernier.features.virtualization.data.repository

import com.kaizehuang.vernier.capability.earlypreload.data.EarlyVirtualizationPreloadResult
import com.kaizehuang.vernier.capability.helperprocess.data.HelperProbeManager
import com.kaizehuang.vernier.capability.helperprocess.data.HelperProcessProfile
import com.kaizehuang.vernier.capability.helperprocess.data.HelperProcessSnapshot
import com.kaizehuang.vernier.capability.helperprocess.data.IsolatedHelperProbeManager
import com.kaizehuang.vernier.capability.helperprocess.data.SacrificialSyscallPackResult
import com.kaizehuang.vernier.capability.helperprocess.data.VirtualizationNativeBridge
import com.kaizehuang.vernier.capability.helperprocess.data.VirtualizationNativeSnapshot
import com.kaizehuang.vernier.capability.helperprocess.data.VirtualizationTrapResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.features.virtualization.data.probes.AsmCounterTrapProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.AsmRawSyscallTrapProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.DexPathProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.DexPathProbeResult
import com.kaizehuang.vernier.features.virtualization.data.probes.NativeSyscallParityTrapProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.NativeTimingTrapProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.UidIdentityProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.UidIdentityProbeResult
import com.kaizehuang.vernier.features.virtualization.data.probes.VirtualizationBuildProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.VirtualizationHostAppProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.VirtualizationHostAppProbeResult
import com.kaizehuang.vernier.features.virtualization.data.probes.VirtualizationPropertyProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.VirtualizationServiceProbe
import com.kaizehuang.vernier.features.virtualization.data.probes.VirtualizationServiceProbeResult
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignal
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalGroup
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity

internal fun repository(
    propertySignals: List<VirtualizationSignal> = emptyList(),
    buildSignals: List<VirtualizationSignal> = emptyList(),
    serviceResult: VirtualizationServiceProbeResult = VirtualizationServiceProbeResult(
        0,
        emptyList()
    ),
    dexPathResult: DexPathProbeResult = DexPathProbeResult(),
    uidIdentityResult: UidIdentityProbeResult = UidIdentityProbeResult(),
    nativeSnapshot: VirtualizationNativeSnapshot = VirtualizationNativeSnapshot(available = true),
    preloadResult: EarlyVirtualizationPreloadResult = EarlyVirtualizationPreloadResult.empty(),
    remoteSnapshot: HelperProcessSnapshot = HelperProcessSnapshot(),
    isolatedSnapshot: HelperProcessSnapshot = HelperProcessSnapshot(
        profile = HelperProcessProfile.ISOLATED,
    ),
    hostAppResult: VirtualizationHostAppProbeResult = VirtualizationHostAppProbeResult(
        packageVisibility = InstalledPackageVisibility.FULL,
        findings = emptyList(),
    ),
    processInfo: VirtualizationProcessInfo = VirtualizationProcessInfo(),
    syscallPackResult: SacrificialSyscallPackResult = SacrificialSyscallPackResult(),
    rendererRequests: MutableList<Boolean> = mutableListOf(),
): VirtualizationRepository {
    return VirtualizationRepository(
        propertyProbe = object : VirtualizationPropertyProbe() {
            override fun probe(): List<VirtualizationSignal> = propertySignals
        },
        buildProbe = object : VirtualizationBuildProbe() {
            override fun probe(): List<VirtualizationSignal> = buildSignals
        },
        serviceProbe = object : VirtualizationServiceProbe() {
            override fun probe(): VirtualizationServiceProbeResult = serviceResult
        },
        dexPathProbe = object : DexPathProbe() {
            override fun probe(): DexPathProbeResult = dexPathResult
        },
        uidIdentityProbe = object : UidIdentityProbe() {
            override fun probe(): UidIdentityProbeResult = uidIdentityResult
        },
        nativeBridge = object : VirtualizationNativeBridge() {
            override fun collectSnapshot(probeRenderer: Boolean): VirtualizationNativeSnapshot {
                rendererRequests += probeRenderer
                return nativeSnapshot
            }
        },
        hostAppProbe = object : VirtualizationHostAppProbe() {
            override fun probe(): VirtualizationHostAppProbeResult = hostAppResult
        },
        probeManager = object : HelperProbeManager() {
            override suspend fun collect(): HelperProcessSnapshot = remoteSnapshot
            override suspend fun runSacrificialSyscallPack(): SacrificialSyscallPackResult =
                syscallPackResult
        },
        isolatedProbeManager = object : IsolatedHelperProbeManager() {
            override suspend fun collect(): HelperProcessSnapshot = isolatedSnapshot
        },
        nativeTimingTrapProbe = object : NativeTimingTrapProbe() {
            override fun probe(): VirtualizationTrapResult = VirtualizationTrapResult()
        },
        nativeSyscallParityTrapProbe = object : NativeSyscallParityTrapProbe() {
            override fun probe(): VirtualizationTrapResult = VirtualizationTrapResult()
        },
        asmCounterTrapProbe = object : AsmCounterTrapProbe() {
            override fun probe(): VirtualizationTrapResult = VirtualizationTrapResult()
        },
        asmRawSyscallTrapProbe = object : AsmRawSyscallTrapProbe() {
            override fun probe(): VirtualizationTrapResult = VirtualizationTrapResult()
        },
        preloadResultProvider = { preloadResult },
        processInfoProvider = { processInfo },
    )
}

internal fun signal(
    id: String,
    label: String,
    group: VirtualizationSignalGroup,
    severity: VirtualizationSignalSeverity,
): VirtualizationSignal {
    return VirtualizationSignal(
        id = id,
        label = label,
        value = label,
        group = group,
        severity = severity,
        detail = label,
    )
}
