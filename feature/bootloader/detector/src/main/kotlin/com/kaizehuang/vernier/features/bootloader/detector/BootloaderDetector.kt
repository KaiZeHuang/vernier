package com.kaizehuang.vernier.features.bootloader.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.bootloader.data.repository.BootloaderRepository
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderReport
import com.kaizehuang.vernier.features.bootloader.presentation.BootloaderCardModelMapper
import com.kaizehuang.vernier.features.bootloader.presentation.model.BootloaderCardModel
import com.kaizehuang.vernier.features.bootloader.presentation.toDetectorReport

/**
 * Bootloader: reads the root of trust from a KeyStore attestation and the boot-state system
 * properties, and reports what each source says about the bootloader and where they disagree.
 *
 * Collected by [BootloaderRepository], judged by `BootloaderReport.toDetectorStatus()` in the
 * domain layer, and described by [BootloaderCardModelMapper].
 */
@DetectorSpecificApi
public object BootloaderDetector : Detector<BootloaderReport, BootloaderCardModel> {
    override val id: DetectorId = DetectorId("bootloader")

    override fun createScanner(context: Context): DetectorScanner<BootloaderReport> = BootloaderRepository(context)

    override fun loadingReport(): BootloaderReport = BootloaderReport.loading()

    override fun describe(report: BootloaderReport): BootloaderCardModel = BootloaderCardModelMapper().map(report)

    override fun export(model: BootloaderCardModel): DetectorReport = model.toDetectorReport()
}
