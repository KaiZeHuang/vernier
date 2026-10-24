package com.kaizehuang.vernier.features.systemproperties.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.systemproperties.data.repository.SystemPropertiesRepository
import com.kaizehuang.vernier.features.systemproperties.domain.SystemPropertiesReport
import com.kaizehuang.vernier.features.systemproperties.presentation.SystemPropertiesCardModelMapper
import com.kaizehuang.vernier.features.systemproperties.presentation.model.SystemPropertiesCardModel
import com.kaizehuang.vernier.features.systemproperties.presentation.toDetectorReport

/**
 * System Properties: reads security-relevant system properties from several sources and reports
 * values and inconsistencies that suggest they were modified.
 *
 * Collected by [SystemPropertiesRepository], judged by
 * `SystemPropertiesReport.toDetectorStatus()` in the domain layer, and described by
 * [SystemPropertiesCardModelMapper].
 */
@DetectorSpecificApi
public object SystemPropertiesDetector : Detector<SystemPropertiesReport, SystemPropertiesCardModel> {
    override val id: DetectorId = DetectorId("system_properties")

    override fun createScanner(context: Context): DetectorScanner<SystemPropertiesReport> = SystemPropertiesRepository()

    override fun loadingReport(): SystemPropertiesReport = SystemPropertiesReport.loading()

    override fun describe(report: SystemPropertiesReport): SystemPropertiesCardModel = SystemPropertiesCardModelMapper().map(report)

    override fun export(model: SystemPropertiesCardModel): DetectorReport = model.toDetectorReport()
}
