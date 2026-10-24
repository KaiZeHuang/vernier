package com.kaizehuang.vernier.features.su.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.su.data.repository.SuRepository
import com.kaizehuang.vernier.features.su.domain.SuReport
import com.kaizehuang.vernier.features.su.presentation.SuCardModelMapper
import com.kaizehuang.vernier.features.su.presentation.model.SuCardModel
import com.kaizehuang.vernier.features.su.presentation.toDetectorReport

/**
 * SU: looks for su binaries and root daemons on known paths and in PATH, an abnormal SELinux
 * context of this process, and suspicious processes from a native process scan.
 *
 * Collected by [SuRepository], judged by `SuReport.toDetectorStatus()` in the domain layer, and
 * described by [SuCardModelMapper].
 */
@DetectorSpecificApi
public object SuDetector : Detector<SuReport, SuCardModel> {
    override val id: DetectorId = DetectorId("su")

    override fun createScanner(context: Context): DetectorScanner<SuReport> = SuRepository()

    override fun loadingReport(): SuReport = SuReport.loading()

    override fun describe(report: SuReport): SuCardModel = SuCardModelMapper().map(report)

    override fun export(model: SuCardModel): DetectorReport = model.toDetectorReport()
}
