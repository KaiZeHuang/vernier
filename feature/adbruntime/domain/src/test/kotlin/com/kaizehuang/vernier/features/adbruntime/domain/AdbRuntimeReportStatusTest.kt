package com.kaizehuang.vernier.features.adbruntime.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdbRuntimeReportStatusTest {
    @Test
    fun loadingAndFailedScansAreInformational() {
        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), AdbRuntimeReport.loading().toDetectorStatus())
        assertEquals(DetectorStatus.info(InfoKind.ERROR), AdbRuntimeReport.failed("failure").toDetectorStatus())
    }

    @Test
    fun unprobedScanIsNotClean() {
        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), ready(probed = false).toDetectorStatus())
    }

    @Test
    fun enabledAdbWithRunningInitIsWarning() {
        val sample = sample(usbEnabled = true, initState = "running")
        val report = ready(samples = listOf(sample, sample))

        assertTrue(report.findings().isEmpty())
        assertEquals(DetectorStatus.warning(), report.toDetectorStatus())
    }

    @Test
    fun settingsVersusStoppedInitStaysWarningWithoutProtocolEvidence() {
        val sample = sample(usbEnabled = true, initState = "stopped")
        val report = ready(samples = listOf(sample, sample))

        assertTrue(report.findings().isEmpty())
        assertEquals(DetectorStatus.warning(), report.toDetectorStatus())
    }

    @Test
    fun debuggingRestrictionMakesSettingsNonAuthoritative() {
        val sample = sample(
            usbEnabled = true,
            initState = "stopped",
            debuggingFeaturesRestricted = true,
        )
        val report = ready(samples = listOf(sample, sample))

        assertTrue(report.findings().isEmpty())
        assertEquals(DetectorStatus.allClear(), report.toDetectorStatus())
    }

    @Test
    fun android13TestHarnessSuppressesUsbSettingOnly() {
        val sample = sample(
            usbEnabled = true,
            initState = "stopped",
            testHarnessMode = "1",
        )
        val report = ready(apiLevel = 33, samples = listOf(sample, sample))

        assertEquals(DetectorStatus.allClear(), report.toDetectorStatus())
    }

    @Test
    fun testHarnessDoesNotSuppressWirelessSetting() {
        val sample = sample(
            usbEnabled = true,
            wifiEnabled = true,
            initState = "stopped",
            testHarnessMode = "1",
        )
        val report = ready(apiLevel = 33, samples = listOf(sample, sample))

        assertEquals(DetectorStatus.warning(), report.toDetectorStatus())
    }

    @Test
    fun usbRuntimeAdbFunctionIsActivityOnly() {
        val sample = sample(
            usbEnabled = false,
            initState = "stopped",
            usbConfigured = true,
            usbFunctions = setOf("mtp", "adb"),
        )
        val report = ready(samples = listOf(sample, sample))

        assertTrue(report.findings().isEmpty())
        assertEquals(DetectorStatus.warning(), report.toDetectorStatus())
    }

    @Test
    fun sysUsbStateAdbIsActivityOnly() {
        val sample = sample(
            usbEnabled = false,
            initState = "stopped",
            sysUsbState = "mtp,adb",
        )
        val report = ready(samples = listOf(sample, sample))

        assertTrue(report.findings().isEmpty())
        assertEquals(DetectorStatus.warning(), report.toDetectorStatus())
    }

    @Test
    fun disabledAdbWithStoppedInitIsClear() {
        val sample = sample(
            usbEnabled = false,
            wifiEnabled = false,
            initState = "stopped",
        )
        val report = ready(samples = listOf(sample, sample))

        assertTrue(report.findings().isEmpty())
        assertEquals(DetectorStatus.allClear(), report.toDetectorStatus())
    }

    private fun sample(
        usbEnabled: Boolean? = false,
        wifiEnabled: Boolean? = false,
        debuggingFeaturesRestricted: Boolean? = false,
        testHarnessMode: String? = "0",
        initState: String? = "running",
        sysUsbState: String? = null,
        usbConfigured: Boolean? = false,
        usbFunctions: Set<String> = emptySet(),
    ) = AdbRuntimeSample(
        usbDebuggingEnabled = usbEnabled,
        wirelessDebuggingEnabled = wifiEnabled,
        debuggingFeaturesRestricted = debuggingFeaturesRestricted,
        properties = AdbPropertySnapshot(
            testHarnessMode = testHarnessMode,
            initAdbdState = initState,
            sysUsbState = sysUsbState,
        ),
        usb = UsbRuntimeSnapshot(
            state = AdbProbeState.OBSERVED,
            configured = usbConfigured,
            functions = usbFunctions,
        ),
    )

    private fun ready(
        apiLevel: Int = 36,
        probed: Boolean = true,
        samples: List<AdbRuntimeSample> = emptyList(),
        mdnsSamples: List<AdbMdnsSnapshot> = emptyList(),
    ) = AdbRuntimeReport(
        stage = AdbRuntimeStage.READY,
        platformApiLevel = apiLevel,
        samples = samples,
        mdnsSamples = mdnsSamples,
        probed = probed,
    )
}
