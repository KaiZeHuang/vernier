package com.kaizehuang.vernier.features.virtualization.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.virtualization.data.repository.VirtualizationRepository
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationReport
import com.kaizehuang.vernier.features.virtualization.presentation.VirtualizationCardModelMapper
import com.kaizehuang.vernier.features.virtualization.presentation.model.VirtualizationCardModel
import com.kaizehuang.vernier.features.virtualization.presentation.toDetectorReport

/**
 * Virtualization: looks for emulators, virtual machines and app virtualization through build
 * and emulator properties, services, dex paths, UID identity, host apps, helper and isolated
 * processes, and native timing and trap probes.
 *
 * Collected by [VirtualizationRepository], judged by `VirtualizationReport.toDetectorStatus()`
 * in the domain layer, and described by [VirtualizationCardModelMapper].
 */
@DetectorSpecificApi
public object VirtualizationDetector : Detector<VirtualizationReport, VirtualizationCardModel> {
    override val id: DetectorId = DetectorId("virtualization")

    override fun createScanner(context: Context): DetectorScanner<VirtualizationReport> = VirtualizationRepository(context)

    override fun loadingReport(): VirtualizationReport = VirtualizationReport.loading()

    override fun describe(report: VirtualizationReport): VirtualizationCardModel = VirtualizationCardModelMapper().map(report)

    override fun export(model: VirtualizationCardModel): DetectorReport = model.toDetectorReport()
}
