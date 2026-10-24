package com.kaizehuang.vernier.features.nativeroot.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThroneHuntWatchNativeBridgeTest {

    private val bridge = ThroneHuntWatchNativeBridge()

    @Test
    fun `watch payload parses install result`() {
        val snapshot = bridge.parseWatch(
            """
            WATCH_INSTALLED=1
            WATCH_DESCRIPTOR=7
            WATCH_ERRNO=0
            WATCH_PACKAGE_DIR=/data/app/~~abc==/com.kaizehuang.vernier-def==
            WATCH_DETAIL=Watching /data/app/~~abc==/com.kaizehuang.vernier-def== for throne hunt.
            """.trimIndent(),
        )

        assertTrue(snapshot.collection.isTrustworthy)
        assertTrue(snapshot.watchInstalled)
        assertEquals(7, snapshot.watchDescriptor)
        assertEquals("/data/app/~~abc==/com.kaizehuang.vernier-def==", snapshot.packageDirectory)
        assertTrue(snapshot.detail.contains("throne hunt"))
    }

    @Test
    fun `denied watch keeps errno`() {
        val snapshot = bridge.parseWatch(
            """
            WATCH_INSTALLED=0
            WATCH_DESCRIPTOR=-1
            WATCH_ERRNO=13
            WATCH_PACKAGE_DIR=
            WATCH_DETAIL=inotify_add_watch denied for /data/app: Permission denied
            """.trimIndent(),
        )

        assertFalse(snapshot.watchInstalled)
        assertEquals(13, snapshot.errorNumber)
        assertTrue(snapshot.detail.contains("Permission denied"))
    }

    @Test
    fun `blank watch payload is rejected instead of being read as clean`() {
        val snapshot = bridge.parseWatch("")

        assertFalse(snapshot.collection.isTrustworthy)
        assertEquals(NativeCollectionOutcome.PAYLOAD_REJECTED, snapshot.collection.outcome)
        assertFalse(snapshot.watchInstalled)
        assertEquals(-1, snapshot.watchDescriptor)
    }

    @Test
    fun `event payload sums directory level opens`() {
        val summary = bridge.parseEvents(
            """
            EVENT_DIRECTORY_OPEN=1
            EVENT_DIRECTORY_ACCESS=2
            EVENT_RAW=5
            EVENT_INVALID=0
            EVENT_DETAIL=directory IN_OPEN=1 IN_ACCESS=2 raw=5
            """.trimIndent(),
        )

        assertTrue(summary.collection.isTrustworthy)
        assertEquals(1, summary.directoryOpenCount)
        assertEquals(2, summary.directoryAccessCount)
        assertEquals(5, summary.rawEventCount)
        assertEquals(3, summary.hitCount)
    }

    @Test
    fun `clean event payload reports zero hits`() {
        val summary = bridge.parseEvents(
            """
            EVENT_DIRECTORY_OPEN=0
            EVENT_DIRECTORY_ACCESS=0
            EVENT_RAW=0
            EVENT_INVALID=0
            EVENT_DETAIL=clean
            """.trimIndent(),
        )

        assertEquals(0, summary.hitCount)
        assertEquals(0, summary.rawEventCount)
        assertTrue(summary.collection.isTrustworthy)
    }

    @Test
    fun `blank event payload is rejected without a clean result`() {
        val summary = bridge.parseEvents("")

        assertFalse(summary.collection.isTrustworthy)
        assertEquals(NativeCollectionOutcome.PAYLOAD_REJECTED, summary.collection.outcome)
        assertEquals(0, summary.hitCount)
    }
}
