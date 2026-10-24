package com.kaizehuang.vernier.features.nativeroot.data.probes

import com.kaizehuang.vernier.capability.helperprocess.data.HelperProcessProfile
import com.kaizehuang.vernier.capability.helperprocess.data.HelperProcessSnapshot
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootFindingSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MountNamespaceDriftProbeTest {

    private val probe = MountNamespaceDriftProbe()

    @Test
    fun `isolated mount anchor drift maps to warning finding`() {
        val result = probe.evaluate(
            localSnapshot = LocalMountNamespaceSnapshot(
                namespaceInode = "mnt:[41]",
                systemMountKey = "10|8:1|/|/system|ext4|/dev/block/dm-1",
            ),
            isolatedSnapshot = HelperProcessSnapshot(
                available = true,
                profile = HelperProcessProfile.ISOLATED,
                mountNamespaceInode = "mnt:[42]",
                systemMountKey = "11|0:22|/|/system|overlay|overlay",
            ),
        )

        assertTrue(result.available)
        assertTrue(result.isolatedProcessAvailable)
        assertEquals(1, result.mountAnchorDriftCount)
        assertEquals(1, result.signalCount)
        assertEquals("Isolated mount anchor drift", result.findings.single().label)
        assertEquals(NativeRootFindingSeverity.WARNING, result.findings.single().severity)
        assertTrue(result.findings.single().detail.contains("/system"))
    }

    @Test
    fun `namespace drift without comparable anchors still surfaces review finding`() {
        val result = probe.evaluate(
            localSnapshot = LocalMountNamespaceSnapshot(
                namespaceInode = "mnt:[100]",
            ),
            isolatedSnapshot = HelperProcessSnapshot(
                available = true,
                profile = HelperProcessProfile.ISOLATED,
                mountNamespaceInode = "mnt:[101]",
            ),
        )

        assertTrue(result.available)
        assertEquals(0, result.mountAnchorDriftCount)
        assertEquals(1, result.signalCount)
        assertEquals("Isolated mount namespace drift", result.findings.single().label)
        assertTrue(result.findings.single().detail.contains("mnt:[100]"))
        assertTrue(result.findings.single().detail.contains("mnt:[101]"))
    }
}
