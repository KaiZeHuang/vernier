package com.kaizehuang.vernier.features.virtualization.data.probes

import android.content.Context
import com.kaizehuang.vernier.capability.packageinventory.data.AndroidInstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.data.PackageDataDirectoryProbe
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.features.virtualization.data.rules.VirtualizationHostAppTarget
import com.kaizehuang.vernier.features.virtualization.data.rules.VirtualizationHostAppsCatalog
import java.io.File

enum class VirtualizationHostDetectionMethodKind(
    val label: String,
) {
    PACKAGE_MANAGER("PackageManager"),
    FUSE_STAT("FUSE stat"),
    NATIVE_DATA_STAT("Native /data/data stat"),
    SPECIAL_PATH("Special path"),
}

data class VirtualizationHostDetectionMethod(
    val kind: VirtualizationHostDetectionMethodKind,
    val detail: String? = null,
)

data class VirtualizationHostAppFinding(
    val target: VirtualizationHostAppTarget,
    val methods: List<VirtualizationHostDetectionMethod>,
)

data class VirtualizationHostAppProbeResult(
    val packageVisibility: InstalledPackageVisibility,
    val findings: List<VirtualizationHostAppFinding>,
    val issues: List<String> = emptyList(),
)

open class VirtualizationHostAppProbe(
    private val context: Context? = null,
    private val nativeBridge: PackageDataDirectoryProbe = PackageDataDirectoryProbe(),
    private val packageInventoryReader: InstalledPackageInventoryReader? = null,
) {

    open fun probe(): VirtualizationHostAppProbeResult {
        val appContext = context?.applicationContext ?: return VirtualizationHostAppProbeResult(
            packageVisibility = InstalledPackageVisibility.UNKNOWN,
            findings = emptyList(),
            issues = listOf("Context unavailable."),
        )

        val detected = linkedMapOf<String, MutableSet<VirtualizationHostDetectionMethod>>()
        val inventoryResult = (packageInventoryReader
            ?: AndroidInstalledPackageInventoryReader(appContext)).read()
        val inventory = (inventoryResult as? InstalledPackageInventoryResult.Available)?.inventory
        val installedPackages = inventory?.packageNames.orEmpty()
        val packageVisibility = inventory?.visibility ?: InstalledPackageVisibility.UNKNOWN

        if (packageVisibility == InstalledPackageVisibility.FULL) {
            VirtualizationHostAppsCatalog.targets.forEach { target ->
                if (target.packageName in installedPackages) {
                    detected
                        .getOrPut(target.packageName) { linkedSetOf() }
                        .add(VirtualizationHostDetectionMethod(VirtualizationHostDetectionMethodKind.PACKAGE_MANAGER))
                }
            }
        }

        VirtualizationHostAppsCatalog.targets.forEach { target ->
            if (checkFusePath(target.packageName)) {
                detected
                    .getOrPut(target.packageName) { linkedSetOf() }
                    .add(VirtualizationHostDetectionMethod(VirtualizationHostDetectionMethodKind.FUSE_STAT))
            }
        }

        val nativeDataDirectories =
            nativeBridge.statPackages(VirtualizationHostAppsCatalog.targets.map { it.packageName })
        nativeDataDirectories?.forEach { packageName ->
            detected
                .getOrPut(packageName) { linkedSetOf() }
                .add(VirtualizationHostDetectionMethod(VirtualizationHostDetectionMethodKind.NATIVE_DATA_STAT))
        }

        VirtualizationHostAppsCatalog.specialPaths.forEach { (path, packageName) ->
            if (runCatching { File(path).exists() }.getOrDefault(false)) {
                detected
                    .getOrPut(packageName) { linkedSetOf() }
                    .add(
                        VirtualizationHostDetectionMethod(
                            kind = VirtualizationHostDetectionMethodKind.SPECIAL_PATH,
                            detail = path,
                        ),
                    )
            }
        }

        val findings = VirtualizationHostAppsCatalog.targets.mapNotNull { target ->
            detected[target.packageName]?.let { methods ->
                VirtualizationHostAppFinding(
                    target = target,
                    methods = methods.sortedBy { it.kind.ordinal },
                )
            }
        }

        val issues = buildList {
            if (packageVisibility == InstalledPackageVisibility.RESTRICTED) {
                add("PackageManager visibility is restricted on this device profile.")
            }
            if (inventoryResult is InstalledPackageInventoryResult.Unavailable) {
                add("PackageManager inventory unavailable: ${inventoryResult.failure.detail}")
            } else if (packageVisibility == InstalledPackageVisibility.UNKNOWN) {
                add("PackageManager inventory did not include this app, so the result is not trustworthy.")
            }
            if (nativeDataDirectories == null) {
                add("The native /data/data stat could not run, so host apps it alone would find may be missing.")
            }
        }

        return VirtualizationHostAppProbeResult(
            packageVisibility = packageVisibility,
            findings = findings,
            issues = issues,
        )
    }

    private fun checkFusePath(packageName: String): Boolean {
        val paths = listOf(
            "/storage/emulated/0/Android/data/$packageName",
            "/storage/emulated/0/Android/obb/$packageName",
        )
        return paths.any { path ->
            runCatching {
                File(path).exists() && File(path).isDirectory
            }.getOrDefault(false)
        }
    }
}
