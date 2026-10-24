package com.kaizehuang.vernier.core.ui.components

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import org.junit.Assert.assertEquals
import org.junit.Test

class SectionSeverityTest {
    @Test
    fun `danger takes precedence even if warning comes first`() {
        assertEquals(
            SectionSeverity.HIGH,
            highestSectionSeverity(listOf(DetectorStatus.warning(), DetectorStatus.danger())),
        )
    }

    @Test
    fun `warning is shown if no danger exists`() {
        assertEquals(
            SectionSeverity.MEDIUM,
            highestSectionSeverity(listOf(DetectorStatus.allClear(), DetectorStatus.warning())),
        )
    }

    @Test
    fun `a failed probe is flagged rather than shown as a clean section`() {
        assertEquals(
            SectionSeverity.PROBE_ERROR,
            highestSectionSeverity(listOf(DetectorStatus.info(InfoKind.ERROR), DetectorStatus.allClear())),
        )
    }

    @Test
    fun `actionable evidence outranks a failed probe`() {
        assertEquals(
            SectionSeverity.MEDIUM,
            highestSectionSeverity(listOf(DetectorStatus.info(InfoKind.ERROR), DetectorStatus.warning())),
        )
        assertEquals(
            SectionSeverity.HIGH,
            highestSectionSeverity(listOf(DetectorStatus.info(InfoKind.ERROR), DetectorStatus.danger())),
        )
    }

    @Test
    fun `empty, clean and supporting sections have no label`() {
        assertEquals(null, highestSectionSeverity(emptyList()))
        assertEquals(
            null,
            highestSectionSeverity(listOf(DetectorStatus.info(InfoKind.SUPPORT), DetectorStatus.allClear())),
        )
    }

    @Test
    fun `header fact severities map only actionable evidence`() {
        assertEquals(SectionSeverity.HIGH, DetectionSeverity.DANGER.toSectionSeverity())
        assertEquals(SectionSeverity.MEDIUM, DetectionSeverity.WARNING.toSectionSeverity())
        assertEquals(null, DetectionSeverity.INFO.toSectionSeverity())
        assertEquals(null, DetectionSeverity.ALL_CLEAR.toSectionSeverity())
    }
}
