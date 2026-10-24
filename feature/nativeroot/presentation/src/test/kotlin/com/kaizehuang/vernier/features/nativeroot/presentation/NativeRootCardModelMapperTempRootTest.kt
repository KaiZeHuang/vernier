package com.kaizehuang.vernier.features.nativeroot.presentation

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootFinding
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootFindingSeverity
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootGroup
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootMethod
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootMethodOutcome
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootMethodResult
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootReport
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeRootCardModelMapperTempRootTest {

    private val mapper = NativeRootCardModelMapper()

    @Test
    fun `warning path hits stay warning in scan rows`() {
        val report = NativeRootReport(
            stage = NativeRootStage.READY,
            findings = listOf(
                NativeRootFinding(
                    id = "path_resetprop_tmp",
                    label = "resetprop tmp residue",
                    value = "Present",
                    detail = "Category: Shell tmp artifact\nConfirmations: 2/3\nEvidence: stat, openat\n/data/local/tmp/resetprop",
                    group = NativeRootGroup.PATH,
                    severity = NativeRootFindingSeverity.WARNING,
                    detailMonospace = true,
                ),
            ),
            rootDetected = false,
            kernelSuDetected = false,
            aPatchDetected = false,
            magiskDetected = false,
            susfsDetected = false,
            kernelSuVersion = 0L,
            nativeAvailable = true,
            prctlProbeHit = false,
            susfsProbeHit = false,
            pathHitCount = 1,
            pathCheckCount = 36,
            processHitCount = 0,
            processCheckedCount = 4,
            processDeniedCount = 0,
            cgroupAvailable = true,
            cgroupPathCheckCount = 32,
            cgroupAccessiblePathCount = 0,
            cgroupProcessCheckedCount = 0,
            cgroupProcDeniedCount = 0,
            cgroupHitCount = 0,
            kernelHitCount = 0,
            kernelSourceCount = 3,
            propertyHitCount = 0,
            propertyCheckCount = 5,
            methods = emptyList(),
        )

        val model = mapper.map(report)

        assertEquals(DetectionSeverity.WARNING, model.status.severity)
        assertEquals(DetectionSeverity.WARNING, model.scanRows.single { it.label == "Path hits" }.status.severity)
        assertTrue(model.runtimeRows.any { it.label == "resetprop tmp residue" })
    }

    @Test
    fun `temp root CVE exploit detection produces danger verdict`() {
        val report = NativeRootReport(
            stage = NativeRootStage.READY,
            findings = listOf(
                NativeRootFinding(
                    id = "temp_root_cve_0",
                    label = "Temp root CVE exploit artifact",
                    value = "libCVE43499root.so",
                    detail = "File \"libCVE43499root.so\" in /data/local/tmp matches CVE-2026-43499 temp root exploit pattern.",
                    group = NativeRootGroup.PATH,
                    severity = NativeRootFindingSeverity.DANGER,
                    detailMonospace = true,
                ),
                NativeRootFinding(
                    id = "temp_root_artifact_1",
                    label = "Temp root artifact",
                    value = "ksud-aarch64-linux-android",
                    detail = "File \"ksud-aarch64-linux-android\" in /data/local/tmp is a known temporary root infrastructure artifact.",
                    group = NativeRootGroup.PATH,
                    severity = NativeRootFindingSeverity.DANGER,
                    detailMonospace = true,
                ),
            ),
            rootDetected = false,
            kernelSuDetected = false,
            aPatchDetected = false,
            magiskDetected = false,
            susfsDetected = false,
            kernelSuVersion = 0L,
            nativeAvailable = true,
            prctlProbeHit = false,
            susfsProbeHit = false,
            pathHitCount = 2,
            pathCheckCount = 36,
            processHitCount = 0,
            processCheckedCount = 4,
            processDeniedCount = 0,
            cgroupAvailable = true,
            cgroupPathCheckCount = 32,
            cgroupAccessiblePathCount = 0,
            cgroupProcessCheckedCount = 0,
            cgroupProcDeniedCount = 0,
            cgroupHitCount = 0,
            kernelHitCount = 0,
            kernelSourceCount = 3,
            propertyHitCount = 0,
            propertyCheckCount = 5,
            methods = listOf(
                NativeRootMethodResult(
                    method = NativeRootMethod.TEMP_ROOT_ARTIFACTS,
                    summary = "CVE-2026-43499",
                    outcome = NativeRootMethodOutcome.DETECTED,
                    detail = "Scans /data/local/tmp for temp root exploit artifacts.",
                ),
            ),
            tempRootDetected = true,
            tempRootCveExploitDetected = true,
            tempRootArtifactHitCount = 2,
            tempRootArtifactCheckCount = 8,
        )

        val model = mapper.map(report)

        assertEquals(DetectionSeverity.DANGER, model.status.severity)
        assertTrue(model.verdict.contains("CVE-2026-43499"))
        assertTrue(model.runtimeRows.any { it.label == "Temp root CVE exploit artifact" })
        assertTrue(model.runtimeRows.any { it.label == "Temp root artifact" })
        assertEquals("2", model.scanRows.single { it.label == "Temp root hits" }.value)
        assertEquals(DetectionSeverity.DANGER, model.scanRows.single { it.label == "Temp root hits" }.status.severity)
        assertTrue(model.methodRows.any { it.label == "tempRootArtifacts" && it.value == "CVE-2026-43499" })
    }

    @Test
    fun `temp root detection without CVE produces danger verdict`() {
        val report = NativeRootReport(
            stage = NativeRootStage.READY,
            findings = listOf(
                NativeRootFinding(
                    id = "temp_root_artifact_0",
                    label = "Temp root artifact",
                    value = "ksu-helper",
                    detail = "File \"ksu-helper\" in /data/local/tmp is a known temporary root infrastructure artifact.",
                    group = NativeRootGroup.PATH,
                    severity = NativeRootFindingSeverity.DANGER,
                    detailMonospace = true,
                ),
            ),
            rootDetected = false,
            kernelSuDetected = false,
            aPatchDetected = false,
            magiskDetected = false,
            susfsDetected = false,
            kernelSuVersion = 0L,
            nativeAvailable = true,
            prctlProbeHit = false,
            susfsProbeHit = false,
            pathHitCount = 1,
            pathCheckCount = 36,
            processHitCount = 0,
            processCheckedCount = 4,
            processDeniedCount = 0,
            cgroupAvailable = true,
            cgroupPathCheckCount = 32,
            cgroupAccessiblePathCount = 0,
            cgroupProcessCheckedCount = 0,
            cgroupProcDeniedCount = 0,
            cgroupHitCount = 0,
            kernelHitCount = 0,
            kernelSourceCount = 3,
            propertyHitCount = 0,
            propertyCheckCount = 5,
            methods = listOf(
                NativeRootMethodResult(
                    method = NativeRootMethod.TEMP_ROOT_ARTIFACTS,
                    summary = "1 hit(s)",
                    outcome = NativeRootMethodOutcome.DETECTED,
                    detail = "Scans /data/local/tmp for temp root exploit artifacts.",
                ),
            ),
            tempRootDetected = true,
            tempRootCveExploitDetected = false,
            tempRootArtifactHitCount = 1,
            tempRootArtifactCheckCount = 8,
        )

        val model = mapper.map(report)

        assertEquals(DetectionSeverity.DANGER, model.status.severity)
        assertTrue(model.verdict.contains("Temp root"))
        assertTrue(model.runtimeRows.any { it.label == "Temp root artifact" })
    }
}
