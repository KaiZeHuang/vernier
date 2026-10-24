package com.kaizehuang.vernier.features.rootmanagers.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.rootmanagers.data.repository.RootManagersRepository
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagersReport
import com.kaizehuang.vernier.features.rootmanagers.presentation.RootManagersCardModelMapper
import com.kaizehuang.vernier.features.rootmanagers.presentation.model.RootManagersCardModel
import com.kaizehuang.vernier.features.rootmanagers.presentation.toDetectorReport

/**
 * Root Managers: finds root manager apps through launcher visibility, including renamed, re-signed,
 * or other-profile installs.
 *
 * Collected by [RootManagersRepository], judged by `RootManagersReport.toDetectorStatus()` in the
 * domain layer, and described by [RootManagersCardModelMapper].
 */
@DetectorSpecificApi
public object RootManagersDetector : Detector<RootManagersReport, RootManagersCardModel> {
    override val id: DetectorId = DetectorId("root_managers")

    override fun createScanner(context: Context): DetectorScanner<RootManagersReport> = RootManagersRepository(context)

    override fun loadingReport(): RootManagersReport = RootManagersReport.loading()

    override fun describe(report: RootManagersReport): RootManagersCardModel = RootManagersCardModelMapper().map(report)

    override fun export(model: RootManagersCardModel): DetectorReport = model.toDetectorReport()
}
