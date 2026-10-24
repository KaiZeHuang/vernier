package com.kaizehuang.vernier.capability.packageinventory.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PackageDataDirectoryProbeTest {

    private val probe = PackageDataDirectoryProbe()

    @Test
    fun `no packages to check is an answered empty set`() {
        assertEquals(emptySet<String>(), probe.statPackages(emptyList()))
    }

    @Test
    fun `a native library that did not load gives no answer rather than no packages`() {
        // JVM unit tests never load libvernier, which is the unavailable case on a device.
        assertNull(probe.statPackages(listOf("com.example.target")))
    }
}
