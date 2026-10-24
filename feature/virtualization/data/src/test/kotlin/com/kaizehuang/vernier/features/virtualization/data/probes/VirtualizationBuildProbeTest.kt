package com.kaizehuang.vernier.features.virtualization.data.probes

import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VirtualizationBuildProbeTest {

    @Test
    fun `single weak token does not raise build warning`() {
        val probe = VirtualizationBuildProbe()
        val signals = probe.evaluate(
            listOf(
                "Build.FINGERPRINT" to "vendor/device/test-keys",
                "Build.PRODUCT" to "realdevice",
            ),
        )

        assertTrue(signals.none { it.label == "Generic build tokens" })
    }

    @Test
    fun `weak token cluster still raises warning`() {
        val probe = VirtualizationBuildProbe()
        val signals = probe.evaluate(
            listOf(
                "Build.FINGERPRINT" to "generic/device/test-keys",
                "Build.PRODUCT" to "unknown",
            ),
        )

        assertEquals(1, signals.size)
        assertEquals("Generic build tokens", signals.first().label)
        assertEquals(VirtualizationSignalSeverity.WARNING, signals.first().severity)
    }
}
