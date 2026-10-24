package com.kaizehuang.vernier.features.nativeroot.data.probes

import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootFindingSeverity
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KernelSuThroneHuntProbeTest {

    private val readyRound = KernelSuThroneHuntRoundResult(
        available = true,
        stimulusApplied = true,
        packageDirectory = "/data/app/~~abc==/com.kaizehuang.vernier-def==",
        watchDescriptor = 12,
        directoryOpenCount = 1,
        directoryAccessCount = 1,
        detail = "watchInstalled=true",
    )

    @Test
    fun `directory traversal yields a danger finding`() {
        val result = KernelSuThroneHuntProbe().run(readyRound)

        assertTrue(result.available)
        assertEquals(2, result.hitCount)
        val finding = result.findings.single()
        assertEquals("KernelSU throne hunt", finding.label)
        assertEquals("open=1 access=1", finding.value)
        assertEquals(NativeRootGroup.PACKAGE, finding.group)
        assertEquals(NativeRootFindingSeverity.DANGER, finding.severity)
    }

    @Test
    fun `clean round emits no finding so runtime counting stays honest`() {
        val result = KernelSuThroneHuntProbe().run(
            readyRound.copy(directoryOpenCount = 0, directoryAccessCount = 0)
        )

        assertTrue(result.available)
        assertEquals(0, result.hitCount)
        assertTrue(result.findings.isEmpty())
    }

    @Test
    fun `unavailable round reports no coverage and no hit`() {
        val result = KernelSuThroneHuntProbe().run(
            KernelSuThroneHuntRoundResult(
                available = false,
                stimulusApplied = false,
                detail = "The app_zygote package directory watch was not installed.",
            )
        )

        assertFalse(result.available)
        assertFalse(result.watchInstalled)
        assertEquals(0, result.hitCount)
        assertTrue(result.findings.isEmpty())
    }

    @Test
    fun `unavailable round preserves diagnostic counters for later review`() {
        val result = KernelSuThroneHuntProbe().run(
            KernelSuThroneHuntRoundResult(
                available = false,
                failureStage = "STIMULUS_FAILED",
                stimulusApplied = false,
                packageDirectory = "/data/app/package",
                watchDescriptor = 12,
                directoryOpenCount = 0,
                directoryAccessCount = 0,
                rawEventCount = 5,
                invalidEventCount = 1,
                baselineHitCount = 2,
                stimulusDetail = "setMimeGroup failed: binder unavailable",
                detail = "round detail",
            )
        )

        assertEquals("STIMULUS_FAILED", result.failureStage)
        assertEquals(5, result.rawEventCount)
        assertEquals(1, result.invalidEventCount)
        assertEquals(2, result.baselineHitCount)
        assertEquals(12, result.watchDescriptor)
        assertTrue(result.stimulusDetail.contains("binder unavailable"))
    }

    @Test
    fun `watch denial is reported without a hit`() {
        val result = KernelSuThroneHuntProbe().run(
            KernelSuThroneHuntRoundResult(
                available = true,
                stimulusApplied = true,
                watchDenied = true,
                detail = "inotify_add_watch denied",
            )
        )

        assertTrue(result.available)
        assertTrue(result.watchDenied)
        assertTrue(result.findings.isEmpty())
    }
}
