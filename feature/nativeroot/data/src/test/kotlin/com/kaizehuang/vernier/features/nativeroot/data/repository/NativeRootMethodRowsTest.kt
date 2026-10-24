package com.kaizehuang.vernier.features.nativeroot.data.repository

import com.kaizehuang.vernier.features.nativeroot.data.native.NativeRootNativeSnapshot
import com.kaizehuang.vernier.features.nativeroot.data.native.SusfsProbeOutcome
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootMethodOutcome
import org.junit.Assert.assertEquals
import org.junit.Test

class NativeRootMethodRowsTest {

    @Test
    fun `latency probe that never measured is unavailable even when the snapshot is available`() {
        val row = methodRow(
            NativeRootNativeSnapshot(
                available = true,
                kernelPatchSideChannelAvailable = false,
                kernelPatchSideChannelDetail = "Not run: KernelPatch's supercall is syscall 45 of the arm64 table.",
            ),
            LATENCY_ROW,
        )

        assertEquals("Unavailable", row.summary)
        assertEquals(NativeRootMethodOutcome.SUPPORT, row.outcome)
    }

    @Test
    fun `a measured run without the pre-fix delay is clean`() {
        val row = methodRow(NativeRootNativeSnapshot(available = true, kernelPatchSideChannelAvailable = true), LATENCY_ROW)

        assertEquals("No pre-fix delay", row.summary)
        assertEquals(NativeRootMethodOutcome.CLEAN, row.outcome)
    }

    @Test
    fun `susfs probe reads its outcome instead of the snapshot availability`() {
        fun row(outcome: SusfsProbeOutcome) =
            methodRow(NativeRootNativeSnapshot(available = true, susfsProbeOutcome = outcome), SUSFS_ROW)

        assertEquals(NativeRootMethodOutcome.SUPPORT, row(SusfsProbeOutcome.NOT_OBSERVED).outcome)
        assertEquals("Normal", row(SusfsProbeOutcome.DENIED).summary)
        assertEquals(NativeRootMethodOutcome.DETECTED, row(SusfsProbeOutcome.KILLED).outcome)
        assertEquals("UID changed", row(SusfsProbeOutcome.CHANGED_UID).summary)
        assertEquals(NativeRootMethodOutcome.DETECTED, row(SusfsProbeOutcome.CHANGED_UID).outcome)
    }

    private companion object {
        const val LATENCY_ROW = "__NR_supercall probe"
        const val SUSFS_ROW = "susfsSideChannel"
    }
}
