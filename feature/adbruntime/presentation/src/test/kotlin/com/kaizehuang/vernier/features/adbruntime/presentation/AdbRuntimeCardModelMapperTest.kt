package com.kaizehuang.vernier.features.adbruntime.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.features.adbruntime.domain.AdbMdnsServiceKind
import com.kaizehuang.vernier.features.adbruntime.domain.AdbMdnsSnapshot
import com.kaizehuang.vernier.features.adbruntime.domain.AdbProbeState
import com.kaizehuang.vernier.features.adbruntime.domain.AdbPropertySnapshot
import com.kaizehuang.vernier.features.adbruntime.domain.AdbProtocolResponseKind
import com.kaizehuang.vernier.features.adbruntime.domain.AdbProtocolSnapshot
import com.kaizehuang.vernier.features.adbruntime.domain.AdbRuntimeReport
import com.kaizehuang.vernier.features.adbruntime.domain.AdbRuntimeSample
import com.kaizehuang.vernier.features.adbruntime.domain.AdbRuntimeStage
import com.kaizehuang.vernier.features.adbruntime.domain.UsbRuntimeSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdbRuntimeCardModelMapperTest {
    private val mapper = AdbRuntimeCardModelMapper()

    @Test
    fun unprobedScanReadsAsNotEvaluated() {
        val model = mapper.map(
            AdbRuntimeReport(
                stage = AdbRuntimeStage.READY,
                platformApiLevel = 36,
                samples = emptyList(),
                mdnsSamples = listOf(AdbMdnsSnapshot(AdbProbeState.UNAVAILABLE)),
                probed = false,
            ),
        )

        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), model.status)
        assertEquals("Not evaluated", model.verdict)
    }

    @Test
    fun protocolContradictionBecomesDangerRowAndExportBlock() {
        val sample = AdbRuntimeSample(
            usbDebuggingEnabled = true,
            debuggingFeaturesRestricted = false,
            properties = AdbPropertySnapshot(
                testHarnessMode = "0",
                initAdbdState = "stopped",
            ),
            usb = UsbRuntimeSnapshot(
                state = AdbProbeState.OBSERVED,
                configured = true,
                functions = setOf("adb"),
            ),
        )
        val mdns = AdbMdnsSnapshot(
            state = AdbProbeState.OBSERVED,
            serviceKind = AdbMdnsServiceKind.TLS_CONNECT,
            address = "192.0.2.5",
            port = 37123,
            protocol = AdbProtocolSnapshot(
                state = AdbProbeState.OBSERVED,
                responseKind = AdbProtocolResponseKind.START_TLS,
            ),
        )
        val model = mapper.map(
            AdbRuntimeReport(
                stage = AdbRuntimeStage.READY,
                platformApiLevel = 36,
                samples = listOf(sample, sample),
                mdnsSamples = listOf(mdns, mdns),
                probed = true,
            ),
        )
        val export = model.toDetectorReport()

        assertEquals(DetectorStatus.danger(), model.status)
        val finding = model.signalRows.single()
        assertEquals(DetectorStatus.danger(), finding.status)
        assertTrue(finding.detail.orEmpty().contains("192.0.2.5:37123"))
        assertTrue(model.scanRows.any { it.detail?.contains("protocol=ADB STLS request") == true })
        val rows = export.blocks
            .filterIsInstance<ReportBlock.Rows>()
            .single { it.title == "Inconsistencies" }
            .rows
        assertTrue(rows.isNotEmpty())
    }
}
