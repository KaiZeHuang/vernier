package com.kaizehuang.vernier.features.tee.data.native

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TeeRegisterTimerNativeBridgeTest {

    private val bridge = TeeRegisterTimerNativeBridge()

    @Test
    fun `parseSelection reads a successful register timer selection`() {
        val selection = bridge.parseSelection(
            """
            REGISTER_TIMER_AVAILABLE=1
            TIMER_SOURCE=cntvct_el0
            FALLBACK_REASON=
            AFFINITY=bound_cpu0
            """.trimIndent(),
        )

        assertTrue(selection.registerTimerAvailable)
        assertEquals("cntvct_el0", selection.timerSource)
        assertNull(selection.fallbackReason)
        assertEquals("bound_cpu0", selection.affinityStatus)
    }

    @Test
    fun `parseSelection keeps a non blank fallback reason`() {
        val selection = bridge.parseSelection(
            """
            REGISTER_TIMER_AVAILABLE=0
            TIMER_SOURCE=clock_monotonic
            FALLBACK_REASON=register read trapped by the kernel
            AFFINITY=not_requested
            """.trimIndent(),
        )

        assertFalse(selection.registerTimerAvailable)
        assertEquals("register read trapped by the kernel", selection.fallbackReason)
    }

    @Test
    fun `parseSelection restores escaped separators in the fallback reason`() {
        val selection = bridge.parseSelection(
            "FALLBACK_REASON=trapped\\nretried on\\tcpu0",
        )

        assertEquals("trapped\nretried on\tcpu0", selection.fallbackReason)
    }

    @Test
    fun `parseSelection falls back to documented defaults on an empty payload`() {
        val selection = bridge.parseSelection("")

        assertFalse(selection.registerTimerAvailable)
        assertEquals("clock_monotonic", selection.timerSource)
        assertEquals("not_requested", selection.affinityStatus)
        assertNull(selection.fallbackReason)
    }
}
