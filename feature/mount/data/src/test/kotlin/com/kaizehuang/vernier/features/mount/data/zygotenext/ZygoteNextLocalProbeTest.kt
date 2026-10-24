package com.kaizehuang.vernier.features.mount.data.zygotenext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ZygoteNextLocalProbeTest {

    private val probe = ZygoteNextLocalProbe()

    @Test
    fun `mountinfo parser keeps propagation and structured root markers`() {
        val result = probe.parseMountInfo(
            lines = listOf(
                "24 1 0:1 / / rw master:1 - rootfs rootfs rw",
                "31 24 7:2 / /data/adb/modules/meta rw shared:9 - ext4 /dev/block/loop2 rw",
            ),
            namespaceInode = 1234,
        )

        assertTrue(result.available)
        assertEquals("master:1", result.rootPropagation)
        assertEquals(2, result.mountCount)
        assertEquals(1234, result.mountNamespaceInode)
        assertEquals(24L, result.rootMountId)
        assertEquals(24L, result.minimumMountId)
        assertEquals(31L, result.maximumMountId)
        assertEquals(listOf("data/adb"), result.markers.single().labels)
        assertTrue(result.markers.single().dangerous)
    }

    @Test
    fun `debug ramdisk alone is contextual rather than dangerous`() {
        val result = probe.parseMountInfo(
            lines = listOf(
                "24 1 0:1 / / rw shared:1 - rootfs rootfs rw",
                "25 24 0:2 / /debug_ramdisk rw - tmpfs tmpfs rw",
            ),
        )

        assertEquals("shared:1", result.rootPropagation)
        assertEquals(listOf(ZygoteNextMountMarker.DEBUG_RAMDISK_LABEL), result.markers.single().labels)
        assertFalse(result.markers.single().dangerous)
    }

    @Test
    fun `magisk adb module root is retained as dangerous evidence`() {
        val result = probe.parseMountInfo(
            lines = listOf(
                "24 1 0:1 / / rw shared:1 - rootfs rootfs rw",
                "31 24 7:2 /adb/modules/example/system /system rw - ext4 /dev/block/loop2 rw",
            ),
        )

        assertEquals(listOf("ADB modules"), result.markers.single().labels)
        assertTrue(result.markers.single().dangerous)
    }

    @Test
    fun `exact data adb path is retained as dangerous evidence`() {
        val result = probe.parseMountInfo(
            lines = listOf(
                "24 1 0:1 / / rw shared:1 - rootfs rootfs rw",
                "31 24 7:2 / /data/adb rw - ext4 /dev/block/loop2 rw",
            ),
        )

        assertEquals(listOf("data/adb"), result.markers.single().labels)
        assertTrue(result.markers.single().dangerous)
    }

    @Test
    fun `similar path prefixes are not treated as root evidence`() {
        val result = probe.parseMountInfo(
            lines = listOf(
                "24 1 0:1 / / rw shared:1 - rootfs rootfs rw",
                "31 24 7:2 / /data/adb_backup rw - ext4 /dev/block/loop2 rw",
                "32 24 7:3 /adb/modules_backup/system /system rw - ext4 /dev/block/loop3 rw",
            ),
        )

        assertTrue(result.markers.isEmpty())
    }

    @Test
    fun `malformed mount ids do not contribute to id range or entry count`() {
        val result = probe.parseMountInfo(
            lines = listOf(
                "24 1 0:1 / / rw master:1 - rootfs rootfs rw",
                "invalid 24 7:2 / /data rw - ext4 /dev/block/dm-1 rw",
            ),
        )

        assertEquals(1, result.mountCount)
        assertEquals(24L, result.rootMountId)
        assertEquals(24L, result.minimumMountId)
        assertEquals(24L, result.maximumMountId)
    }
}
