package com.kaizehuang.vernier.features.dangerousapps.data.repository

import android.content.Context
import com.kaizehuang.vernier.capability.packageinventory.data.AndroidInstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.data.InstalledPackageVisibilityChecker
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousPackageVisibility

data class DangerousPackageInventory(
    val packageNames: Set<String>,
    val visibility: DangerousPackageVisibility,
    val suspiciouslyLow: Boolean,
    val issue: String? = null,
)

object PackageVisibilityChecker {

    fun inspect(
        reader: InstalledPackageInventoryReader,
    ): DangerousPackageInventory {
        return when (val result = reader.read()) {
            is InstalledPackageInventoryResult.Available -> {
                val inventory = result.inventory
                DangerousPackageInventory(
                    packageNames = inventory.packageNames,
                    visibility = inventory.visibility.toDangerousVisibility(),
                    suspiciouslyLow = inventory.suspiciouslyLowInventory,
                    issue = if (inventory.visibility == InstalledPackageVisibility.UNKNOWN) {
                        "PackageManager inventory did not include this app, so package absence is inconclusive."
                    } else {
                        null
                    },
                )
            }

            is InstalledPackageInventoryResult.Unavailable -> {
                DangerousPackageInventory(
                    packageNames = emptySet(),
                    visibility = DangerousPackageVisibility.UNKNOWN,
                    suspiciouslyLow = false,
                    issue = "PackageManager inventory unavailable: ${result.failure.detail}",
                )
            }
        }
    }

    fun inspect(context: Context): DangerousPackageInventory {
        return inspect(AndroidInstalledPackageInventoryReader(context.applicationContext))
    }

    @Deprecated("Use inspect() so query failure remains explicit.")
    @Suppress("DEPRECATION")
    fun detect(
        context: Context,
        installedPackageCount: Int,
    ): DangerousPackageVisibility {
        return when (InstalledPackageVisibilityChecker.detect(context, installedPackageCount)) {
            InstalledPackageVisibility.FULL -> DangerousPackageVisibility.FULL
            InstalledPackageVisibility.RESTRICTED -> DangerousPackageVisibility.RESTRICTED
            InstalledPackageVisibility.UNKNOWN -> DangerousPackageVisibility.UNKNOWN
        }
    }

    @Deprecated("Use inspect() so query failure remains explicit.")
    @Suppress("DEPRECATION")
    fun getInstalledPackages(context: Context): Set<String> {
        return InstalledPackageVisibilityChecker.getInstalledPackages(context)
    }

    @Deprecated("Use inspect() so visibility and inventory are evaluated together.")
    fun hasSuspiciouslyLowInventory(
        packageVisibility: DangerousPackageVisibility,
        installedPackageCount: Int,
    ): Boolean {
        val visibility = when (packageVisibility) {
            DangerousPackageVisibility.FULL -> InstalledPackageVisibility.FULL
            DangerousPackageVisibility.RESTRICTED -> InstalledPackageVisibility.RESTRICTED
            DangerousPackageVisibility.UNKNOWN -> InstalledPackageVisibility.UNKNOWN
        }
        return InstalledPackageVisibilityChecker.hasSuspiciouslyLowInventory(
            visibility = visibility,
            installedPackageCount = installedPackageCount,
        )
    }

    private fun InstalledPackageVisibility.toDangerousVisibility(): DangerousPackageVisibility {
        return when (this) {
            InstalledPackageVisibility.FULL -> DangerousPackageVisibility.FULL
            InstalledPackageVisibility.RESTRICTED -> DangerousPackageVisibility.RESTRICTED
            InstalledPackageVisibility.UNKNOWN -> DangerousPackageVisibility.UNKNOWN
        }
    }
}
