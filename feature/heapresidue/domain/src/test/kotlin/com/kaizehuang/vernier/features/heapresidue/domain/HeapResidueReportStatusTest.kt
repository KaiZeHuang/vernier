package com.kaizehuang.vernier.features.heapresidue.domain

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import org.junit.Assert.assertEquals
import org.junit.Test

class HeapResidueReportStatusTest {
    @Test fun everyNegativeOutcomeRemainsInformational() {
        HeapResidueOutcome.entries.forEach { outcome ->
            assertEquals(DetectionSeverity.INFO, HeapResidueReport(HeapResidueStage.READY, outcome).toDetectorStatus().severity)
        }
    }
    @Test fun onlyObservedEvidenceWarns() {
        val signal = HeapResidueSignal("com.target.app", setOf(HeapResidueArgument.PACKAGE_NAME))
        assertEquals(DetectionSeverity.WARNING, HeapResidueReport(HeapResidueStage.READY,
            HeapResidueOutcome.OBSERVED, listOf(signal)).toDetectorStatus().severity)
        assertEquals(DetectionSeverity.INFO, HeapResidueReport(HeapResidueStage.FAILED,
            HeapResidueOutcome.OBSERVED, listOf(signal)).toDetectorStatus().severity)
        assertEquals(DetectionSeverity.INFO, HeapResidueReport.loading().toDetectorStatus().severity)
    }
    @Test fun snapshotReductionRequiresCompletionAndDistinguishesNoCandidates() {
        val signal = HeapResidueSignal("com.target.app", setOf(HeapResidueArgument.PACKAGE_NAME))
        assertEquals(HeapResidueOutcome.UNAVAILABLE, heapResidueOutcome(false, 1, listOf(signal)))
        assertEquals(HeapResidueOutcome.OBSERVED, heapResidueOutcome(true, 1, listOf(signal)))
        assertEquals(HeapResidueOutcome.INCONCLUSIVE, heapResidueOutcome(true, 0, emptyList()))
        assertEquals(HeapResidueOutcome.NOT_OBSERVED, heapResidueOutcome(true, 1, emptyList()))
    }

}
