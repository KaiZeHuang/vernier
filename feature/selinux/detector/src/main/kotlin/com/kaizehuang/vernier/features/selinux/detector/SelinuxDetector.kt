package com.kaizehuang.vernier.features.selinux.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.selinux.data.repository.SelinuxRepository
import com.kaizehuang.vernier.features.selinux.domain.SelinuxReport
import com.kaizehuang.vernier.features.selinux.presentation.SelinuxCardModelMapper
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxCardModel
import com.kaizehuang.vernier.features.selinux.presentation.toDetectorReport

/**
 * SELinux: checks the SELinux state visible to the app: audit behaviour, context validity
 * checked from an app zygote carrier, and signs of a modified policy.
 *
 * Collected by [SelinuxRepository], judged by `SelinuxReport.toDetectorStatus()` in the domain
 * layer, and described by [SelinuxCardModelMapper].
 */
@DetectorSpecificApi
public object SelinuxDetector : Detector<SelinuxReport, SelinuxCardModel> {
    override val id: DetectorId = DetectorId("selinux")

    override fun createScanner(context: Context): DetectorScanner<SelinuxReport> = SelinuxRepository(context)

    override fun loadingReport(): SelinuxReport = SelinuxReport.loading()

    override fun describe(report: SelinuxReport): SelinuxCardModel = SelinuxCardModelMapper().map(report)

    override fun export(model: SelinuxCardModel): DetectorReport = model.toDetectorReport()
}
