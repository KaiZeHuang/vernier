package com.kaizehuang.vernier.features.nativeroot.data.probes

import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingCollection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SrcuTimingApplicabilityTest {
    @Test fun `public API presence does not make Android 15 persistence a packages list trigger`() {
        for (api in listOf(29, 35, 36, 37)) {
            assertEquals(SrcuTimingCollection.UNSUPPORTED_ANDROID, srcuTimingApplicability(api, "6.12.81-android16"))
        }
    }
    @Test fun `Android 11 is excluded because its readback cannot confirm the change`() {
        assertEquals(SrcuTimingCollection.UNSUPPORTED_ANDROID, srcuTimingApplicability(30, "5.10.240-android12"))
        assertNull(srcuTimingApplicability(31, "5.10.240-android12"))
    }
    @Test fun `only reviewed kernel families pass the structural gate`() {
        for (release in listOf("5.10.240-android12", "5.15.200", "6.1.140", "6.6.90", "6.12.81")) {
            assertNull(srcuTimingApplicability(34, release))
        }
        for (release in listOf("5.4.100", "6.8.0", "unknown", "999999999999.1")) {
            assertEquals(SrcuTimingCollection.UNSUPPORTED_KERNEL, srcuTimingApplicability(34, release))
        }
    }
}
