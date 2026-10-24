package com.kaizehuang.vernier.features.su.data.native

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SuNativeBridgeTest {

    private val bridge = SuNativeBridge()

    @Test
    fun `parse reads the process context and counters`() {
        val snapshot = bridge.parse(
            """
            AVAILABLE=1
            SELF_CONTEXT=u:r:untrusted_app:s0:c512,c768
            SELF_ABNORMAL=0
            PROC_CHECKED=214
            PROC_DENIED=7
            """.trimIndent(),
        )

        assertTrue(snapshot.available)
        assertEquals("u:r:untrusted_app:s0:c512,c768", snapshot.selfContext)
        assertFalse(snapshot.selfContextAbnormal)
        assertEquals(214, snapshot.checkedProcesses)
        assertEquals(7, snapshot.deniedProcesses)
    }

    @Test
    fun `parse collects every suspicious process line`() {
        val snapshot = bridge.parse(
            """
            AVAILABLE=1
            PROC=1234 magiskd u:r:magisk:s0
            PROC=5678 ksud u:r:su:s0
            """.trimIndent(),
        )

        assertEquals(
            listOf("1234 magiskd u:r:magisk:s0", "5678 ksud u:r:su:s0"),
            snapshot.suspiciousProcesses,
        )
    }

    @Test
    fun `an explicit AVAILABLE of zero is not treated as available`() {
        val snapshot = bridge.parse("AVAILABLE=0")

        assertFalse(snapshot.available)
    }

    @Test
    fun `non numeric counters fall back to zero rather than throwing`() {
        val snapshot = bridge.parse(
            """
            AVAILABLE=1
            PROC_CHECKED=not-a-number
            """.trimIndent(),
        )

        assertEquals(0, snapshot.checkedProcesses)
    }

    @Test
    fun `blank payload reports an unavailable snapshot`() {
        val snapshot = bridge.parse("")

        assertFalse(snapshot.available)
        assertTrue(snapshot.suspiciousProcesses.isEmpty())
    }
}
