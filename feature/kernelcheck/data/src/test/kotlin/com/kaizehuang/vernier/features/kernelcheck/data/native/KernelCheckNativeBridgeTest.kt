package com.kaizehuang.vernier.features.kernelcheck.data.native

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KernelCheckNativeBridgeTest {

    private val bridge = KernelCheckNativeBridge()

    @Test
    fun `parse reads kernel identity sources`() {
        val snapshot = bridge.parse(
            """
            AVAILABLE=1
            PROC_VERSION=Linux version 6.6.50-android15-8 (build@localhost)
            PROC_CMDLINE=androidboot.verifiedbootstate=green
            UTS_RELEASE=6.6.119-android15-8
            UTS_VERSION=#1 SMP PREEMPT Fri Aug 2 10:00:00 UTC 2024
            SYSCTL_OSRELEASE=6.6.50-android15-8
            SYSCTL_VERSION=#1 SMP PREEMPT Fri Aug 2 10:00:00 UTC 2024
            CMDLINE=0
            KPTR=0
            CPU_IDENTITY_STATUS=COMPLETED
            CPU_IDENTITY=0${'\t'}1${'\t'}SYSFS${'\t'}410fd050${'\t'}410fd050${'\t'}VERIFIED${'\t'}0
            FINDING=CMDLINE|GOOD|verifiedbootstate=green (verified)
            """.trimIndent(),
        )

        assertTrue(snapshot.available)
        assertEquals("6.6.119-android15-8", snapshot.utsRelease)
        assertEquals("#1 SMP PREEMPT Fri Aug 2 10:00:00 UTC 2024", snapshot.utsVersion)
        assertEquals("6.6.50-android15-8", snapshot.sysctlOsRelease)
        assertEquals("#1 SMP PREEMPT Fri Aug 2 10:00:00 UTC 2024", snapshot.sysctlVersion)
        assertFalse(snapshot.suspiciousCmdline)
        assertEquals(1, snapshot.findings.size)
        assertEquals(Arm64CpuIdentityProbeStatus.COMPLETED, snapshot.cpuIdentityStatus)
        assertEquals(1, snapshot.cpuIdentityObservations.size)
    }

    @Test
    fun `missing identity keys stay blank`() {
        val snapshot = bridge.parse(
            """
            AVAILABLE=1
            PROC_VERSION=Linux version 6.6.50-android15-8 (build@localhost)
            CMDLINE=1
            KPTR=1
            """.trimIndent(),
        )

        assertTrue(snapshot.suspiciousCmdline)
        assertTrue(snapshot.kptrExposed)
        assertEquals("", snapshot.utsRelease)
        assertEquals("", snapshot.utsVersion)
        assertEquals("", snapshot.sysctlOsRelease)
        assertEquals("", snapshot.sysctlVersion)
    }

    @Test
    fun `blank raw output returns empty snapshot`() {
        val snapshot = bridge.parse("")

        assertFalse(snapshot.available)
        assertEquals("", snapshot.procVersion)
        assertEquals("", snapshot.utsRelease)
    }
}
