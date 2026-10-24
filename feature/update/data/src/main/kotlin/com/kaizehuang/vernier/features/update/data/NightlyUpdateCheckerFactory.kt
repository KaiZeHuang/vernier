package com.kaizehuang.vernier.features.update.data

import android.content.Context
import com.kaizehuang.vernier.features.update.domain.NightlyUpdateChecker
import kotlinx.coroutines.flow.first

fun createNightlyUpdateChecker(appContext: Context): NightlyUpdateChecker {
    val accelerationStore = GitHubAccelerationStore.getInstance(appContext)
    return UpdateRepository(
        cache = UpdateCacheStore.getInstance(appContext),
        currentRoute = { GitHubRoute.of(accelerationStore.acceleration.first()) },
    )
}
