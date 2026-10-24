package com.kaizehuang.vernier.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Vernier design tokens: the instrument palette of the Vernier environment inspector.
 *
 * Light is a Braun paper surface (default, cold start); dark is a warm ink surface with the
 * original instrument lamp colors. Both sets are hand-tuned: brand tokens beat wallpaper
 * extraction, and the status lamps must stay the color of a real instrument's lamps.
 */
@Immutable
public class VernierPalette internal constructor(
    /** The page behind grouped containers. */
    public val base: Color,
    /** A card or group of rows sitting on [base]. */
    public val surface: Color,
    /** Pressed/selected fill inside [surface]. */
    public val interactive: Color,
    /** Hairlines, scale ticks, separators. */
    public val hairline: Color,
    /** Primary text: warm black on paper, cream on ink. Never pure black or white. */
    public val ink: Color,
    /** Secondary text: labels and support copy. */
    public val ink2: Color,
    /** Weak text: counters, indices, end values. */
    public val ink3: Color,
    /** The single interactive focus of a screen (signal orange). */
    public val accent: Color,
    /** Status lamps. */
    public val statusClear: Color,
    public val statusWarning: Color,
    public val statusDanger: Color,
    public val statusNotChecked: Color,
    /** Tab-bar / segmented-control knob. */
    public val knob: Color,
) {
    /** Status lamp for [status]; the lamp color is a token, never decoration. */
    public fun statusColor(status: VernierStatus): Color = when (status) {
        VernierStatus.CLEAR -> statusClear
        VernierStatus.WARNING -> statusWarning
        VernierStatus.DANGER -> statusDanger
        VernierStatus.NOT_CHECKED -> statusNotChecked
    }
}

/** The four verdicts of the measurement language: 正常 / 注意 / 危险 / 未检测. */
public enum class VernierStatus {
    CLEAR,
    WARNING,
    DANGER,
    NOT_CHECKED,
}

internal val LocalVernierPalette: ProvidableCompositionLocal<VernierPalette> =
    staticCompositionLocalOf { lightVernierPalette() }

public object VernierTheme {
    public val palette: VernierPalette
        @Composable @ReadOnlyComposable
        get() = LocalVernierPalette.current
}

/** Braun paper: warm white with a faint yellow-grey cast, never a cold white. */
public fun lightVernierPalette(): VernierPalette = VernierPalette(
    base = Color(0xFFF0EDE5),
    surface = Color(0xFFE7E2D6),
    interactive = Color(0xFFDDD6C6),
    hairline = Color(0xFFCFC8B8),
    ink = Color(0xFF1A1A18),
    ink2 = Color(0xFF6E6A60),
    ink3 = Color(0xFF8A8578),
    accent = Color(0xFFC24A00),
    statusClear = Color(0xFF1E8E3D),
    statusWarning = Color(0xFFA66A00),
    statusDanger = Color(0xFFC02820),
    statusNotChecked = Color(0xFF8A8578),
    knob = Color(0xFFFFFFFF),
)

/** Warm ink: brown-black, cream text, and the original instrument lamp colors. */
public fun darkVernierPalette(): VernierPalette = VernierPalette(
    base = Color(0xFF12110E),
    surface = Color(0xFF1B1915),
    interactive = Color(0xFF242019),
    hairline = Color(0xFF2B2822),
    ink = Color(0xFFF2EDE3),
    ink2 = Color(0xFFA39B8A),
    ink3 = Color(0xFF7D766A),
    accent = Color(0xFFFF7A1A),
    statusClear = Color(0xFF34C759),
    statusWarning = Color(0xFFFF9F0A),
    statusDanger = Color(0xFFFF453A),
    statusNotChecked = Color(0xFF7D766A),
    knob = Color(0xFFF2EDE3),
)

/** Material 3 scheme derived from the Vernier tokens; dynamic color is never used. */
public fun vernierColorScheme(palette: VernierPalette, dark: Boolean): ColorScheme {
    // Two-level instrument surfaces: page (base) and card (surface); anything above is the
    // pressed fill. Expressive layers read surfaceBright, so give it a real surface value.
    return if (dark) {
        darkColorScheme(
            background = palette.base,
            onBackground = palette.ink,
            surface = palette.base,
            onSurface = palette.ink,
            surfaceVariant = palette.surface,
            onSurfaceVariant = palette.ink2,
            surfaceDim = palette.base,
            surfaceBright = palette.interactive,
            surfaceTint = palette.accent,
            surfaceContainerLowest = palette.base,
            surfaceContainerLow = palette.surface,
            surfaceContainer = palette.surface,
            surfaceContainerHigh = palette.interactive,
            surfaceContainerHighest = palette.interactive,
            outline = palette.hairline,
            outlineVariant = palette.hairline,
            primary = palette.accent,
            onPrimary = palette.base,
            primaryContainer = palette.interactive,
            onPrimaryContainer = palette.ink,
            secondary = palette.ink2,
            onSecondary = palette.base,
            secondaryContainer = palette.interactive,
            onSecondaryContainer = palette.ink,
            tertiary = palette.accent,
            onTertiary = palette.base,
            tertiaryContainer = palette.interactive,
            onTertiaryContainer = palette.ink,
            error = palette.statusDanger,
            onError = palette.base,
            errorContainer = palette.interactive,
            onErrorContainer = palette.statusDanger,
            inverseSurface = palette.ink,
            inverseOnSurface = palette.base,
            inversePrimary = palette.accent,
            scrim = palette.base,
        )
    } else {
        lightColorScheme(
            background = palette.base,
            onBackground = palette.ink,
            surface = palette.base,
            onSurface = palette.ink,
            surfaceVariant = palette.surface,
            onSurfaceVariant = palette.ink2,
            surfaceDim = palette.interactive,
            surfaceBright = palette.knob,
            surfaceTint = palette.accent,
            surfaceContainerLowest = palette.base,
            surfaceContainerLow = palette.surface,
            surfaceContainer = palette.surface,
            surfaceContainerHigh = palette.interactive,
            surfaceContainerHighest = palette.interactive,
            outline = palette.hairline,
            outlineVariant = palette.hairline,
            primary = palette.accent,
            onPrimary = palette.knob,
            primaryContainer = palette.surface,
            onPrimaryContainer = palette.ink,
            secondary = palette.ink2,
            onSecondary = palette.base,
            secondaryContainer = palette.surface,
            onSecondaryContainer = palette.ink,
            tertiary = palette.accent,
            onTertiary = palette.knob,
            tertiaryContainer = palette.surface,
            onTertiaryContainer = palette.ink,
            error = palette.statusDanger,
            onError = palette.knob,
            errorContainer = palette.surface,
            onErrorContainer = palette.statusDanger,
            inverseSurface = palette.ink,
            inverseOnSurface = palette.base,
            inversePrimary = palette.accent,
            scrim = palette.ink,
        )
    }
}
