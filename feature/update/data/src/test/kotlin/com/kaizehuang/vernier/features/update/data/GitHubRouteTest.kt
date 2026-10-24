package com.kaizehuang.vernier.features.update.data

import com.kaizehuang.vernier.features.update.domain.GitHubAcceleration
import org.junit.Assert.assertEquals
import org.junit.Test

class GitHubRouteTest {

    @Test
    fun `only enabled acceleration goes through gh-proxy`() {
        assertEquals(GitHubRoute.DIRECT, GitHubRoute.of(GitHubAcceleration.UNDECIDED))
        assertEquals(GitHubRoute.DIRECT, GitHubRoute.of(GitHubAcceleration.DISABLED))
        assertEquals(GitHubRoute.GH_PROXY, GitHubRoute.of(GitHubAcceleration.ENABLED))
    }

    @Test
    fun `gh-proxy prefixes the whole GitHub URL`() {
        assertEquals(TEST_DOWNLOAD_URL, GitHubRoute.DIRECT.url(TEST_DOWNLOAD_URL))
        assertEquals("https://gh-proxy.com/$TEST_DOWNLOAD_URL", GitHubRoute.GH_PROXY.url(TEST_DOWNLOAD_URL))
    }
}
