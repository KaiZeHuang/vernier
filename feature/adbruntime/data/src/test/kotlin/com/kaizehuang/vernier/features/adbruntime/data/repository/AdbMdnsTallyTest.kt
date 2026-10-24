package com.kaizehuang.vernier.features.adbruntime.data.repository

import com.kaizehuang.vernier.features.adbruntime.domain.AdbMdnsServiceKind
import com.kaizehuang.vernier.features.adbruntime.domain.AdbProbeState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdbMdnsTallyTest {
    private val modern = listOf(AdbMdnsServiceKind.TLS_CONNECT, AdbMdnsServiceKind.LEGACY_TCP)

    @Test
    fun secureConnectServiceDecidesTheWindow() {
        val tally = AdbMdnsTally(modern)

        tally.localServiceResolved(AdbMdnsServiceKind.TLS_CONNECT, "adb-1", ADDRESS, TLS_PORT)

        assertTrue(tally.decided)
        assertEquals(AdbProbeState.OBSERVED, tally.snapshot().state)
        assertEquals(AdbMdnsServiceKind.TLS_CONNECT, tally.snapshot().serviceKind)
    }

    @Test
    fun legacyServiceWaitsForTheSecureConnectType() {
        val tally = AdbMdnsTally(modern)

        tally.localServiceResolved(AdbMdnsServiceKind.LEGACY_TCP, "adb-1", ADDRESS, LEGACY_PORT)

        assertFalse(tally.decided)
        assertEquals(AdbMdnsServiceKind.LEGACY_TCP, tally.snapshot().serviceKind)
    }

    @Test
    fun secureConnectWinsOverAnEarlierLegacyService() {
        val tally = AdbMdnsTally(modern)

        tally.localServiceResolved(AdbMdnsServiceKind.LEGACY_TCP, "adb-1", ADDRESS, LEGACY_PORT)
        tally.localServiceResolved(AdbMdnsServiceKind.TLS_CONNECT, "adb-1", ADDRESS, TLS_PORT)

        assertTrue(tally.decided)
        assertEquals(TLS_PORT, tally.snapshot().port)
    }

    @Test
    fun legacyServiceDecidesOnceSecureConnectCannotBeBrowsed() {
        val tally = AdbMdnsTally(modern)

        tally.discoveryFailed(AdbMdnsServiceKind.TLS_CONNECT, "start failed", permission = false)
        assertFalse(tally.decided)
        tally.localServiceResolved(AdbMdnsServiceKind.LEGACY_TCP, "adb-1", ADDRESS, LEGACY_PORT)

        assertTrue(tally.decided)
        assertEquals(AdbProbeState.OBSERVED, tally.snapshot().state)
    }

    @Test
    fun resolveFailureWithoutLocalServiceIsUnavailableNotAbsent() {
        val tally = AdbMdnsTally(modern)

        tally.resolveFailed(AdbMdnsServiceKind.TLS_CONNECT, "resolve failed", permission = false)

        assertFalse(tally.decided)
        assertEquals(AdbProbeState.UNAVAILABLE, tally.snapshot().state)
    }

    @Test
    fun everyTypeFailingToBrowseEndsTheWindow() {
        val tally = AdbMdnsTally(modern)

        tally.discoveryFailed(AdbMdnsServiceKind.TLS_CONNECT, "start failed", permission = false)
        tally.discoveryFailed(AdbMdnsServiceKind.LEGACY_TCP, "start failed", permission = false)

        assertTrue(tally.decided)
        assertEquals(AdbProbeState.UNAVAILABLE, tally.snapshot().state)
    }

    @Test
    fun permissionDenialEndsTheWindow() {
        val tally = AdbMdnsTally(modern)

        tally.resolveFailed(AdbMdnsServiceKind.LEGACY_TCP, "denied", permission = true)

        assertTrue(tally.decided)
        assertEquals(AdbProbeState.PERMISSION_REQUIRED, tally.snapshot().state)
    }

    @Test
    fun emptyWindowIsNotObserved() {
        val tally = AdbMdnsTally(modern)

        assertFalse(tally.decided)
        assertEquals(AdbProbeState.NOT_OBSERVED, tally.snapshot().state)
        assertNull(tally.snapshot().serviceKind)
    }

    @Test
    fun legacyOnlyPlatformDecidesOnTheLegacyService() {
        val tally = AdbMdnsTally(listOf(AdbMdnsServiceKind.LEGACY_TCP))

        tally.localServiceResolved(AdbMdnsServiceKind.LEGACY_TCP, "adb-1", ADDRESS, LEGACY_PORT)

        assertTrue(tally.decided)
        assertEquals(LEGACY_PORT, tally.snapshot().port)
    }

    private companion object {
        const val ADDRESS = "192.0.2.5"
        const val LEGACY_PORT = 5555
        const val TLS_PORT = 37123
    }
}
