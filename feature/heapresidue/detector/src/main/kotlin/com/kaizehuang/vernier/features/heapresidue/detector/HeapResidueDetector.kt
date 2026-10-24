package com.kaizehuang.vernier.features.heapresidue.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.heapresidue.data.repository.HeapResidueRepository
import com.kaizehuang.vernier.features.heapresidue.domain.HeapResidueReport
import com.kaizehuang.vernier.features.heapresidue.presentation.HeapResidueCardModelMapper
import com.kaizehuang.vernier.features.heapresidue.presentation.model.HeapResidueCardModel
import com.kaizehuang.vernier.features.heapresidue.presentation.toDetectorReport

/**
 * Zygote heap residue: observes exact startup argument strings in a fresh isolated ART heap snapshot as heuristic package traces.
 *
 * Collected by [HeapResidueRepository], judged by `HeapResidueReport.toDetectorStatus()` in the domain
 * layer, and described by [HeapResidueCardModelMapper].
 */
@DetectorSpecificApi
public object HeapResidueDetector : Detector<HeapResidueReport, HeapResidueCardModel> {
    override val id: DetectorId = DetectorId("heap_residue")

    override fun createScanner(context: Context): DetectorScanner<HeapResidueReport> = HeapResidueRepository(context)

    override fun loadingReport(): HeapResidueReport = HeapResidueReport.loading()

    override fun describe(report: HeapResidueReport): HeapResidueCardModel = HeapResidueCardModelMapper().map(report)

    override fun export(model: HeapResidueCardModel): DetectorReport = model.toDetectorReport()
}
