package com.kaizehuang.vernier.features.kernelcheck.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.kernelcheck.data.repository.KernelCheckRepository
import com.kaizehuang.vernier.features.kernelcheck.domain.KernelCheckReport
import com.kaizehuang.vernier.features.kernelcheck.presentation.KernelCheckCardModelMapper
import com.kaizehuang.vernier.features.kernelcheck.presentation.model.KernelCheckCardModel
import com.kaizehuang.vernier.features.kernelcheck.presentation.toDetectorReport

/**
 * Kernel Check: checks the kernel identity and, on arm64, the CPU identity for inconsistencies
 * between the sources that report them.
 *
 * Collected by [KernelCheckRepository], judged by `KernelCheckReport.toDetectorStatus()` in the
 * domain layer, and described by [KernelCheckCardModelMapper].
 */
@DetectorSpecificApi
public object KernelCheckDetector : Detector<KernelCheckReport, KernelCheckCardModel> {
    override val id: DetectorId = DetectorId("kernel_check")

    override fun createScanner(context: Context): DetectorScanner<KernelCheckReport> = KernelCheckRepository()

    override fun loadingReport(): KernelCheckReport = KernelCheckReport.loading()

    override fun describe(report: KernelCheckReport): KernelCheckCardModel = KernelCheckCardModelMapper().map(report)

    override fun export(model: KernelCheckCardModel): DetectorReport = model.toDetectorReport()
}
