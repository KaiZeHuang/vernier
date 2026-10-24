package com.kaizehuang.vernier.sdk

import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.features.adbruntime.detector.AdbRuntimeDetector
import com.kaizehuang.vernier.features.bootloader.detector.BootloaderDetector
import com.kaizehuang.vernier.features.customrom.detector.CustomRomDetector
import com.kaizehuang.vernier.features.dangerousapps.detector.DangerousAppsDetector
import com.kaizehuang.vernier.features.heapresidue.detector.HeapResidueDetector
import com.kaizehuang.vernier.features.kernelcheck.detector.KernelCheckDetector
import com.kaizehuang.vernier.features.lsposed.detector.LSPosedDetector
import com.kaizehuang.vernier.features.memory.detector.MemoryDetector
import com.kaizehuang.vernier.features.mount.detector.MountDetector
import com.kaizehuang.vernier.features.nativeroot.detector.NativeRootDetector
import com.kaizehuang.vernier.features.playintegrityfix.detector.PlayIntegrityFixDetector
import com.kaizehuang.vernier.features.rootmanagers.detector.RootManagersDetector
import com.kaizehuang.vernier.features.selinux.detector.SelinuxDetector
import com.kaizehuang.vernier.features.su.detector.SuDetector
import com.kaizehuang.vernier.features.systemproperties.detector.SystemPropertiesDetector
import com.kaizehuang.vernier.features.tee.detector.TeeDetector
import com.kaizehuang.vernier.features.virtualization.detector.VirtualizationDetector
import com.kaizehuang.vernier.features.zygisk.detector.ZygiskDetector

/**
 * Every detector, in the order their scans start.
 *
 * This is the one list of detectors: the SDK scans in this order, and the application starts its
 * dashboard sessions in this order. Bootloader and TEE start first, as the application has always
 * started them; every other detector follows by name. Adding a detector means adding it here and
 * giving it a dashboard card.
 */
public object DetectorCatalog {
    public val all: List<Detector<*, *>> = listOf(
        BootloaderDetector,
        TeeDetector,
        AdbRuntimeDetector,
        CustomRomDetector,
        DangerousAppsDetector,
        HeapResidueDetector,
        KernelCheckDetector,
        LSPosedDetector,
        MemoryDetector,
        MountDetector,
        NativeRootDetector,
        PlayIntegrityFixDetector,
        RootManagersDetector,
        SelinuxDetector,
        SuDetector,
        SystemPropertiesDetector,
        VirtualizationDetector,
        ZygiskDetector,
    )
}
