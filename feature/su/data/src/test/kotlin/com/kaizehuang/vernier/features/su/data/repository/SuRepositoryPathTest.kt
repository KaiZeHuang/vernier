package com.kaizehuang.vernier.features.su.data.repository

import com.kaizehuang.vernier.core.platform.PathState
import com.kaizehuang.vernier.features.su.data.native.SuNativeBridge
import com.kaizehuang.vernier.features.su.domain.SuMethodOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// The file scan also walks the host's PATH and runs `which su`, so only the daemon scan, which
// reads nothing but the injected path states, is asserted here.
class SuRepositoryPathTest {

    @Test
    fun `daemon paths this app cannot stat leave the daemon scan partial`() {
        val report = SuRepository(
            nativeBridge = SuNativeBridge(),
            pathState = { path -> if (path.startsWith("/data/adb")) PathState.NOT_OBSERVABLE else PathState.ABSENT },
        ).scanInternal()

        val daemonScan = report.methods.single { it.label == "daemonScan" }
        assertEquals("Partial", daemonScan.summary)
        assertEquals(SuMethodOutcome.SUPPORT, daemonScan.outcome)
        assertTrue(report.unobservablePathCount > 0)
        assertEquals(0, report.checkedDaemonPathCount)
    }

    @Test
    fun `absent daemon paths keep the daemon scan clean`() {
        val report = SuRepository(SuNativeBridge(), pathState = { PathState.ABSENT }).scanInternal()

        val daemonScan = report.methods.single { it.label == "daemonScan" }
        assertEquals(SuMethodOutcome.CLEAN, daemonScan.outcome)
        assertEquals(0, report.unobservablePathCount)
    }

    @Test
    fun `a daemon that can be stat-ed is detected`() {
        val report = SuRepository(
            nativeBridge = SuNativeBridge(),
            pathState = { path -> if (path == "/data/adb/ksud") PathState.PRESENT else PathState.ABSENT },
        ).scanInternal()

        assertEquals(SuMethodOutcome.DETECTED, report.methods.single { it.label == "daemonScan" }.outcome)
    }
}
