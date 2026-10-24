package com.kaizehuang.vernier.features.virtualization.data.probes

import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity
import org.junit.Assert.assertTrue
import org.junit.Test

class UidIdentityProbeTest {

    private val probe = UidIdentityProbe()

    @Test
    fun `packages for uid containing host package maps to danger`() {
        val result = probe.evaluate(
            uid = 10123,
            applicationUid = 10123,
            packageName = "com.kaizehuang.vernier",
            processName = "com.kaizehuang.vernier",
            uidName = "u0_a123",
            packagesForUid = listOf("com.kaizehuang.vernier", "com.vmos.pro"),
        )

        assertTrue(
            result.signals.any {
                it.label == "Host package shares UID" &&
                        it.severity == VirtualizationSignalSeverity.DANGER
            },
        )
        assertTrue(result.hostPackageHit)
    }

    @Test
    fun `missing own package maps to danger`() {
        val result = probe.evaluate(
            uid = 10123,
            applicationUid = 10123,
            packageName = "com.kaizehuang.vernier",
            processName = "com.kaizehuang.vernier",
            uidName = "u0_a123",
            packagesForUid = listOf("com.example.other"),
        )

        assertTrue(
            result.signals.any {
                it.label == "Current package missing from UID" &&
                        it.severity == VirtualizationSignalSeverity.DANGER
            },
        )
    }

    @Test
    fun `blank uid name maps to warning`() {
        val result = probe.evaluate(
            uid = 10123,
            applicationUid = 10123,
            packageName = "com.kaizehuang.vernier",
            processName = "com.kaizehuang.vernier",
            uidName = "",
            packagesForUid = listOf("com.kaizehuang.vernier"),
        )

        assertTrue(
            result.signals.any {
                it.label == "UID name unavailable" &&
                        it.severity == VirtualizationSignalSeverity.WARNING
            },
        )
    }
}
