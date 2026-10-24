package com.kaizehuang.vernier.features.rootmanagers.domain

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import org.junit.Assert.assertEquals
import org.junit.Test

class RootManagersReportStatusTest {

    @Test
    fun `loading and failed scans are informational`() {
        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), RootManagersReport.loading().toDetectorStatus())
        assertEquals(DetectorStatus.info(InfoKind.ERROR), RootManagersReport.failed("failure").toDetectorStatus())
    }

    @Test
    fun `a scan that never enumerated the profiles is not clean`() {
        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), RootManagersReport.undecidable("no profiles").toDetectorStatus())
        assertEquals(DetectorStatus.info(InfoKind.ERROR), RootManagersReport.unavailable("denied").toDetectorStatus())
    }

    @Test
    fun `a complete evaluation without entries is clean`() {
        assertEquals(DetectorStatus.allClear(), evaluated().toDetectorStatus())
    }

    @Test
    fun `an unobserved or denied profile makes an empty result partial`() {
        val empty = evaluated(profileScans = listOf(searched(0), RootManagersProfileScan(10, ProfileScanState.EMPTY)))
        val denied = evaluated(profileScans = listOf(searched(0), RootManagersProfileScan(10, ProfileScanState.DENIED)))

        assertEquals(RootManagersOutcome.PARTIAL, empty.outcome())
        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), denied.toDetectorStatus())
    }

    @Test
    fun `filtered visibility, a missing self entry or unreadable certificates make an empty result partial`() {
        val filtered = evaluated(coverage = fullCoverage().copy(packageVisibility = InstalledPackageVisibility.RESTRICTED))
        val selfMissing = evaluated(coverage = fullCoverage().copy(callerSelfObserved = false))
        val noCertificates = evaluated(coverage = fullCoverage().copy(certificatesRead = 0, certificateReadFailures = 9))

        listOf(filtered, selfMissing, noCertificates).forEach { report ->
            assertEquals(RootManagersOutcome.PARTIAL, report.outcome())
        }
    }

    @Test
    fun `a caller without a launcher activity is not held to the self check`() {
        val report = evaluated(coverage = fullCoverage().copy(callerSelfObserved = null))

        assertEquals(DetectorStatus.allClear(), report.toDetectorStatus())
    }

    @Test
    fun `an identified manager is a warning`() {
        assertEquals(DetectorStatus.warning(), evaluated(listOf(entry(RootManagerConfidence.HIGH))).toDetectorStatus())
        assertEquals(DetectorStatus.warning(), evaluated(listOf(entry(RootManagerConfidence.MEDIUM))).toDetectorStatus())
    }

    @Test
    fun `an identified manager that hid its launcher icon is danger`() {
        val report = evaluated(listOf(entry(RootManagerConfidence.HIGH, hidesLauncherIcon = true)))

        assertEquals(RootManagersOutcome.CONCEALED_MANAGER, report.outcome())
        assertEquals(DetectorStatus.danger(), report.toDetectorStatus())
    }

    @Test
    fun `a weak-only hit stays informational, even with a hidden icon`() {
        val report = evaluated(listOf(entry(RootManagerConfidence.LOW, hidesLauncherIcon = true)))

        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), report.toDetectorStatus())
    }

    @Test
    fun `entries found by the PackageManager sweep survive an unavailable launcher enumeration`() {
        val report = RootManagersReport.unavailable("denied", entries = listOf(entry(RootManagerConfidence.HIGH)))

        assertEquals(DetectorStatus.warning(), report.toDetectorStatus())
    }
}
