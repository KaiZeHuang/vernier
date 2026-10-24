package com.kaizehuang.vernier.core.evidence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class DetectorStatusTest {

    @Test
    fun `factories produce the matching severity`() {
        assertEquals(DetectionSeverity.ALL_CLEAR, DetectorStatus.allClear().severity)
        assertEquals(DetectionSeverity.WARNING, DetectorStatus.warning().severity)
        assertEquals(DetectionSeverity.DANGER, DetectorStatus.danger().severity)
        assertNull(DetectorStatus.danger().infoKind)
        assertEquals(DetectorStatus(DetectionSeverity.INFO, InfoKind.ERROR), DetectorStatus.info(InfoKind.ERROR))
    }

    @Test
    fun `only info status may carry an info kind`() {
        DetectionSeverity.entries.filter { it != DetectionSeverity.INFO }.forEach { severity ->
            assertThrows(IllegalArgumentException::class.java) {
                DetectorStatus(severity, InfoKind.SUPPORT)
            }
        }
    }
}
