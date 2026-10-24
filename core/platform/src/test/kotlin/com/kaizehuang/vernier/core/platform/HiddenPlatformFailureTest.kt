package com.kaizehuang.vernier.core.platform

import android.os.ParcelableException
import android.os.ServiceSpecificException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HiddenPlatformFailureTest {

    @Test
    fun `a service specific failure exposes its error code`() {
        val failure = ServiceSpecificException(7, "No key found")

        assertTrue(HiddenPlatformFailure.isServiceSpecific(failure))
        assertEquals(7, HiddenPlatformFailure.serviceSpecificErrorCode(failure))
    }

    @Test
    fun `a parcelable failure is recognised`() {
        assertTrue(HiddenPlatformFailure.isParcelable(ParcelableException(Throwable())))
        assertFalse(HiddenPlatformFailure.isServiceSpecific(ParcelableException(Throwable())))
    }

    @Test
    fun `other failures are neither and carry no error code`() {
        val failure = IllegalStateException("gone")

        assertFalse(HiddenPlatformFailure.isServiceSpecific(failure))
        assertFalse(HiddenPlatformFailure.isParcelable(failure))
        assertNull(HiddenPlatformFailure.serviceSpecificErrorCode(failure))
    }

    @Test
    fun `recognition needs the full class name`() {
        class ServiceSpecificException(@JvmField val errorCode: Int) : RuntimeException()

        assertFalse(HiddenPlatformFailure.isServiceSpecific(ServiceSpecificException(7)))
        assertNull(HiddenPlatformFailure.serviceSpecificErrorCode(ServiceSpecificException(7)))
    }
}
