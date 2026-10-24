package com.kaizehuang.vernier.features.nativeroot.presentation

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootFinding
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootFindingSeverity
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootGroup
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootReport
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootStage
import com.kaizehuang.vernier.features.nativeroot.domain.ROUND_COUNT
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuCloseSample
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuCloseStage
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingCollection
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingObservation
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingRound
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingVerdict
import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingWindow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SrcuTimingPresentationTest {
    @Test fun `disabled unsupported and failed experiments stay support rows`() {
        for (state in SrcuTimingCollection.entries.filter { it != SrcuTimingCollection.COLLECTED }) {
            val row = srcuTimingRow(SrcuTimingObservation(collection = state))
            assertEquals(DetectionSeverity.INFO, row.status.severity)
            assertEquals("fsnotifySrcuCloseTiming", row.label)
            assertNotNull(row.hiddenCopyText)
        }
    }
    @Test fun `mapper carries experiment failures into the SDK export`() {
        val report = NativeRootReport.loading().copy(stage = NativeRootStage.READY,
            srcuTiming = SrcuTimingObservation(SrcuTimingCollection.TIMED_OUT,
                failureDetail = "cleanup unconfirmed"))
        val model = NativeRootCardModelMapper().map(report)
        val row = model.methodRows.single { it.label == "fsnotifySrcuCloseTiming" }
        assertEquals(DetectionSeverity.INFO, model.status.severity)
        val exported = model.toDetectorReport().blocks.filterIsInstance<ReportBlock.Rows>()
            .flatMap { it.rows }.single { it.label == row.label }
        assertEquals(row.detail, exported.detail)
        assertTrue(exported.detail.orEmpty().contains("cleanup unconfirmed"))
    }
    @Test fun `failed errno appears in exportable diagnostics`() {
        val row = srcuTimingRow(SrcuTimingObservation(collection = SrcuTimingCollection.NATIVE_FAILED,
            failedWindow = SrcuTimingWindow(0, 0, false,
                listOf(SrcuCloseSample(0, 0, SrcuCloseStage.INIT_FAILED, 24)))))
        assertTrue(row.detail.orEmpty().contains("errno=24"))
    }

    private fun closes(delay: Long) = SrcuTimingWindow(1000, 100000, false,
        List(16) { SrcuCloseSample(1000L + it * 1000, 1000L + it * 1000 + delay, SrcuCloseStage.OK) })
    private val delayed = SrcuTimingObservation(SrcuTimingCollection.COLLECTED,
        List(ROUND_COUNT) { SrcuTimingRound(closes(100), closes(5000), closes(100)) }, cleanupCompleted = true)
    private val delayedReport = NativeRootReport.loading().copy(stage = NativeRootStage.READY, srcuTiming = delayed)

    @Test fun `the experiment names the card only when no finding warns`() {
        assertEquals(SrcuTimingVerdict.REPEATABLE_DELAY, delayed.analysis.verdict)
        assertEquals("Experimental fsnotify/SRCU timing anomaly", buildVerdict(delayedReport))
        assertTrue(buildSummary(delayedReport).startsWith("The optional experiment"))
        assertTrue(buildImpactItems(delayedReport).first().text.startsWith("The optional timing experiment"))
    }
    @Test fun `an established warning keeps the headline summary and impact`() {
        val report = delayedReport.copy(findings = listOf(NativeRootFinding("kernel_trace", "Kernel trace",
            "hit", "", NativeRootGroup.KERNEL, NativeRootFindingSeverity.WARNING)))
        assertEquals("2 native signal(s) need review", buildVerdict(report))
        assertTrue(buildSummary(report).startsWith("Only weaker"))
        assertTrue(buildImpactItems(report).first().text.startsWith("Isolated-process mount drift"))
    }
}
