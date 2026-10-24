package com.kaizehuang.vernier.features.customrom.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.customrom.data.repository.CustomRomRepository
import com.kaizehuang.vernier.features.customrom.domain.CustomRomReport
import com.kaizehuang.vernier.features.customrom.presentation.CustomRomCardModelMapper
import com.kaizehuang.vernier.features.customrom.presentation.model.CustomRomCardModel
import com.kaizehuang.vernier.features.customrom.presentation.toDetectorReport

/**
 * Custom ROM: looks for custom ROM traces in installed packages, build and ROM system
 * properties, and native property-area and symbol probes.
 *
 * Collected by [CustomRomRepository], judged by `CustomRomReport.toDetectorStatus()` in the
 * domain layer, and described by [CustomRomCardModelMapper].
 */
@DetectorSpecificApi
public object CustomRomDetector : Detector<CustomRomReport, CustomRomCardModel> {
    override val id: DetectorId = DetectorId("custom_rom")

    override fun createScanner(context: Context): DetectorScanner<CustomRomReport> = CustomRomRepository(context)

    override fun loadingReport(): CustomRomReport = CustomRomReport.loading()

    override fun describe(report: CustomRomReport): CustomRomCardModel = CustomRomCardModelMapper().map(report)

    override fun export(model: CustomRomCardModel): DetectorReport = model.toDetectorReport()
}
