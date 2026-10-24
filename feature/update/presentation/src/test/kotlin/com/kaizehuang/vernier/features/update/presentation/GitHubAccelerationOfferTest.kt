package com.kaizehuang.vernier.features.update.presentation

import com.kaizehuang.vernier.features.update.domain.GitHubAcceleration
import java.util.Locale
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GitHubAccelerationOfferTest {

    @Test
    fun `undecided users reading Chinese are asked in every script and region`() {
        listOf("zh-CN", "zh-TW", "zh-Hant-HK", "zh-SG", "zh").forEach { tag ->
            assertTrue(
                tag,
                shouldOfferGitHubAcceleration(GitHubAcceleration.UNDECIDED, Locale.forLanguageTag(tag)),
            )
        }
    }

    @Test
    fun `undecided users reading another language are not asked`() {
        listOf("en-US", "ja-JP", "ko-KR", "vi-VN").forEach { tag ->
            assertFalse(
                tag,
                shouldOfferGitHubAcceleration(GitHubAcceleration.UNDECIDED, Locale.forLanguageTag(tag)),
            )
        }
    }

    @Test
    fun `a recorded choice is not asked again`() {
        val chinese = Locale.forLanguageTag("zh-CN")

        assertFalse(shouldOfferGitHubAcceleration(GitHubAcceleration.ENABLED, chinese))
        assertFalse(shouldOfferGitHubAcceleration(GitHubAcceleration.DISABLED, chinese))
    }
}
