package com.kaizehuang.vernier.sdk

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledApplicationRecord
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventory
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageInventoryFailure
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageInventoryFailureKind
import org.junit.Assert.assertEquals
import org.junit.Test

class PackageVisibilityTest {

    private fun inventory(visibility: InstalledPackageVisibility, suspiciouslyLow: Boolean) =
        InstalledPackageInventoryResult.Available(
            InstalledPackageInventory(
                applications = listOf("a.one", "a.two", "a.two").map { InstalledApplicationRecord(it, it, emptySet()) },
                visibility = visibility,
                suspiciouslyLowInventory = suspiciouslyLow,
            ),
        )

    @Test
    fun `each inventory visibility maps to the scope of the same name`() {
        mapOf(
            InstalledPackageVisibility.UNKNOWN to PackageVisibility.Scope.UNKNOWN,
            InstalledPackageVisibility.FULL to PackageVisibility.Scope.FULL,
            InstalledPackageVisibility.RESTRICTED to PackageVisibility.Scope.RESTRICTED,
        ).forEach { (visibility, scope) ->
            assertEquals(PackageVisibility(scope, 2, true), inventory(visibility, suspiciouslyLow = true).toPackageVisibility())
        }
    }

    @Test
    fun `an unreadable inventory is an unknown scope with nothing visible`() {
        val failure = PackageInventoryFailure(PackageInventoryFailureKind.PLATFORM_QUERY_FAILED, "denied")

        assertEquals(
            PackageVisibility(PackageVisibility.Scope.UNKNOWN, visiblePackageCount = 0, suspiciouslyLow = false),
            InstalledPackageInventoryResult.Unavailable(failure).toPackageVisibility(),
        )
    }
}
