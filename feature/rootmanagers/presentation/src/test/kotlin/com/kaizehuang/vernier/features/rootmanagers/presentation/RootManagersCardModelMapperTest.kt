package com.kaizehuang.vernier.features.rootmanagers.presentation

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.features.rootmanagers.domain.CertificateFingerprint
import com.kaizehuang.vernier.features.rootmanagers.domain.ObservationSource
import com.kaizehuang.vernier.features.rootmanagers.domain.ObservedApp
import com.kaizehuang.vernier.features.rootmanagers.domain.ProfileScanState
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagerAnchor
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagerConfidence
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagerEntry
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagerEntryRules
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagerFamily
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagersCoverage
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagersProfileScan
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagersReport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RootManagersCardModelMapperTest {

    private val mapper = RootManagersCardModelMapper()

    @Test
    fun `a scan that is still running reads as scanning`() {
        val model = mapper.map(RootManagersReport.loading())

        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), model.status)
        assertEquals("Scanning", model.verdict)
        assertEquals(0, model.entryRows.size)
    }

    @Test
    fun `an unavailable enumeration is informational, not clean`() {
        val model = mapper.map(RootManagersReport.unavailable("denied"))

        assertEquals(DetectorStatus.info(InfoKind.ERROR), model.status)
        assertEquals("Launcher enumeration unavailable", model.verdict)
    }

    @Test
    fun `a strong match is a warning with one row and one exported row`() {
        val model = mapper.map(report(listOf(entry(RootManagerConfidence.HIGH))))
        val export = model.toDetectorReport()

        assertEquals(DetectorStatus.warning(), model.status)
        assertEquals("Root manager apps found", model.verdict)
        assertEquals(model.verdict, export.verdict)
        assertEquals(model.status.severity, export.severity)
        val rows = export.blocks.filterIsInstance<ReportBlock.Rows>().single { it.title == "Matched root managers" }.rows
        assertEquals(1, rows.size)
    }

    @Test
    fun `a match by the manager key says so in its row`() {
        val key = CertificateFingerprint(827, "c371061b19d8c7d7d6133c6a9bafe198fa944e50c1b31c9d8daa8d7f1fc2d2d6")
        val model = mapper.map(report(listOf(entry(RootManagerConfidence.HIGH, matchedCertificate = key))))

        assertTrue(model.entryRows.single().detail.orEmpty().contains("manager key"))
        assertTrue(model.entryRows.single().hiddenCopyText.orEmpty().contains(key.sha256))
    }

    @Test
    fun `a manager that hid its icon is danger`() {
        val model = mapper.map(report(listOf(entry(RootManagerConfidence.HIGH, hidesLauncherIcon = true))))

        assertEquals(DetectorStatus.danger(), model.status)
        assertEquals("Root manager hiding its icon", model.verdict)
        assertEquals(DetectorStatus.danger(), model.entryRows.single().status)
    }

    @Test
    fun `a weak-only match stays informational`() {
        val model = mapper.map(report(listOf(entry(RootManagerConfidence.LOW))))

        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), model.status)
        assertEquals("Weak root manager match", model.verdict)
    }

    @Test
    fun `a complete evaluation without matches shows what it checked`() {
        val model = mapper.map(report(emptyList()))

        assertEquals(DetectorStatus.allClear(), model.status)
        assertEquals("No root manager apps", model.verdict)
        assertEquals(2, model.profileRows.size)
        assertEquals("2 of 2", model.scanRows.single { it.label == "Profiles searched" }.value)
        assertEquals("52", model.scanRows.single { it.label == "Apps checked" }.value)
        assertEquals("120 read · 0 failed", model.scanRows.single { it.label == "Signing certificates" }.value)
    }

    @Test
    fun `a partial evaluation is never clean`() {
        val model = mapper.map(report(emptyList(), coverage = coverage().copy(packageVisibility = InstalledPackageVisibility.RESTRICTED)))

        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), model.status)
        assertEquals("Partially evaluated", model.verdict)
        assertTrue(model.summary.contains("package visibility is not full"))
    }

    @Test
    fun `every row carries a hidden copy, and none leaks an unmatched app`() {
        val unmatched = (0 until 30).map { index ->
            ObservedApp(0, "com.private.app$index", ObservationSource.LAUNCHER_APPS, label = "Private $index")
        }
        val kernelSu = ObservedApp(0, "me.weishu.kernelsu", ObservationSource.LAUNCHER_APPS, label = "KernelSU")
        val entries = RootManagerEntryRules.entries(unmatched + kernelSu)
        val model = mapper.map(report(entries))
        val export = model.toDetectorReport()

        val rows = model.entryRows + model.profileRows + model.scanRows
        assertTrue(rows.all { row -> row.hiddenCopyText != null })
        val everything = rows.flatMap { listOf(it.label, it.value, it.detail, it.hiddenCopyText) }.joinToString("\n") +
            export.blocks.filterIsInstance<ReportBlock.Rows>().flatMap { it.rows }.joinToString("\n")
        assertTrue(everything.contains("me.weishu.kernelsu"))
        assertFalse(everything.contains("com.private.app"))
    }

    @Test
    fun `a denied profile row shows denied and makes the result partial`() {
        val model = mapper.map(
            report(
                entries = emptyList(),
                profileScans = listOf(searched(0, 40), RootManagersProfileScan(10, ProfileScanState.DENIED)),
            ),
        )

        val denied = model.profileRows.single { row -> row.label == "user 10" }
        assertEquals("Denied", denied.value)
        assertEquals(DetectorStatus.info(InfoKind.ERROR), denied.status)
        assertEquals("1 of 2", model.scanRows.single { it.label == "Profiles searched" }.value)
        assertEquals("Partially evaluated", model.verdict)
    }

    private fun report(
        entries: List<RootManagerEntry>,
        profileScans: List<RootManagersProfileScan> = listOf(searched(0, 40), searched(10, 12)),
        coverage: RootManagersCoverage = coverage(),
    ) = RootManagersReport.evaluated(entries = entries, profileScans = profileScans, coverage = coverage)

    private fun searched(profileUserId: Int, apps: Int) =
        RootManagersProfileScan(profileUserId, ProfileScanState.SEARCHED, launcherActivitiesSeen = apps, appsChecked = apps)

    private fun coverage() = RootManagersCoverage(
        callerSelfObserved = true,
        packageVisibility = InstalledPackageVisibility.FULL,
        certificatesRead = 120,
        payloadDirectoriesRead = 80,
    )

    private fun entry(
        confidence: RootManagerConfidence,
        hidesLauncherIcon: Boolean = false,
        matchedCertificate: CertificateFingerprint? = null,
    ) = RootManagerEntry(
        family = RootManagerFamily.KERNEL_SU,
        packageName = "me.weishu.kernelsu",
        displayName = "KernelSU",
        profileUserId = 0,
        componentClassName = "me.weishu.kernelsu.ui.MainActivity",
        applicationClassName = "me.weishu.kernelsu.KernelSUApplication",
        sourceDir = null,
        uid = null,
        firstInstallTime = null,
        anchors = setOf(RootManagerAnchor.PACKAGE_NAME, RootManagerAnchor.APPLICATION_CLASS),
        confidence = confidence,
        matchedCertificate = matchedCertificate,
        hidesLauncherIcon = hidesLauncherIcon,
    )
}
