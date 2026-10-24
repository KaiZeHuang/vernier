package com.kaizehuang.vernier.features.dangerousapps.data.repository

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledApplicationRecord
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventory
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageInventoryFailure
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageInventoryFailureKind
import com.kaizehuang.vernier.features.dangerousapps.domain.DangerousPackageVisibility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class PackageVisibilityCheckerTest {

    @Test
    fun `query failure maps to an explicit unknown feature result`() {
        val inventory = PackageVisibilityChecker.inspect(
            InstalledPackageInventoryReader {
                InstalledPackageInventoryResult.Unavailable(
                    PackageInventoryFailure(
                        kind = PackageInventoryFailureKind.PLATFORM_QUERY_FAILED,
                        detail = "SecurityException",
                    ),
                )
            },
        )

        assertEquals(DangerousPackageVisibility.UNKNOWN, inventory.visibility)
        assertEquals(emptySet<String>(), inventory.packageNames)
        assertFalse(inventory.suspiciouslyLow)
        assertNotNull(inventory.issue)
    }

    @Test
    fun `available inventory keeps the shared assessment and package set`() {
        val inventory = PackageVisibilityChecker.inspect(
            InstalledPackageInventoryReader {
                InstalledPackageInventoryResult.Available(
                    InstalledPackageInventory(
                        applications = listOf(
                            InstalledApplicationRecord(
                                packageName = "example.target",
                                label = "example.target",
                                metadataKeys = emptySet(),
                            ),
                        ),
                        visibility = InstalledPackageVisibility.RESTRICTED,
                        suspiciouslyLowInventory = false,
                    ),
                )
            },
        )

        assertEquals(DangerousPackageVisibility.RESTRICTED, inventory.visibility)
        assertEquals(setOf("example.target"), inventory.packageNames)
        assertFalse(inventory.suspiciouslyLow)
    }
}
