package com.kaizehuang.vernier.features.update.data

import com.kaizehuang.vernier.features.update.domain.GitHubAcceleration

/**
 * How update requests and the Nightly download reach GitHub. gh-proxy.com fetches the GitHub URL
 * written after its own address and returns that response, release assets included:
 * https://gh-proxy.com/docs/github-accelerator
 */
internal enum class GitHubRoute {
    DIRECT,
    GH_PROXY,
    ;

    fun url(gitHubUrl: String): String = when (this) {
        DIRECT -> gitHubUrl
        GH_PROXY -> GH_PROXY_PREFIX + gitHubUrl
    }

    /**
     * gh-proxy.com's firewall answers 403 to every path containing the `...` between a comparison's
     * base and head, escaped or not, so only a direct check can compare commits.
     */
    val comparesCommits: Boolean
        get() = this == DIRECT

    companion object {
        fun of(acceleration: GitHubAcceleration): GitHubRoute =
            if (acceleration == GitHubAcceleration.ENABLED) GH_PROXY else DIRECT
    }
}

private const val GH_PROXY_PREFIX = "https://gh-proxy.com/"
