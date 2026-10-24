package com.kaizehuang.vernier.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.github.xiaotong6666.uihelper.mode.AdaptiveInteractionRuntime
import io.github.xiaotong6666.uihelper.mode.AdaptiveTheme
import io.github.xiaotong6666.uihelper.mode.materialCompatibilityColorSchemeFromMiuix
import io.github.xiaotong6666.uihelper.mode.UiMode

/** Distinct native MIUIX and dynamic Material Expressive skins over the same UI state. */
@Composable
public fun VernierAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    uiMode: UiMode = UiMode.Miuix,
    content: @Composable () -> Unit,
) {
    // Vernier brand tokens beat wallpaper extraction: the instrument palette is hand-tuned,
    // and the status lamps must stay the color of a real instrument's lamps. Both skins
    // (Material Expressive and native MIUIX) derive their colors from the same tokens.
    val vernier = if (darkTheme) darkVernierPalette() else lightVernierPalette()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !darkTheme
        }
    }

    val appMaterialScheme = vernierColorScheme(vernier, darkTheme)
    AdaptiveTheme(uiMode = uiMode, darkTheme = darkTheme, materialColorScheme = appMaterialScheme) {
        // Keep MIUIX's own colors. Material uses the Vernier scheme directly.
        val colorScheme = if (uiMode == UiMode.Miuix) {
            materialCompatibilityColorSchemeFromMiuix(appMaterialScheme)
        } else appMaterialScheme
        val base = remember(colorScheme, darkTheme, uiMode) {
            if (uiMode == UiMode.Miuix) surfacePalette(colorScheme, darkTheme)
            else expressiveSurfacePalette(colorScheme, darkTheme)
        }
        // The status lamps come from the Vernier tokens in both skins: grouped utility
        // screens may render inside MIUIX chrome, but the lamps stay instrument lamps.
        val palette = base.copy(
            positive = vernier.statusClear,
            caution = vernier.statusWarning,
            critical = vernier.statusDanger,
            neutral = vernier.statusNotChecked,
        )

        // A single stable theme subtree is essential: switching between two separate theme
        // branches disposes VernierApp and can restart scans / reset the selected tab.
        // MIUIX owns its native components, colors and Folme motion; this Material layer is
        // only for the remaining Compose Material components and cross-skin fallbacks.
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = if (uiMode == UiMode.Material) VernierMaterialTypography else Typography,
            shapes = if (uiMode == UiMode.Material) ExpressiveShapes else Shapes,
            motionScheme = if (uiMode == UiMode.Material) MotionScheme.expressive() else MotionScheme.standard(),
        ) {
            AdaptiveInteractionRuntime {
                CompositionLocalProvider(
                    LocalSurfacePalette provides palette,
                    LocalVernierPalette provides vernier,
                    content = content,
                )
            }
        }
    }
}
