package com.kaizehuang.vernier.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colors for grouped utility screens. MIUIX mode derives surfaces from MiuixTheme and uses the
 * same status accents as the native MIUIX treatment instead of mixing in a second platform's
 * system palette.
 */
@Immutable
public data class SurfacePalette internal constructor(
    /** The page behind grouped containers. */
    public val groupedBackground: Color,
    /** A container on [groupedBackground]: a card or a group of rows. */
    public val groupedSurface: Color,
    /** A fill set inside a [groupedSurface], such as a tile of facts. */
    public val groupedInset: Color,
    /** Hairlines between rows of one container. */
    public val separator: Color,
    public val positive: Color,
    public val caution: Color,
    public val critical: Color,
    public val neutral: Color,
)

internal val LocalSurfacePalette: ProvidableCompositionLocal<SurfacePalette> =
    staticCompositionLocalOf { surfacePalette(LightMonochromeScheme, dark = false) }

public object SurfaceTheme {
    public val palette: SurfacePalette
        @Composable @ReadOnlyComposable
        get() = LocalSurfacePalette.current
}

// In light appearance the containers are the brightest surface on a tinted page; in dark
// appearance the page is the darkest surface and the containers step up from it.
internal fun surfacePalette(scheme: ColorScheme, dark: Boolean): SurfacePalette = if (dark) {
    SurfacePalette(
        groupedBackground = scheme.surfaceContainerLowest,
        groupedSurface = scheme.surfaceContainerLow,
        groupedInset = scheme.surfaceContainerHigh,
        separator = scheme.outlineVariant.copy(alpha = 0.55f),
        positive = Color(0xFF36D167),
        caution = Color(0xFFF5A623),
        critical = scheme.error,
        neutral = scheme.onSurfaceVariant,
    )
} else {
    SurfacePalette(
        groupedBackground = scheme.surfaceContainerLowest,
        groupedSurface = scheme.surfaceContainer,
        groupedInset = scheme.surfaceContainer,
        separator = scheme.outlineVariant.copy(alpha = 0.7f),
        positive = Color(0xFF36D167),
        caution = Color(0xFFF5A623),
        critical = scheme.error,
        neutral = scheme.onSurfaceVariant,
    )
}

/** Material Expressive layers: tonal page, bright grouped items, and higher inset facts. */
internal fun expressiveSurfacePalette(scheme: ColorScheme, dark: Boolean): SurfacePalette =
    if (dark) {
        SurfacePalette(
            groupedBackground = scheme.surfaceContainer,
            groupedSurface = scheme.surfaceBright,
            groupedInset = scheme.surfaceContainerHigh,
            separator = scheme.outlineVariant.copy(alpha = 0.55f),
            positive = Color(0xFF30D158),
            caution = Color(0xFFFF9F0A),
            critical = Color(0xFFFF453A),
            neutral = Color(0xFF98989D),
        )
    } else {
        SurfacePalette(
            groupedBackground = scheme.surfaceContainer,
            groupedSurface = scheme.surfaceBright,
            groupedInset = scheme.surfaceContainerHigh,
            separator = scheme.outlineVariant.copy(alpha = 0.7f),
            positive = Color(0xFF34C759),
            caution = Color(0xFFFF9500),
            critical = Color(0xFFFF3B30),
            neutral = Color(0xFF8E8E93),
        )
    }
