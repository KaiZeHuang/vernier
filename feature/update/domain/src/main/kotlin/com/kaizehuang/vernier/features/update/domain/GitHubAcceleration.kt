package com.kaizehuang.vernier.features.update.domain

/**
 * Whether update checks and Nightly downloads reach GitHub through the gh-proxy.com acceleration
 * service. It stays [UNDECIDED] until the user chooses, and undecided requests go to GitHub directly.
 */
enum class GitHubAcceleration {
    UNDECIDED,
    ENABLED,
    DISABLED,
}
