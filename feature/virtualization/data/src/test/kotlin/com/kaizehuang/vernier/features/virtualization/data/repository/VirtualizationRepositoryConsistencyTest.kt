package com.kaizehuang.vernier.features.virtualization.data.repository

import com.kaizehuang.vernier.capability.helperprocess.data.HelperProcessProfile
import com.kaizehuang.vernier.capability.helperprocess.data.HelperProcessSnapshot
import com.kaizehuang.vernier.capability.helperprocess.data.VirtualizationNativeFinding
import com.kaizehuang.vernier.capability.helperprocess.data.VirtualizationNativeSnapshot
import com.kaizehuang.vernier.features.virtualization.data.probes.DexPathProbeResult
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationMethodOutcome
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalGroup
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VirtualizationRepositoryConsistencyTest {

    @Test
    fun `main and helper drift create consistency hit`() = runBlocking {
        val report = repository(
            nativeSnapshot = VirtualizationNativeSnapshot(
                available = true,
                findings = listOf(
                    VirtualizationNativeFinding(
                        "RUNTIME",
                        "DANGER",
                        "Emulator device node",
                        "/dev/qemu_pipe",
                        "/dev/qemu_pipe"
                    ),
                ),
            ),
            remoteSnapshot = HelperProcessSnapshot(
                available = true,
                nativeAvailable = true,
                filesDir = "/data/user/0/com.kaizehuang.vernier.virtual",
                cacheDir = "/data/user/0/com.kaizehuang.vernier.virtual/cache",
                codePath = "/data/app/virtual/base.apk",
                findings = emptyList(),
            ),
            processInfo = VirtualizationProcessInfo(
                filesDir = "/data/user/0/com.kaizehuang.vernier/files",
                cacheDir = "/data/user/0/com.kaizehuang.vernier/cache",
                codePath = "/data/app/normal/base.apk",
            ),
        ).scanInternal()

        assertTrue(report.consistencyHitCount > 0)
        assertTrue(report.consistencyRows.any { it.label.contains("drift", ignoreCase = true) })
    }

    @Test
    fun `graphics only artifact difference does not trigger cross process artifact drift`() =
        runBlocking {
            val report = repository(
                nativeSnapshot = VirtualizationNativeSnapshot(
                    available = true,
                    findings = listOf(
                        VirtualizationNativeFinding(
                            "RUNTIME",
                            "WARNING",
                            "Graphics renderer",
                            "gfxstream",
                            "Google\ngfxstream\nOpenGL ES 3.2",
                        ),
                    ),
                ),
                remoteSnapshot = HelperProcessSnapshot(
                    available = true,
                    profile = HelperProcessProfile.REGULAR,
                    findings = emptyList(),
                ),
            ).scanInternal()

            assertTrue(report.consistencyRows.none { it.label == "Cross-process artifact drift" })
        }

    @Test
    fun `host classpath residue maps to danger`() = runBlocking {
        val report = repository(
            dexPathResult = DexPathProbeResult(
                classPathEntries = listOf(
                    "/data/app/com.vmos.pro/base.apk",
                    "/data/app/com.kaizehuang.vernier/base.apk",
                ),
                entryCount = 2,
                hitCount = 1,
                hostPathHit = true,
                signals = listOf(
                    signal(
                        id = "dex_host",
                        label = "Host dex path",
                        group = VirtualizationSignalGroup.RUNTIME,
                        severity = VirtualizationSignalSeverity.DANGER,
                    ),
                ),
            ),
        ).scanInternal()

        assertTrue(report.dangerSignals.any { it.label == "Host dex path" })
        assertEquals(1, report.dexPathHitCount)
    }

    @Test
    fun `egl renderer alone stays warning`() = runBlocking {
        val report = repository(
            nativeSnapshot = VirtualizationNativeSnapshot(
                available = true,
                eglAvailable = true,
                eglRenderer = "gfxstream",
                findings = listOf(
                    VirtualizationNativeFinding(
                        "RUNTIME",
                        "WARNING",
                        "Graphics renderer",
                        "gfxstream",
                        "Google\ngfxstream\nOpenGL ES 3.2",
                    ),
                ),
            ),
        ).scanInternal()

        assertTrue(report.dangerSignals.isEmpty())
        assertTrue(report.warningSignals.any { it.label == "Graphics renderer" })
        assertEquals(
            VirtualizationMethodOutcome.WARNING,
            report.methods.first { it.label == "Graphics renderer" }.outcome,
        )
    }

    @Test
    fun `main helper contaminated while isolated stays clean maps to danger`() = runBlocking {
        val report = repository(
            remoteSnapshot = HelperProcessSnapshot(
                available = true,
                profile = HelperProcessProfile.REGULAR,
                classPathEntries = listOf("/data/app/com.vmos.pro/base.apk"),
            ),
            isolatedSnapshot = HelperProcessSnapshot(
                available = true,
                profile = HelperProcessProfile.ISOLATED,
                classPathEntries = emptyList(),
                packagesForUid = emptyList(),
            ),
        ).scanInternal()

        assertTrue(
            report.consistencyRows.any {
                it.label == "Isolated process stayed clean" &&
                        it.severity == VirtualizationSignalSeverity.DANGER
            },
        )
    }

    @Test
    fun `mount anchor drift maps to danger`() = runBlocking {
        val report = repository(
            nativeSnapshot = VirtualizationNativeSnapshot(
                available = true,
                mountNamespaceInode = "mnt:[1]",
                apexMountKey = "10|8:1|/|/apex|ext4|/dev/block/dm-1",
            ),
            remoteSnapshot = HelperProcessSnapshot(
                available = true,
                profile = HelperProcessProfile.REGULAR,
                mountNamespaceInode = "mnt:[2]",
                apexMountKey = "11|8:1|/|/apex|authfs|microdroid",
            ),
        ).scanInternal()

        assertEquals(1, report.mountAnchorDriftCount)
        assertTrue(
            report.consistencyRows.any {
                it.label == "Cross-process mount anchor drift" &&
                        it.severity == VirtualizationSignalSeverity.DANGER
            },
        )
    }

    @Test
    fun `mount id only difference does not create anchor drift`() = runBlocking {
        val report = repository(
            nativeSnapshot = VirtualizationNativeSnapshot(
                available = true,
                mountNamespaceInode = "mnt:[1]",
                apexMountKey = "10|0:22|/|/apex|tmpfs|tmpfs",
                vendorMountKey = "20|254:29|/|/vendor|erofs|/dev/block/dm-29",
            ),
            remoteSnapshot = HelperProcessSnapshot(
                available = true,
                profile = HelperProcessProfile.REGULAR,
                mountNamespaceInode = "mnt:[2]",
                apexMountKey = "99|0:22|/|/apex|tmpfs|tmpfs",
                vendorMountKey = "88|254:29|/|/vendor|erofs|/dev/block/dm-29",
            ),
        ).scanInternal()

        assertEquals(0, report.mountAnchorDriftCount)
        assertTrue(report.consistencyRows.none { it.label == "Cross-process mount anchor drift" })
        assertTrue(report.consistencyRows.none { it.label == "Cross-process namespace drift" })
    }

    @Test
    fun `unavailable isolated identity snapshot does not create virtualization drift`() = runBlocking {
        val report = repository(
            nativeSnapshot = VirtualizationNativeSnapshot(
                available = true,
                mountNamespaceInode = "mnt:[1]",
                apexMountKey = "10|0:22|/|/apex|tmpfs|tmpfs",
                vendorMountKey = "20|254:29|/|/vendor|erofs|/dev/block/dm-29",
            ),
            isolatedSnapshot = HelperProcessSnapshot(
                available = false,
                profile = HelperProcessProfile.ISOLATED,
                errorDetail = "Identity probe failed.",
            ),
        ).scanInternal()

        assertFalse(report.isolatedProcessAvailable)
        assertEquals(0, report.mountAnchorDriftCount)
        assertTrue(report.consistencyRows.none { it.label == "Isolated mount anchor drift" })
        assertTrue(report.consistencyRows.none { it.label == "Isolated process stayed clean" })
    }

    @Test
    fun `namespace drift without comparable anchors stays warning`() = runBlocking {
        val report = repository(
            nativeSnapshot = VirtualizationNativeSnapshot(
                available = true,
                mountNamespaceInode = "mnt:[1]",
            ),
            remoteSnapshot = HelperProcessSnapshot(
                available = true,
                profile = HelperProcessProfile.REGULAR,
                mountNamespaceInode = "mnt:[2]",
            ),
        ).scanInternal()

        assertEquals(0, report.mountAnchorDriftCount)
        assertTrue(
            report.consistencyRows.any {
                it.label == "Cross-process namespace drift" &&
                        it.severity == VirtualizationSignalSeverity.WARNING
            },
        )
    }
}
