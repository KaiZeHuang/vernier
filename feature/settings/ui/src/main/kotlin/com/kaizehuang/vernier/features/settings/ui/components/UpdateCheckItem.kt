package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.MotionTokens
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.settings.presentation.model.SettingsUpdateStatus
import com.kaizehuang.vernier.features.settings.ui.R
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveInfiniteProgressIndicator
import io.github.xiaotong6666.uihelper.adaptive.SettingsNavigationItem
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import io.github.xiaotong6666.uihelper.adaptive.adaptiveValue

private enum class UpdateTrailing { Recheck, Checking, Details }

private data class UpdateGlyph(val icon: ImageVector, val tint: Color)

/** A tap opens the waiting update when there is one and checks GitHub again otherwise. */
@Composable
internal fun UpdateCheckItem(
    status: SettingsUpdateStatus,
    shapes: ListItemShapes,
    onCheckForUpdates: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val available = status == SettingsUpdateStatus.AVAILABLE
    val glyph = when (status) {
        SettingsUpdateStatus.IDLE,
        SettingsUpdateStatus.CHECKING -> UpdateGlyph(Icons.Rounded.SystemUpdate, colorScheme.primary)
        SettingsUpdateStatus.CURRENT -> UpdateGlyph(Icons.Rounded.CheckCircle, colorScheme.primary)
        SettingsUpdateStatus.AVAILABLE -> UpdateGlyph(
            Icons.Rounded.NewReleases,
            adaptiveValue(material = colorScheme.onPrimaryContainer, miuix = colorScheme.primary),
        )
        SettingsUpdateStatus.FAILED -> UpdateGlyph(Icons.Rounded.ErrorOutline, colorScheme.error)
    }
    val trailing = when (status) {
        SettingsUpdateStatus.CHECKING -> UpdateTrailing.Checking
        SettingsUpdateStatus.AVAILABLE -> UpdateTrailing.Details
        SettingsUpdateStatus.IDLE,
        SettingsUpdateStatus.CURRENT,
        SettingsUpdateStatus.FAILED -> UpdateTrailing.Recheck
    }

    val title = stringResource(R.string.update_settings_label)
    val statusText = updateStatusText(status)
    AdaptiveContent(
        miuix = {
            if (available) {
                SettingsNavigationItem(
                    title = title,
                    description = statusText,
                    icon = Icons.Rounded.NewReleases,
                    onClick = onCheckForUpdates,
                )
            } else {
                UpdateCheckRow(
                    title = title,
                    statusText = statusText,
                    status = status,
                    shapes = shapes,
                    glyph = glyph,
                    trailing = trailing,
                    available = available,
                    onCheckForUpdates = onCheckForUpdates,
                )
            }
        },
        material = {
            UpdateCheckRow(
                title = title,
                statusText = statusText,
                status = status,
                shapes = shapes,
                glyph = glyph,
                trailing = trailing,
                available = available,
                onCheckForUpdates = onCheckForUpdates,
            )
        },
    )
}

@Composable
private fun UpdateCheckRow(
    title: String,
    statusText: String,
    status: SettingsUpdateStatus,
    shapes: ListItemShapes,
    glyph: UpdateGlyph,
    trailing: UpdateTrailing,
    available: Boolean,
    onCheckForUpdates: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    SettingsItem(
        headline = title,
        shapes = shapes,
        onClick = onCheckForUpdates,
        enabled = status != SettingsUpdateStatus.CHECKING,
        colors = if (available) {
            settingsItemColors(
                containerColor = colorScheme.primaryContainer,
                contentColor = colorScheme.onPrimaryContainer,
                supportingColor = colorScheme.onPrimaryContainer,
            )
        } else {
            settingsItemColors()
        },
        leadingContent = {
            SettingsIconTile {
                AnimatedContent(
                    targetState = glyph,
                    transitionSpec = { crossfade() },
                    label = "updateGlyph",
                ) { target ->
                    SurfaceIcon(
                        imageVector = target.icon,
                        contentDescription = null,
                        tint = adaptiveValue(material = target.tint, miuix = aboutMiuixIconColor()),
                    )
                }
            }
        },
        supportingContent = {
            AnimatedContent(
                targetState = status,
                transitionSpec = { crossfade() },
                label = "updateStatus",
            ) { target ->
                WrapSafeText(
                    text = if (target == status) statusText else updateStatusText(target),
                    color = if (target == SettingsUpdateStatus.FAILED) colorScheme.error else Color.Unspecified,
                )
            }
        },
        trailingContent = {
            AnimatedContent(
                targetState = trailing,
                transitionSpec = { crossfade() },
                label = "updateTrailing",
            ) { target ->
                when (target) {
                    UpdateTrailing.Checking -> {
                        AdaptiveInfiniteProgressIndicator(size = 22.dp, materialStrokeWidth = 2.5.dp)
                    }
                    UpdateTrailing.Details -> SurfaceIcon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                    )
                    UpdateTrailing.Recheck -> SurfaceIcon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                    )
                }
            }
        },
    )
}

@Composable
private fun updateStatusText(status: SettingsUpdateStatus): String = stringResource(
    when (status) {
        SettingsUpdateStatus.IDLE -> R.string.update_status_idle
        SettingsUpdateStatus.CHECKING -> R.string.update_status_checking
        SettingsUpdateStatus.CURRENT -> R.string.update_status_current
        SettingsUpdateStatus.AVAILABLE -> R.string.update_status_available
        SettingsUpdateStatus.FAILED -> R.string.update_status_failed
    },
)

private fun crossfade(): ContentTransform =
    fadeIn(MotionTokens.FadeInOut) togetherWith fadeOut(MotionTokens.FadeInOut)
