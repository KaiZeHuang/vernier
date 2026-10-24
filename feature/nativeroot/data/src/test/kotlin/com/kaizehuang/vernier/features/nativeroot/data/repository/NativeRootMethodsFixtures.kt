package com.kaizehuang.vernier.features.nativeroot.data.repository

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.features.nativeroot.data.native.NativeRootNativeSnapshot
import com.kaizehuang.vernier.features.nativeroot.data.probes.CgroupProcessLeakProbeResult
import com.kaizehuang.vernier.features.nativeroot.data.probes.KernelSuManagerFingerprintProbeResult
import com.kaizehuang.vernier.features.nativeroot.data.probes.KernelSuThroneHuntProbeResult
import com.kaizehuang.vernier.features.nativeroot.data.probes.MountNamespaceDriftProbeResult
import com.kaizehuang.vernier.features.nativeroot.data.probes.TempRootArtifactProbeResult
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootMethodResult

/** The method row named [label], built from [snapshot] with every Kotlin-side probe left unavailable. */
internal fun methodRow(snapshot: NativeRootNativeSnapshot, label: String): NativeRootMethodResult = buildMethods(
    snapshot = snapshot,
    findings = emptyList(),
    shellTmpDetail = "",
    rootProcessDetail = "",
    cgroupResult = CgroupProcessLeakProbeResult(false, 0, 0, 0, 0, emptyList(), ""),
    mountNamespaceResult = MountNamespaceDriftProbeResult(false, false, "", "", 0, emptyList(), ""),
    managerFingerprintResult = KernelSuManagerFingerprintProbeResult(
        false,
        InstalledPackageVisibility.UNKNOWN,
        false,
        0,
        emptyList(),
        "",
    ),
    tempRootArtifactResult = TempRootArtifactProbeResult(false, 0, emptyList(), false, false, ""),
    throneHuntResult = KernelSuThroneHuntProbeResult(
        available = false,
        watchInstalled = false,
        watchDenied = false,
        packageDirectory = "",
        watchDescriptor = -1,
        directoryOpenCount = 0,
        directoryAccessCount = 0,
        rawEventCount = 0,
        invalidEventCount = 0,
        baselineHitCount = 0,
        stimulusDetail = "",
        findings = emptyList(),
        detail = "",
    ),
).single { it.label == label }
