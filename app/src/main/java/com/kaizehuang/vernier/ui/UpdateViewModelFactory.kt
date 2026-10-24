package com.kaizehuang.vernier.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.kaizehuang.vernier.BuildConfig
import com.kaizehuang.vernier.features.update.data.createNightlyUpdateChecker
import com.kaizehuang.vernier.features.update.ui.UpdateViewModel

internal fun updateViewModelFactory(context: Context): ViewModelProvider.Factory {
    val appContext = context.applicationContext
    return UpdateViewModel.factory(
        createChecker = { createNightlyUpdateChecker(appContext) },
        currentVersionCode = BuildConfig.VERSION_CODE,
        currentCommitSha = BuildConfig.BUILD_HASH,
    )
}
