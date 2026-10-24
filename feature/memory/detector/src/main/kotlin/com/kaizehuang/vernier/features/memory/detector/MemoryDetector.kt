package com.kaizehuang.vernier.features.memory.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.memory.data.repository.MemoryRepository
import com.kaizehuang.vernier.features.memory.domain.MemoryReport
import com.kaizehuang.vernier.features.memory.presentation.MemoryCardModelMapper
import com.kaizehuang.vernier.features.memory.presentation.model.MemoryCardModel
import com.kaizehuang.vernier.features.memory.presentation.toDetectorReport

/**
 * Memory: inspects this process natively for function hooks, memory map anomalies, file
 * descriptors, signal handlers, and vDSO and dynamic linker tampering.
 *
 * Collected by [MemoryRepository], judged by `MemoryReport.toDetectorStatus()` in the domain
 * layer, and described by [MemoryCardModelMapper].
 */
@DetectorSpecificApi
public object MemoryDetector : Detector<MemoryReport, MemoryCardModel> {
    override val id: DetectorId = DetectorId("memory")

    override fun createScanner(context: Context): DetectorScanner<MemoryReport> = MemoryRepository()

    override fun loadingReport(): MemoryReport = MemoryReport.loading()

    override fun describe(report: MemoryReport): MemoryCardModel = MemoryCardModelMapper().map(report)

    override fun export(model: MemoryCardModel): DetectorReport = model.toDetectorReport()
}
