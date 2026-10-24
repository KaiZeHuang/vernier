package com.kaizehuang.vernier.features.nativeroot.data.repository

import com.kaizehuang.vernier.features.nativeroot.data.native.NativeRootNativeSnapshot
import com.kaizehuang.vernier.features.nativeroot.data.probes.CgroupProcessLeakProbeResult
import com.kaizehuang.vernier.features.nativeroot.data.probes.KernelSuManagerFingerprintProbeResult
import com.kaizehuang.vernier.features.nativeroot.data.probes.KernelSuThroneHuntProbeResult
import com.kaizehuang.vernier.features.nativeroot.data.probes.MountNamespaceDriftProbeResult
import com.kaizehuang.vernier.features.nativeroot.data.probes.TempRootArtifactProbeResult
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootFinding
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootGroup
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootMethodResult

internal fun buildMethods(
    snapshot: NativeRootNativeSnapshot,
    findings: List<NativeRootFinding>,
    shellTmpDetail: String,
    rootProcessDetail: String,
    cgroupResult: CgroupProcessLeakProbeResult,
    mountNamespaceResult: MountNamespaceDriftProbeResult,
    managerFingerprintResult: KernelSuManagerFingerprintProbeResult,
    tempRootArtifactResult: TempRootArtifactProbeResult,
    throneHuntResult: KernelSuThroneHuntProbeResult,
): List<NativeRootMethodResult> {
    val directFindings =
        findings.filter { it.group == NativeRootGroup.SYSCALL || it.group == NativeRootGroup.SIDE_CHANNEL }
    val runtimeFindings =
        findings.filter {
            it.group == NativeRootGroup.PATH ||
                    it.group == NativeRootGroup.PROCESS ||
                    it.group == NativeRootGroup.PACKAGE
        }
    val kernelFindings = findings.filter { it.group == NativeRootGroup.KERNEL }
    val propertyFindings = findings.filter { it.group == NativeRootGroup.PROPERTY }

    val evidence = NativeRootMethodEvidence(
        snapshot,
        findings,
        shellTmpDetail,
        rootProcessDetail,
        cgroupResult,
        mountNamespaceResult,
        managerFingerprintResult,
        tempRootArtifactResult,
        throneHuntResult,
        directFindings,
        runtimeFindings,
        kernelFindings,
        propertyFindings,
    )
    return evidence.kernelInterfaceMethods() + evidence.namespaceAndArtifactMethods()
}

/** What every Native Root method row reads: the scan's evidence and its findings by group. */
internal class NativeRootMethodEvidence(
    val snapshot: NativeRootNativeSnapshot,
    val findings: List<NativeRootFinding>,
    val shellTmpDetail: String,
    val rootProcessDetail: String,
    val cgroupResult: CgroupProcessLeakProbeResult,
    val mountNamespaceResult: MountNamespaceDriftProbeResult,
    val managerFingerprintResult: KernelSuManagerFingerprintProbeResult,
    val tempRootArtifactResult: TempRootArtifactProbeResult,
    val throneHuntResult: KernelSuThroneHuntProbeResult,
    val directFindings: List<NativeRootFinding>,
    val runtimeFindings: List<NativeRootFinding>,
    val kernelFindings: List<NativeRootFinding>,
    val propertyFindings: List<NativeRootFinding>,
)
