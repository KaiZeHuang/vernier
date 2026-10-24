package com.kaizehuang.vernier.features.adbruntime.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.adbruntime.data.repository.AdbRuntimeRepository
import com.kaizehuang.vernier.features.adbruntime.domain.AdbRuntimeReport
import com.kaizehuang.vernier.features.adbruntime.presentation.AdbRuntimeCardModelMapper
import com.kaizehuang.vernier.features.adbruntime.presentation.model.AdbRuntimeCardModel
import com.kaizehuang.vernier.features.adbruntime.presentation.toDetectorReport

/**
 * ADB Runtime: correlates Android debugging settings, USB and wireless ADB runtime signals, and init properties to detect contradictory ADB state.
 *
 * Collected by [AdbRuntimeRepository], judged by `AdbRuntimeReport.toDetectorStatus()` in the domain
 * layer, and described by [AdbRuntimeCardModelMapper].
 */
@DetectorSpecificApi
public object AdbRuntimeDetector : Detector<AdbRuntimeReport, AdbRuntimeCardModel> {
    override val id: DetectorId = DetectorId("adb_runtime")

    override fun createScanner(context: Context): DetectorScanner<AdbRuntimeReport> = AdbRuntimeRepository(context)

    override fun loadingReport(): AdbRuntimeReport = AdbRuntimeReport.loading()

    override fun describe(report: AdbRuntimeReport): AdbRuntimeCardModel = AdbRuntimeCardModelMapper().map(report)

    override fun export(model: AdbRuntimeCardModel): DetectorReport = model.toDetectorReport()
}
