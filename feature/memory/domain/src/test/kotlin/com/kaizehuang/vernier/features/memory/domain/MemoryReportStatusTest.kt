package com.kaizehuang.vernier.features.memory.domain

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import org.junit.Assert.assertEquals
import org.junit.Test

class MemoryReportStatusTest {

    private fun readyWith(severity: MemoryFindingSeverity): MemoryReport = MemoryReport.loading().copy(
        stage = MemoryStage.READY,
        findings = listOf(
            MemoryFinding(
                id = "memory_0",
                section = MemoryFindingSection.MAPS,
                category = "SMAPS",
                label = "Privately copied system code matches its file",
                detail = "/system/lib64/libc.so: 1 privately copied executable page(s) match the file byte for byte",
                severity = severity,
                detailMonospace = true,
            ),
        ),
    )

    @Test
    fun `a low finding alone leaves the memory status clear`() {
        assertEquals(
            DetectionSeverity.ALL_CLEAR,
            readyWith(MemoryFindingSeverity.LOW).toDetectorStatus().severity,
        )
    }

    @Test
    fun `a medium finding asks for review`() {
        assertEquals(
            DetectionSeverity.WARNING,
            readyWith(MemoryFindingSeverity.MEDIUM).toDetectorStatus().severity,
        )
    }
}
