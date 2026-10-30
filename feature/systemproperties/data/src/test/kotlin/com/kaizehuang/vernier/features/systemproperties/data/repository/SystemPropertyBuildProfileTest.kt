package com.kaizehuang.vernier.features.systemproperties.data.repository

import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertyCategory
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SystemPropertyBuildProfileTest {

    private val releaseFingerprint =
        "google/husky/husky:14/AP1A.240305.019/1234567:user/release-keys"
    private val userdebugFingerprint =
        "google/husky/husky:14/AP1A.240305.019/1234567:userdebug/test-keys"

    @Test
    fun `a rewritten build type contradicts the fingerprint`() {
        val signal = buildBuildProfileConsistencySignal(
            fingerprint = userdebugFingerprint,
            buildType = "user",
            buildTags = "test-keys",
        )

        assertNotNull(signal)
        assertEquals(SystemPropertySeverity.DANGER, signal?.severity)
        assertEquals(SystemPropertyCategory.PROPERTY_CONSISTENCY, signal?.category)
        assertTrue(signal?.detail.orEmpty(), signal?.detail.orEmpty().contains("userdebug"))
    }

    @Test
    fun `a rewritten build tags alone is enough`() {
        val signal = buildBuildProfileConsistencySignal(
            fingerprint = "google/husky/husky:14/AP1A.240305.019/1234567:user/test-keys",
            buildType = "user",
            buildTags = "release-keys",
        )

        assertEquals(SystemPropertySeverity.DANGER, signal?.severity)
    }

    @Test
    fun `an honest release device stays silent`() {
        assertNull(
            buildBuildProfileConsistencySignal(
                fingerprint = releaseFingerprint,
                buildType = "user",
                buildTags = "release-keys",
            ),
        )
    }

    @Test
    fun `an honest userdebug device stays silent`() {
        assertNull(
            buildBuildProfileConsistencySignal(
                fingerprint = userdebugFingerprint,
                buildType = "userdebug",
                buildTags = "test-keys",
            ),
        )
    }

    @Test
    fun `the reverse direction is not evidence of hiding`() {
        // A fingerprint that looks less locked than the constants is odd, but it is not a module
        // hiding a userdebug build behind a release claim.
        assertNull(
            buildBuildProfileConsistencySignal(
                fingerprint = releaseFingerprint,
                buildType = "userdebug",
                buildTags = "test-keys",
            ),
        )
    }

    @Test
    fun `an unparseable fingerprint is left alone`() {
        assertNull(
            buildBuildProfileConsistencySignal(
                fingerprint = "not-a-fingerprint",
                buildType = "user",
                buildTags = "release-keys",
            ),
        )
        assertNull(
            buildBuildProfileConsistencySignal(
                fingerprint = "",
                buildType = "user",
                buildTags = "release-keys",
            ),
        )
    }
}
