package com.kaizehuang.vernier.features.heapresidue.data

import com.kaizehuang.vernier.features.heapresidue.domain.HeapResidueRelease
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HeapResidueReleasesTest {
    @Test fun releasesBeforeAndroid12AreNotRunEvenAsPreviews() {
        for (api in listOf(29, 30)) for (prerelease in listOf(false, true)) {
            assertNull(HeapResidueReleases.classify(api, prerelease))
        }
    }

    @Test fun android12To17AreAuditedBaselines() {
        for (api in 31..37) assertEquals(HeapResidueRelease.AUDITED, HeapResidueReleases.classify(api, prerelease = false))
    }

    @Test fun newerReleasesRunButAreNeverReportedAsAudited() {
        for (api in listOf(38, 39, 50)) assertEquals(HeapResidueRelease.NEWER, HeapResidueReleases.classify(api, prerelease = false))
    }

    @Test fun previewReportingAnAuditedApiLevelIsNotAudited() {
        for (api in listOf(31, 36, 37, 38)) {
            assertEquals(HeapResidueRelease.PRERELEASE, HeapResidueReleases.classify(api, prerelease = true))
        }
    }
}
