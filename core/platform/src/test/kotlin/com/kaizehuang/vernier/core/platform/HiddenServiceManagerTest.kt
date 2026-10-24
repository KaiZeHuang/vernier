package com.kaizehuang.vernier.core.platform

import android.os.Binder
import android.os.ServiceManager
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class HiddenServiceManagerTest {

    @After
    fun reset() {
        ServiceManager.services = emptyMap()
        ServiceManager.listed = emptyArray()
    }

    @Test
    fun `getService returns the registered binder or null`() {
        val binder = Binder()
        ServiceManager.services = mapOf("activity" to binder)

        assertSame(binder, HiddenServiceManager.getService("activity").getOrNull())
        assertNull(HiddenServiceManager.getService("qemud").getOrNull())
    }

    @Test
    fun `listServices keeps only the service names`() {
        ServiceManager.listed = arrayOf("activity", null, 7, "qemud")

        assertEquals(listOf("activity", "qemud"), HiddenServiceManager.listServices().getOrNull())
    }
}
