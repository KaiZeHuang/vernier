package com.kaizehuang.vernier.core.platform

import android.os.SystemProperties
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class HiddenSystemPropertiesTest {

    @After
    fun reset() {
        SystemProperties.values = emptyMap()
        SystemProperties.lastOverload = null
    }

    @Test
    fun `the single argument read calls get with the key only`() {
        SystemProperties.values = mapOf("ro.build.type" to "user")

        assertEquals("user", HiddenSystemProperties.read("ro.build.type").getOrNull())
        assertEquals("get(key)", SystemProperties.lastOverload)
    }

    @Test
    fun `the default read calls the overload that takes a default`() {
        assertEquals("fallback", HiddenSystemProperties.read("ro.missing", "fallback").getOrNull())
        assertEquals("get(key, def)", SystemProperties.lastOverload)
    }

    @Test
    fun `an unset key reads as empty through the single argument overload`() {
        assertEquals("", HiddenSystemProperties.read("ro.missing").getOrNull())
    }
}
