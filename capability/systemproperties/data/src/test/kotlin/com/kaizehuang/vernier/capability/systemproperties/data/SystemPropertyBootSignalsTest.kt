package com.kaizehuang.vernier.capability.systemproperties.data

import com.kaizehuang.vernier.capability.systemproperties.domain.MultiSourcePropertyRead
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertiesNativeSnapshot
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertyCategory
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySeverity
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SystemPropertyBootSignalsTest {

    private val utils = SystemPropertyConsistencyUtils()

    private fun read(property: String, value: String) = MultiSourcePropertyRead(
        property = property,
        category = SystemPropertyCategory.PROPERTY_CONSISTENCY,
        preferredValue = value,
        preferredSource = SystemPropertySource.REFLECTION,
        sourceValues = linkedMapOf(SystemPropertySource.REFLECTION to value),
    )

    @Test
    fun `a resetprop rewrite of the knox warranty bit contradicts cmdline`() {
        val signals = utils.buildRawBootSignals(
            readsByProperty = mapOf("ro.boot.warranty_bit" to read("ro.boot.warranty_bit", "0")),
            nativeSnapshot = SystemPropertiesNativeSnapshot(
                available = true,
                cmdlineBootParams = mapOf("androidboot.warranty_bit" to "1"),
            ),
        )

        val signal = signals.single()
        assertEquals(SystemPropertySeverity.DANGER, signal.severity)
        val detail = signal.detail.orEmpty()
        assertTrue(detail, detail.contains("ro.boot.warranty_bit"))
        assertTrue(detail, detail.contains("1"))
    }

    @Test
    fun `a resetprop rewrite of the realme boot state contradicts cmdline`() {
        val signals = utils.buildRawBootSignals(
            readsByProperty = mapOf(
                "ro.boot.realmebootstate" to read("ro.boot.realmebootstate", "green"),
            ),
            nativeSnapshot = SystemPropertiesNativeSnapshot(
                available = true,
                cmdlineBootParams = mapOf("androidboot.realmebootstate" to "orange"),
            ),
        )

        assertEquals(SystemPropertySeverity.DANGER, signals.single().severity)
    }

    @Test
    fun `an untouched boot parameter produces no signal`() {
        val signals = utils.buildRawBootSignals(
            readsByProperty = mapOf("ro.boot.warranty_bit" to read("ro.boot.warranty_bit", "1")),
            nativeSnapshot = SystemPropertiesNativeSnapshot(
                available = true,
                cmdlineBootParams = mapOf("androidboot.warranty_bit" to "1"),
            ),
        )

        assertTrue(signals.isEmpty())
    }

    @Test
    fun `bootconfig is consulted before cmdline`() {
        val signals = utils.buildRawBootSignals(
            readsByProperty = mapOf("ro.boot.warranty_bit" to read("ro.boot.warranty_bit", "0")),
            nativeSnapshot = SystemPropertiesNativeSnapshot(
                available = true,
                cmdlineBootParams = mapOf("androidboot.warranty_bit" to "0"),
                bootconfigBootParams = mapOf("androidboot.warranty_bit" to "1"),
            ),
        )

        val signal = signals.single()
        assertEquals(SystemPropertySource.BOOTCONFIG, signal.source)
    }

    @Test
    fun `a property the kernel never supplied is left alone`() {
        val signals = utils.buildRawBootSignals(
            readsByProperty = mapOf("ro.boot.warranty_bit" to read("ro.boot.warranty_bit", "0")),
            nativeSnapshot = SystemPropertiesNativeSnapshot(
                available = true,
                cmdlineBootParams = mapOf("androidboot.verifiedbootstate" to "green"),
            ),
        )

        assertTrue(signals.isEmpty())
    }
}
