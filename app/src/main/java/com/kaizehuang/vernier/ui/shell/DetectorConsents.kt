package com.kaizehuang.vernier.ui.shell

import com.kaizehuang.vernier.core.detector.ConsentDecision
import com.kaizehuang.vernier.core.detector.ConsentId
import com.kaizehuang.vernier.core.detector.DetectorConsent
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.ui.detector.ConsentCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** A consent card with the detector whose scans its decision affects. */
internal class DetectorConsentCard(
    val detectorId: DetectorId,
    val card: ConsentCard,
) {
    val consent: DetectorConsent get() = card.consent
}

/**
 * Every consent's decision by consent id. It emits once every consent's decision has loaded, which
 * the startup gate waits for, and again whenever one of them changes.
 */
internal fun combineConsentDecisions(
    decisions: Map<ConsentId, Flow<ConsentDecision>>,
): Flow<Map<ConsentId, ConsentDecision>> {
    if (decisions.isEmpty()) return flowOf(emptyMap())
    val entries = decisions.map { (id, decision) -> decision.map { id to it } }
    return combine(entries) { loaded -> loaded.toMap() }
}
