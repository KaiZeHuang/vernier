package com.kaizehuang.vernier.sdk

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.core.evidence.ContractValue

/**
 * How much of the installed package list this process can see. It bounds every detector that reads
 * the package inventory, so a host can tell a clean inventory from one that package visibility
 * filtering hides, and decide whether to request `QUERY_ALL_PACKAGES`.
 */
@ContractValue
public class PackageVisibility(
    public val scope: Scope,
    public val visiblePackageCount: Int,
    /** Whether fewer packages are visible than any Android release installs, which suggests hiding. */
    public val suspiciouslyLow: Boolean,
) {
    public enum class Scope {
        /** The inventory could not be read, so the scope is not known. */
        UNKNOWN,

        /** Every installed package is visible. */
        FULL,

        /** Package visibility filtering applies to this process. */
        RESTRICTED,
    }
}

internal fun InstalledPackageInventoryResult.toPackageVisibility(): PackageVisibility {
    val inventory = (this as? InstalledPackageInventoryResult.Available)?.inventory
        ?: return PackageVisibility(PackageVisibility.Scope.UNKNOWN, visiblePackageCount = 0, suspiciouslyLow = false)
    val scope = when (inventory.visibility) {
        InstalledPackageVisibility.UNKNOWN -> PackageVisibility.Scope.UNKNOWN
        InstalledPackageVisibility.FULL -> PackageVisibility.Scope.FULL
        InstalledPackageVisibility.RESTRICTED -> PackageVisibility.Scope.RESTRICTED
    }
    return PackageVisibility(scope, inventory.visiblePackageCount, inventory.suspiciouslyLowInventory)
}
