package com.kaizehuang.vernier.features.tee.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorConsent
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.tee.data.repository.TeeRepository
import com.kaizehuang.vernier.features.tee.domain.TeeReport
import com.kaizehuang.vernier.features.tee.presentation.TeeCardModelMapper
import com.kaizehuang.vernier.features.tee.presentation.model.TeeCardModel
import com.kaizehuang.vernier.features.tee.presentation.toDetectorReport

/**
 * TEE: evaluates Android KeyStore and attestation evidence: certificate chains, security
 * levels, revocation status and selected KeyMint, StrongBox and Soter behaviour, together with
 * native timing and environment probes.
 *
 * Collected by [TeeRepository], judged by `TeeReport.toDetectorStatus()` in the domain layer,
 * and described by [TeeCardModelMapper].
 *
 * The card model is described collapsed; expansion only changes the card, never the export.
 */
@DetectorSpecificApi
public object TeeDetector : Detector<TeeReport, TeeCardModel> {
    override val id: DetectorId = DetectorId("tee")

    override val consents: List<DetectorConsent> = listOf(TeeRevocationNetworkConsent)

    override fun createScanner(context: Context): DetectorScanner<TeeReport> = TeeRepository(context)

    override fun loadingReport(): TeeReport = TeeReport.loading()

    override fun describe(report: TeeReport): TeeCardModel = TeeCardModelMapper().map(report, isExpanded = false)

    override fun export(model: TeeCardModel): DetectorReport = model.toDetectorReport()
}
