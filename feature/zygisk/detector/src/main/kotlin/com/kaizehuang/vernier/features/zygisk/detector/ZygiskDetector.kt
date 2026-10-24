package com.kaizehuang.vernier.features.zygisk.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.zygisk.data.repository.ZygiskRepository
import com.kaizehuang.vernier.features.zygisk.domain.ZygiskReport
import com.kaizehuang.vernier.features.zygisk.presentation.ZygiskCardModelMapper
import com.kaizehuang.vernier.features.zygisk.presentation.model.ZygiskCardModel
import com.kaizehuang.vernier.features.zygisk.presentation.toDetectorReport

/**
 * Zygisk: looks for Zygisk injection in this process through native linker, maps, file
 * descriptor, thread and heap probes, and a file descriptor trap in a separate
 * process.
 *
 * Collected by [ZygiskRepository], judged by `ZygiskReport.toDetectorStatus()` in the domain
 * layer, and described by [ZygiskCardModelMapper].
 */
@DetectorSpecificApi
public object ZygiskDetector : Detector<ZygiskReport, ZygiskCardModel> {
    override val id: DetectorId = DetectorId("zygisk")

    override fun createScanner(context: Context): DetectorScanner<ZygiskReport> = ZygiskRepository(context)

    override fun loadingReport(): ZygiskReport = ZygiskReport.loading()

    override fun describe(report: ZygiskReport): ZygiskCardModel = ZygiskCardModelMapper().map(report)

    override fun export(model: ZygiskCardModel): DetectorReport = model.toDetectorReport()
}
