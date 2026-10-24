package com.kaizehuang.vernier.features.playintegrityfix.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.playintegrityfix.data.repository.PlayIntegrityFixRepository
import com.kaizehuang.vernier.features.playintegrityfix.domain.PlayIntegrityFixReport
import com.kaizehuang.vernier.features.playintegrityfix.presentation.PlayIntegrityFixCardModelMapper
import com.kaizehuang.vernier.features.playintegrityfix.presentation.model.PlayIntegrityFixCardModel
import com.kaizehuang.vernier.features.playintegrityfix.presentation.toDetectorReport

/**
 * Play Integrity Fix: looks for local traces of integrity-spoofing modules in system properties
 * and their consistency; it never returns a Google Play Integrity verdict.
 *
 * Collected by [PlayIntegrityFixRepository], judged by
 * `PlayIntegrityFixReport.toDetectorStatus()` in the domain layer, and described by
 * [PlayIntegrityFixCardModelMapper].
 */
@DetectorSpecificApi
public object PlayIntegrityFixDetector : Detector<PlayIntegrityFixReport, PlayIntegrityFixCardModel> {
    override val id: DetectorId = DetectorId("play_integrity_fix")

    override fun createScanner(context: Context): DetectorScanner<PlayIntegrityFixReport> = PlayIntegrityFixRepository()

    override fun loadingReport(): PlayIntegrityFixReport = PlayIntegrityFixReport.loading()

    override fun describe(report: PlayIntegrityFixReport): PlayIntegrityFixCardModel = PlayIntegrityFixCardModelMapper().map(report)

    override fun export(model: PlayIntegrityFixCardModel): DetectorReport = model.toDetectorReport()
}
