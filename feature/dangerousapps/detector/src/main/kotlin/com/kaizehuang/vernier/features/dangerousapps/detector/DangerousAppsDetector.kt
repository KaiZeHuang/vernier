package com.kaizehuang.vernier.features.dangerousapps.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.dangerousapps.data.repository.DangerousAppsRepository
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousAppsCatalog
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousAppsReport
import com.kaizehuang.vernier.features.dangerousapps.presentation.DangerousAppsCardModelMapper
import com.kaizehuang.vernier.features.dangerousapps.presentation.model.DangerousAppsCardModel
import com.kaizehuang.vernier.features.dangerousapps.presentation.toDetectorReport

/**
 * Dangerous Apps: checks whether known root, hooking and tampering apps are installed, using
 * the package inventory and side channels that reveal packages the inventory cannot see.
 *
 * Collected by [DangerousAppsRepository], judged by `DangerousAppsReport.toDetectorStatus()` in
 * the domain layer, and described by [DangerousAppsCardModelMapper].
 */
@DetectorSpecificApi
public object DangerousAppsDetector : Detector<DangerousAppsReport, DangerousAppsCardModel> {
    override val id: DetectorId = DetectorId("dangerous_apps")

    override fun createScanner(context: Context): DetectorScanner<DangerousAppsReport> = DangerousAppsRepository(context)

    override fun loadingReport(): DangerousAppsReport = DangerousAppsReport.loading(DangerousAppsCatalog.targets)

    override fun describe(report: DangerousAppsReport): DangerousAppsCardModel = DangerousAppsCardModelMapper().map(report)

    override fun export(model: DangerousAppsCardModel): DetectorReport = model.toDetectorReport()
}
