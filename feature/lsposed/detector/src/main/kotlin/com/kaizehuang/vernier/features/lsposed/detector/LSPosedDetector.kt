package com.kaizehuang.vernier.features.lsposed.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.lsposed.data.repository.LSPosedRepository
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedReport
import com.kaizehuang.vernier.features.lsposed.presentation.LSPosedCardModelMapper
import com.kaizehuang.vernier.features.lsposed.presentation.model.LSPosedCardModel
import com.kaizehuang.vernier.features.lsposed.presentation.toDetectorReport

/**
 * LSPosed: looks for LSPosed and Xposed-style hooking through class, class loader, bridge
 * field, package, stack, hook callback, Binder, zygote permission, runtime artifact, logcat and
 * SELinux policy probes.
 *
 * Collected by [LSPosedRepository], judged by `LSPosedReport.toDetectorStatus()` in the domain
 * layer, and described by [LSPosedCardModelMapper].
 */
@DetectorSpecificApi
public object LSPosedDetector : Detector<LSPosedReport, LSPosedCardModel> {
    override val id: DetectorId = DetectorId("lsposed")

    override fun createScanner(context: Context): DetectorScanner<LSPosedReport> = LSPosedRepository(context)

    override fun loadingReport(): LSPosedReport = LSPosedReport.loading()

    override fun describe(report: LSPosedReport): LSPosedCardModel = LSPosedCardModelMapper().map(report)

    override fun export(model: LSPosedCardModel): DetectorReport = model.toDetectorReport()
}
