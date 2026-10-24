package com.kaizehuang.vernier.features.heapresidue.presentation

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.features.heapresidue.domain.*
import org.junit.Assert.*
import org.junit.Test

class HeapResidueCardModelMapperTest {
    @Test fun negativeSnapshotExplainsUnknownCoverage() {
        val model = HeapResidueCardModelMapper().map(HeapResidueReport(HeapResidueStage.READY, HeapResidueOutcome.NOT_OBSERVED))
        assertEquals(DetectionSeverity.INFO, model.status.severity)
        assertEquals("No target trace observed", model.verdict)
        assertTrue(model.summary.contains("history is incomplete"))
    }
    @Test fun findingDoesNotClaimInstallationOrCompromise() {
        val model = HeapResidueCardModelMapper().map(HeapResidueReport(HeapResidueStage.READY,
            HeapResidueOutcome.OBSERVED, listOf(HeapResidueSignal("com.target.app", setOf(HeapResidueArgument.NICE_NAME)))))
        assertEquals(DetectionSeverity.WARNING, model.status.severity)
        assertTrue(model.summary.contains("does not prove current installation"))
        assertEquals("Argument trace", model.signalRows.single().value)
    }
    @Test fun failureAndUnsupportedRemainDistinct() {
        val mapper = HeapResidueCardModelMapper()
        assertEquals("Probe unavailable", mapper.map(HeapResidueReport.failed("IOException")).verdict)
        val unsupported = mapper.map(HeapResidueReport(HeapResidueStage.READY, HeapResidueOutcome.UNSUPPORTED))
        assertEquals("Unsupported", unsupported.verdict)
        assertTrue(unsupported.summary.contains("Android 12 to 17"))
    }
    @Test fun releaseRowSeparatesAuditedFromNewerReleases() {
        fun row(release: HeapResidueRelease) = HeapResidueCardModelMapper()
            .map(HeapResidueReport(HeapResidueStage.READY, HeapResidueOutcome.NOT_OBSERVED, release = release))
            .scanRows.firstOrNull { it.label == "Android release" }
        assertEquals("Audited baseline", row(HeapResidueRelease.AUDITED)?.value)
        assertEquals("Newer than audited", row(HeapResidueRelease.NEWER)?.value)
        assertEquals("Pre-release build", row(HeapResidueRelease.PRERELEASE)?.value)
        assertNull(row(HeapResidueRelease.UNKNOWN))
    }
}
