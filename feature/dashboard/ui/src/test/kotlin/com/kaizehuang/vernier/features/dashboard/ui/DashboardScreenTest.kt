package com.kaizehuang.vernier.features.dashboard.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardScreenTest {

    @Test
    fun `export filename sanitizes model and uses deterministic timestamp`() {
        assertEquals(
            "vernier_detector_report_Pixel_7_20240102_030405.txt",
            generateExportReportFileName(" Pixel 7 ", 1704164645000L),
        )
    }

    @Test
    fun `blank model falls back to unknown`() {
        assertEquals(
            "vernier_detector_report_unknown_20240102_030405.txt",
            generateExportReportFileName("   ", 1704164645000L),
        )
    }

    private val groupFindings = listOf(
        GroupFinding(VernierGroup.BOOT_TRUST, hasFinding = true),
        GroupFinding(VernierGroup.RUNTIME_HOOKS, hasFinding = true),
        GroupFinding(VernierGroup.MOUNTS_FILES, hasFinding = false),
        GroupFinding(VernierGroup.POLICY_PROPERTIES, hasFinding = false),
        GroupFinding(VernierGroup.ENVIRONMENT, hasFinding = false),
    )

    @Test
    fun `a running scan keeps every group visible`() {
        val fold = foldCleanGroups(groupFindings, scanFinished = false, showClean = false)
        assertTrue(fold.hiddenGroups.isEmpty())
        assertEquals(3, fold.cleanGroupCount)
    }

    @Test
    fun `a finished scan folds only the clean groups`() {
        val fold = foldCleanGroups(groupFindings, scanFinished = true, showClean = false)
        assertEquals(
            setOf(
                VernierGroup.MOUNTS_FILES,
                VernierGroup.POLICY_PROPERTIES,
                VernierGroup.ENVIRONMENT,
            ),
            fold.hiddenGroups,
        )
        assertEquals(3, fold.cleanGroupCount)
    }

    @Test
    fun `expanding reveals clean groups but keeps the fold row`() {
        val fold = foldCleanGroups(groupFindings, scanFinished = true, showClean = true)
        assertTrue(fold.hiddenGroups.isEmpty())
        assertEquals(3, fold.cleanGroupCount)
    }

    @Test
    fun `a scan with findings everywhere has no fold row`() {
        val allFound = groupFindings.map { it.copy(hasFinding = true) }
        val fold = foldCleanGroups(allFound, scanFinished = true, showClean = false)
        assertTrue(fold.hiddenGroups.isEmpty())
        assertEquals(0, fold.cleanGroupCount)
    }
}
