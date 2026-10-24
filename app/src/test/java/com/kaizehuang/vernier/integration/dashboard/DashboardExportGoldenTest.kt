package com.kaizehuang.vernier.integration.dashboard

import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.features.dashboard.presentation.export.DashboardReportRenderer
import com.kaizehuang.vernier.features.dashboard.presentation.export.ExportHeader
import com.kaizehuang.vernier.sdk.DetectorCatalog
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Renders the export of the detectors recorded with the golden report and compares it byte for byte.
 *
 * A detector added to the catalog since the recording does not have to join it; running with
 * `DD_UPDATE_GOLDEN=1` records every catalogued detector again.
 */
class DashboardExportGoldenTest {

    @Test
    fun `typed report matches the golden export`() {
        val update = System.getenv("DD_UPDATE_GOLDEN") == "1"
        val detectors = if (update) DetectorCatalog.all else recordedDetectors()
        val rendered = GOLDEN_SEEDS.joinToString(separator = "\n# ---- next scenario ----\n") { seed ->
            DashboardReportRenderer.render(ExportScenario(seed, detectors, minListSize = seed % 2 * 2).export(FIXED_HEADER))
        }
        if (update) {
            write(GOLDEN_RESOURCE, rendered)
            write(GOLDEN_DETECTORS_RESOURCE, detectors.map { it.id.value }.sorted().joinToString(separator = "\n", postfix = "\n"))
            return
        }
        val golden = javaClass.getResource("/$GOLDEN_RESOURCE")?.readText()

        assertNotNull("missing golden export $GOLDEN_RESOURCE", golden)
        assertEquals(golden, rendered)
    }

    private fun recordedDetectors(): List<Detector<*, *>> {
        val recorded = javaClass.getResource("/$GOLDEN_DETECTORS_RESOURCE")?.readText()
        assertNotNull("missing recorded detectors $GOLDEN_DETECTORS_RESOURCE", recorded)
        val catalog = DetectorCatalog.all.associateBy { it.id.value }
        return recorded.orEmpty().lines().filter(String::isNotBlank).map { id ->
            checkNotNull(catalog[id]) { "the golden export records $id, which DetectorCatalog no longer has" }
        }
    }

    private fun write(resource: String, text: String) {
        File("src/test/resources/$resource").apply { parentFile.mkdirs() }.writeText(text)
    }

    private companion object {
        val GOLDEN_SEEDS = 0 until 4
        const val GOLDEN_RESOURCE = "dashboard-export/golden-report.txt"
        const val GOLDEN_DETECTORS_RESOURCE = "dashboard-export/golden-detectors.txt"
        val FIXED_HEADER = ExportHeader(
            versionName = "2026.01.02-0123456789ab",
            versionCode = 321,
            buildHash = "0123456789ab",
            buildTime = "2026-01-02 03:04:05",
            reportTime = "2026-01-02 03:04:05 (UTC)",
        )
    }
}
