package com.kaizehuang.vernier.core.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Build metadata of the running application.
 *
 * Only the application module owns `BuildConfig`, so libraries receive these values from the
 * composition root instead of reading a generated class they cannot see.
 */
@Immutable
public data class AppBuildInfo(
    public val versionName: String,
    public val versionCode: Int,
    public val buildHash: String,
    public val buildTimeUtc: String,
    public val isAlphaVersion: Boolean,
)

public val LocalAppBuildInfo: ProvidableCompositionLocal<AppBuildInfo> =
    staticCompositionLocalOf { error("AppBuildInfo must be provided by the application composition root") }
