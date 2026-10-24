package com.kaizehuang.vernier.capability.packageinventory.data

import android.content.Context
import android.os.Build
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryAnomalyPolicy
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibilityPolicy
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageVisibilityEnvironment

/**
 * Compatibility facade for callers that have not moved to [InstalledPackageInventoryReader].
 *
 * New code should consume the inventory result as one value. Keeping the old entry points avoids
 * a source-level breaking change while internal callers migrate away from the former two-step
 * `getInstalledPackages()` + `detect(count)` protocol, which could not distinguish query failure
 * from a successful empty response.
 */
public object InstalledPackageVisibilityChecker {

    public const val SUSPICIOUSLY_LOW_VISIBLE_PACKAGE_COUNT: Int =
        InstalledPackageInventoryAnomalyPolicy.SUSPICIOUSLY_LOW_VISIBLE_PACKAGE_COUNT

    public fun inspect(context: Context): InstalledPackageInventoryResult {
        return AndroidInstalledPackageInventoryReader(context.applicationContext).read()
    }

    public fun hasSuspiciouslyLowInventory(
        visibility: InstalledPackageVisibility,
        installedPackageCount: Int,
        sdkInt: Int = Build.VERSION.SDK_INT,
    ): Boolean {
        return InstalledPackageInventoryAnomalyPolicy.isSuspiciouslyLow(
            visibility = visibility,
            installedPackageCount = installedPackageCount,
            sdkInt = sdkInt,
        )
    }

    @Deprecated(
        message = "Read InstalledPackageInventoryResult so query failure remains explicit.",
        replaceWith = ReplaceWith("InstalledPackageVisibilityChecker.inspect(context)"),
    )
    public fun getInstalledPackages(context: Context): Set<String> {
        return when (val result = inspect(context)) {
            is InstalledPackageInventoryResult.Available -> result.inventory.packageNames
            is InstalledPackageInventoryResult.Unavailable -> emptySet()
        }
    }

    @Deprecated(
        message = "Visibility cannot be derived safely from a count; inspect the inventory.",
        replaceWith = ReplaceWith("InstalledPackageVisibilityChecker.inspect(context)"),
    )
    public fun detect(
        context: Context,
        installedPackageCount: Int,
    ): InstalledPackageVisibility {
        return resolveVisibility(
            inventoryHasCaller = installedPackageCount > 0,
            environment = AndroidPackageVisibilityEnvironmentProvider(context).read(),
        )
    }

    internal fun resolveVisibility(
        inventoryHasCaller: Boolean,
        environment: PackageVisibilityEnvironment,
    ): InstalledPackageVisibility {
        return InstalledPackageVisibilityPolicy.evaluate(
            environment = environment,
            callerPackageObserved = inventoryHasCaller,
        )
    }
}
