package com.kaizehuang.vernier.core.ui.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.core.report.DetectorHeadline
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.scan.DetectorSummary
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardDetectorViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `shows the loading card until the first scan finishes`() = runTest(dispatcher) {
        val scanner = GatedScanner()
        val viewModel = CardDetectorViewModel(ProbeDetector, scanner)

        assertEquals(CardDetectorState(ready = false, cardModel = Card(0)), viewModel.state.value)
        assertEquals(summary(verdict = "report 0", ready = false), viewModel.summary.value)

        scanner.finish(7)

        assertEquals(CardDetectorState(ready = true, cardModel = Card(7)), viewModel.state.value)
        assertEquals(summary(verdict = "report 7", ready = true), viewModel.summary.value)
    }

    @Test
    fun `a rescan shows the loading card again and then publishes the new report`() = runTest(dispatcher) {
        val scanner = GatedScanner()
        val viewModel = CardDetectorViewModel(ProbeDetector, scanner)
        scanner.finish(1)

        viewModel.rescan()
        assertEquals(CardDetectorState(ready = false, cardModel = Card(0)), viewModel.state.value)

        scanner.finish(2)
        assertEquals(CardDetectorState(ready = true, cardModel = Card(2)), viewModel.state.value)
        assertEquals(2, scanner.scans)
    }

    private fun summary(verdict: String, ready: Boolean) = DetectorSummary(
        id = DetectorId("probe"),
        title = "Probe",
        status = DetectorStatus.info(InfoKind.SUPPORT),
        headline = verdict,
        summary = "summary",
        ready = ready,
    )

    private data class Card(val report: Int) : DetectorHeadline {
        override val title = "Probe"
        override val status = DetectorStatus.info(InfoKind.SUPPORT)
        override val verdict = "report $report"
        override val summary = "summary"
    }

    private object ProbeDetector : Detector<Int, Card> {
        override val id = DetectorId("probe")

        override fun createScanner(context: Context): DetectorScanner<Int> = error("tests pass the scanner")

        override fun loadingReport(): Int = 0

        override fun describe(report: Int): Card = Card(report)

        override fun export(model: Card): DetectorReport = error("not exported here")
    }

    /** A scanner whose scans finish only when the test says so. */
    private class GatedScanner : DetectorScanner<Int> {
        private var pending = CompletableDeferred<Int>()
        var scans = 0

        override suspend fun scan(): Int {
            scans++
            return pending.await().also { pending = CompletableDeferred() }
        }

        fun finish(report: Int) {
            pending.complete(report)
        }
    }
}
