package com.kaizehuang.vernier.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kaizehuang.vernier.core.designsystem.R

/**
 * Vernier type system: the status word is the visual hero, every technical number is monospaced.
 *
 * Display (Space Grotesk) carries headings and the verdict sentence; mono (IBM Plex Mono) carries
 * counters, progress and build info; Chinese falls back to the platform font — Space Grotesk has
 * no CJK glyphs, and Noto Sans SC would add megabytes for no measurable gain over the system font.
 */
internal val VernierDisplayFamily: FontFamily = FontFamily(
    (400..700 step 100).map { weight ->
        Font(
            resId = R.font.space_grotesk_variable,
            weight = FontWeight(weight),
            variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
        )
    },
)

internal val VernierMonoFamily: FontFamily = FontFamily(
    Font(R.font.ibm_plex_mono_variable, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_mono_semibold, FontWeight.SemiBold),
)

/** Material 3 typography on the display family; Material components keep expected metrics. */
internal val VernierMaterialTypography: Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.02).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.01).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.01).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

/**
 * Named styles of the Vernier screens, mirroring the prototype's CSS: the 27px verdict sentence,
 * the mono counters, the 15.5px group names and the 26px detail titles.
 */
public object VernierTypography {
    /** The hero sentence of the measure screen: "18 项检测完成，一切正常". */
    public val verdict: TextStyle = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 27.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.02).sp,
    )

    /** "正在检测：xxx" during a scan, same weight as the verdict. */
    public val verdictScanning: TextStyle = verdict

    /** Row-end counters and record summaries: "3 项危险". */
    public val count: TextStyle = TextStyle(
        fontFamily = VernierMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.5.sp,
        lineHeight = 16.sp,
    )

    /** Scale labels and tiny annotations. */
    public val scaleLabel: TextStyle = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    /** Measurement group name in a row. */
    public val groupName: TextStyle = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.5.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.01).sp,
    )

    /** Group / detector description under a name. */
    public val description: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.5.sp,
        lineHeight = 16.sp,
    )

    /** Page title of the group detail (the shared element). */
    public val detailTitle: TextStyle = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.02).sp,
    )

    /** Records page title. */
    public val pageTitle: TextStyle = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.02).sp,
    )

    /** Status word at the end of a row: 正常 / 注意 / 危险 / 未检测. */
    public val statusWord: TextStyle = TextStyle(
        fontFamily = VernierDisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    /** Build info, versions, durations — all technical values. */
    public val mono: TextStyle = TextStyle(
        fontFamily = VernierMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    )

}
