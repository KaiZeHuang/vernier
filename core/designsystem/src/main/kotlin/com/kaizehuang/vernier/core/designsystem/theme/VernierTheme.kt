package com.kaizehuang.vernier.core.designsystem.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * The Vernier instrument theme: hand-tuned tokens, never dynamic wallpaper color.
 *
 * Cold start is always light (the Braun paper surface); dark is a manual switch. The theme only
 * swaps tokens — the layout never moves when the appearance changes.
 */
@Composable
public fun VernierTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) darkVernierPalette() else lightVernierPalette()
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    CompositionLocalProvider(LocalVernierPalette provides palette) {
        MaterialTheme(
            colorScheme = vernierColorScheme(palette, darkTheme),
            typography = VernierMaterialTypography,
            content = content,
        )
    }
}

/** Default status bar / background scrim behind edge-to-edge content. */
public val VernierScrim: Color = Color(0x40000000)
