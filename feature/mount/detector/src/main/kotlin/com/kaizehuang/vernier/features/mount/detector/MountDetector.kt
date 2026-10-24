package com.kaizehuang.vernier.features.mount.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.mount.data.repository.MountRepository
import com.kaizehuang.vernier.features.mount.domain.MountReport
import com.kaizehuang.vernier.features.mount.presentation.MountCardModelMapper
import com.kaizehuang.vernier.features.mount.presentation.model.MountCardModel
import com.kaizehuang.vernier.features.mount.presentation.toDetectorReport

/**
 * Mount: looks for mount namespace anomalies through native mount table, filesystem and path
 * checks, concealed shell temporary files, an isolated process's mount view and a native
 * isolated service.
 *
 * Collected by [MountRepository], judged by `MountReport.toDetectorStatus()` in the domain
 * layer, and described by [MountCardModelMapper].
 */
@DetectorSpecificApi
public object MountDetector : Detector<MountReport, MountCardModel> {
    override val id: DetectorId = DetectorId("mount")

    override fun createScanner(context: Context): DetectorScanner<MountReport> = MountRepository(context)

    override fun loadingReport(): MountReport = MountReport.loading()

    override fun describe(report: MountReport): MountCardModel = MountCardModelMapper().map(report)

    override fun export(model: MountCardModel): DetectorReport = model.toDetectorReport()
}
