package com.kaizehuang.vernier.sdk

import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.report.DetectorHeadline
import org.junit.Assert.assertEquals
import org.junit.Test

class DetectorCatalogTest {

    private val ids = DetectorCatalog.all.map { it.id.value }

    @Test
    fun `every detector appears once`() {
        assertEquals(ids.distinct(), ids)
    }

    @Test
    fun `every consent id appears once across the catalog`() {
        val consentIds = DetectorCatalog.all.flatMap { detector -> detector.consents.map { it.id } }
        assertEquals(consentIds.distinct(), consentIds)
    }

    @Test
    fun `bootloader and TEE start first and every other detector follows by id`() {
        assertEquals(listOf("bootloader", "tee"), ids.take(2))
        assertEquals(ids.drop(2).sorted(), ids.drop(2))
    }

    @Test
    fun `every detector describes and exports its loading report under its own title`() {
        DetectorCatalog.all.forEach { detector ->
            val (card, export) = loadingCardAndExport(detector)
            assertEquals(detector.id.value, card.title, export.title)
            assertEquals(detector.id.value, card.status.severity, export.severity)
            assertEquals(detector.id.value, card.verdict, export.verdict)
        }
    }

    private fun <R : Any, M : DetectorHeadline> loadingCardAndExport(detector: Detector<R, M>) =
        detector.describe(detector.loadingReport()).let { card -> card to detector.export(card) }
}
