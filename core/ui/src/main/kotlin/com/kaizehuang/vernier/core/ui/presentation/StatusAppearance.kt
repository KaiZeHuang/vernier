package com.kaizehuang.vernier.core.ui.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.GppBad
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.core.ui.R

@Immutable
public data class StatusAppearance(
    val label: String,
    val metaLabel: String?,
    val icon: ImageVector,
    val iconTint: Color,
) {
    /** The tint thinned into a background: text over it keeps the content color, not the tint. */
    val tintWash: Color get() = iconTint.copy(alpha = 0.14f)
}

@Composable
public fun rememberStatusAppearance(status: DetectorStatus): StatusAppearance {
    val palette = SurfaceTheme.palette
    val infoLabel = stringResource(R.string.status_info)
    val infoErrorLabel = stringResource(R.string.status_info_error)
    val infoSupportLabel = stringResource(R.string.status_info_support)
    val allClearLabel = stringResource(R.string.status_all_clear)
    val warningLabel = stringResource(R.string.status_warning)
    val dangerLabel = stringResource(R.string.status_danger)

    return when (status.severity) {
        DetectionSeverity.INFO -> {
            val isError = status.infoKind == InfoKind.ERROR

            StatusAppearance(
                label = infoLabel,
                metaLabel = if (isError) infoErrorLabel else infoSupportLabel,
                icon = if (isError) Icons.Rounded.ErrorOutline else Icons.Rounded.TipsAndUpdates,
                iconTint = if (isError) palette.critical else palette.neutral,
            )
        }

        DetectionSeverity.ALL_CLEAR -> StatusAppearance(
            label = allClearLabel,
            metaLabel = null,
            icon = Icons.Rounded.Verified,
            iconTint = palette.positive,
        )

        DetectionSeverity.WARNING -> StatusAppearance(
            label = warningLabel,
            metaLabel = null,
            icon = Icons.Rounded.Warning,
            iconTint = palette.caution,
        )

        DetectionSeverity.DANGER -> StatusAppearance(
            label = dangerLabel,
            metaLabel = null,
            icon = Icons.Rounded.GppBad,
            iconTint = palette.critical,
        )
    }
}
