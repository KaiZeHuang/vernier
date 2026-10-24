package com.kaizehuang.vernier.features.nativeroot.data.service

import com.kaizehuang.vernier.core.native.NativeCollectionOutcome
import com.kaizehuang.vernier.core.native.NativeLibraryHandle
import com.kaizehuang.vernier.core.native.NativeSnapshotCollector
import com.kaizehuang.vernier.features.nativeroot.data.native.ThroneHuntWatchNativeBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThroneHuntWatchInstallerTest {

    private val library = RecordingLibrary()
    private val bridge = ThroneHuntWatchNativeBridge(collector = NativeSnapshotCollector(library))

    @Test
    fun `before Android 12 no watch descriptor is opened in app_zygote`() {
        val state = ThroneHuntWatchInstaller.install(SOURCE_DIR, sdkInt = 30, bridge = bridge)

        assertFalse("the native watch must not be reached", library.queried)
        assertFalse(state.watchInstalled)
        assertEquals(-1, state.watchDescriptor)
        assertTrue(state.failureReason.orEmpty().contains("Android 12"))
        // An unsupported release is not a denied watch, which the card reports differently.
        assertFalse(state.watchDenied)
    }

    @Test
    fun `from Android 12 the watch is requested from the native bridge`() {
        val state = ThroneHuntWatchInstaller.install(SOURCE_DIR, sdkInt = 31, bridge = bridge)

        assertTrue(library.queried)
        assertFalse(state.watchInstalled)
        assertEquals(NativeCollectionOutcome.LIBRARY_UNAVAILABLE, state.collection.outcome)
    }

    private class RecordingLibrary : NativeLibraryHandle {
        var queried = false

        override val isLoaded: Boolean
            get() {
                queried = true
                return false
            }

        override val loadFailureDetail: String = "not loaded in unit tests"
    }

    private companion object {
        const val SOURCE_DIR = "/data/app/~~abc==/com.kaizehuang.vernier-def==/base.apk"
    }
}
