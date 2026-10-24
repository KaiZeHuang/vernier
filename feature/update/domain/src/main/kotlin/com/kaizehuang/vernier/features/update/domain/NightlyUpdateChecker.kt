package com.kaizehuang.vernier.features.update.domain

import com.kaizehuang.vernier.features.update.domain.UpdateCheckResult

fun interface NightlyUpdateChecker {
    suspend fun check(
        currentVersionCode: Int,
        currentCommitSha: String,
    ): UpdateCheckResult
}
