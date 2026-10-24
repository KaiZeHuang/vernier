package com.kaizehuang.vernier.features.kernelcheck.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

data class KernelCheckNativeSnapshot(
    val available: Boolean = false,
    val procVersion: String = "",
    val procCmdline: String = "",
    val utsRelease: String = "",
    val utsVersion: String = "",
    val sysctlOsRelease: String = "",
    val sysctlVersion: String = "",
    val suspiciousCmdline: Boolean = false,
    val kptrExposed: Boolean = false,
    val findings: List<String> = emptyList(),
    val cpuIdentityStatus: Arm64CpuIdentityProbeStatus = Arm64CpuIdentityProbeStatus.UNKNOWN,
    val cpuIdentityObservations: List<Arm64CpuIdentityObservation> = emptyList(),
    /**
     * Why this snapshot is or is not usable. [available] alone cannot distinguish "the probe ran and
     * found nothing" from "the probe never ran", so the reason is carried here.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
)
