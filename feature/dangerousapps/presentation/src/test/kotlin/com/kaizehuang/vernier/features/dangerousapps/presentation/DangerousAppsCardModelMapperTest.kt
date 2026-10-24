package com.kaizehuang.vernier.features.dangerousapps.presentation

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousAppFinding
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousAppsCatalog
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousAppsReport
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousAppsStage
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousDetectionMethod
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousDetectionMethodKind
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousPackageVisibility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DangerousAppsCardModelMapperTest {

    private val mapper = DangerousAppsCardModelMapper()

    @Test
    fun `suspiciously low pm inventory becomes warning and mentions hma whitelist`() {
        val model = mapper.map(
            DangerousAppsReport(
                stage = DangerousAppsStage.READY,
                packageVisibility = DangerousPackageVisibility.FULL,
                packageManagerVisibleCount = 43,
                suspiciousLowPmInventory = true,
                suspiciousSharedStorageDenied = false,
                targets = DangerousAppsCatalog.targets,
                findings = emptyList(),
                hiddenFromPackageManager = emptyList(),
                probesRan = emptyList(),
                issues = listOf(
                    "PackageManager returned only 43 visible packages despite a full inventory result. This can happen under HMA-style whitelist filtering.",
                ),
            ),
        )

        assertEquals(DetectionSeverity.WARNING, model.status.severity)
        assertEquals(null, model.packageSectionSeverity)
        assertEquals("Package inventory unusually small", model.verdict)
        assertTrue(model.summary.contains("HMA-style whitelist filtering"))
        assertTrue(model.subtitle.contains("43 visible"))
        assertTrue(
            model.headerFacts.any { fact ->
                fact.label == "PM" &&
                        fact.status.severity == DetectionSeverity.WARNING &&
                        fact.value.contains("43")
            },
        )
    }

    @Test
    fun `hidden package copy uses direct corroboration language`() {
        val target = DangerousAppsCatalog.targets.first()
        val finding = DangerousAppFinding(
            target = target,
            methods = listOf(
                DangerousDetectionMethod(DangerousDetectionMethodKind.CREATE_PACKAGE_CONTEXT_ZIP),
            ),
        )
        val model = mapper.map(
            DangerousAppsReport(
                stage = DangerousAppsStage.READY,
                packageVisibility = DangerousPackageVisibility.FULL,
                packageManagerVisibleCount = 120,
                suspiciousLowPmInventory = false,
                suspiciousSharedStorageDenied = false,
                targets = DangerousAppsCatalog.targets,
                findings = listOf(finding),
                hiddenFromPackageManager = listOf(finding),
                probesRan = listOf(DangerousDetectionMethodKind.CREATE_PACKAGE_CONTEXT_ZIP),
            ),
        )

        assertEquals(DetectionSeverity.DANGER, model.status.severity)
        assertEquals(DetectionSeverity.DANGER, model.packageSectionSeverity)
        assertTrue(model.summary.contains("direct corroboration probes"))
        assertTrue(model.summary.contains("PackageManager inventory"))
        assertTrue(model.hmaAlert?.summary.orEmpty().contains("direct corroboration probes"))
        assertEquals(
            listOf("createPackageContext + ZipFile"),
            model.packageItems.single().methods,
        )
    }

    @Test
    fun `shared storage baseline denial becomes warning and mentions shared user gid`() {
        val model = mapper.map(
            DangerousAppsReport(
                stage = DangerousAppsStage.READY,
                packageVisibility = DangerousPackageVisibility.FULL,
                packageManagerVisibleCount = 120,
                suspiciousLowPmInventory = false,
                suspiciousSharedStorageDenied = true,
                targets = DangerousAppsCatalog.targets,
                findings = emptyList(),
                hiddenFromPackageManager = emptyList(),
                probesRan = emptyList(),
                issues = listOf(
                    "Shared external-storage baseline paths all returned EACCES/EPERM. This suggests shared user gid or related zygote storage groups may have been restricted.",
                ),
            ),
        )

        assertEquals(DetectionSeverity.WARNING, model.status.severity)
        assertEquals(null, model.packageSectionSeverity)
        assertEquals("Shared storage baseline denied", model.verdict)
        assertTrue(model.summary.contains("shared user gid"))
    }

    @Test
    fun `a detected package without hma gets medium section severity`() {
        val target = DangerousAppsCatalog.targets.first()
        val finding = DangerousAppFinding(
            target = target,
            methods = listOf(DangerousDetectionMethod(DangerousDetectionMethodKind.PACKAGE_MANAGER)),
        )
        val model = mapper.map(
            DangerousAppsReport(
                stage = DangerousAppsStage.READY,
                packageVisibility = DangerousPackageVisibility.FULL,
                packageManagerVisibleCount = 120,
                suspiciousLowPmInventory = false,
                suspiciousSharedStorageDenied = false,
                targets = DangerousAppsCatalog.targets,
                findings = listOf(finding),
                hiddenFromPackageManager = emptyList(),
                probesRan = listOf(DangerousDetectionMethodKind.PACKAGE_MANAGER),
            ),
        )
        assertEquals(DetectionSeverity.WARNING, model.packageSectionSeverity)
    }

    @Test
    fun `scene debugfs corroboration hidden from pm marks packages high`() {
        val scene = DangerousAppsCatalog.targets.first { it.packageName == "com.omarea.vtools" }
        val finding = DangerousAppFinding(
            target = scene,
            methods = listOf(
                DangerousDetectionMethod(
                    DangerousDetectionMethodKind.SCENE_DEBUGFS_CONTEXT,
                    detail = "Scene debugfs context: /dev/example",
                ),
            ),
        )
        val model = mapper.map(
            DangerousAppsReport(
                stage = DangerousAppsStage.READY,
                packageVisibility = DangerousPackageVisibility.FULL,
                packageManagerVisibleCount = 578,
                suspiciousLowPmInventory = false,
                suspiciousSharedStorageDenied = false,
                targets = DangerousAppsCatalog.targets,
                findings = listOf(finding),
                hiddenFromPackageManager = listOf(finding),
                probesRan = listOf(DangerousDetectionMethodKind.SCENE_DEBUGFS_CONTEXT),
            ),
        )

        assertEquals(DetectionSeverity.DANGER, model.packageSectionSeverity)
        assertEquals("Scene", model.packageItems.single().appName)
        assertEquals("Scene", model.hmaAlert?.hiddenPackages?.single()?.appName)
    }
}
