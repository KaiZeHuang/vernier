package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import com.kaizehuang.vernier.core.ui.R as CoreUiR
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.core.ui.copyPlainTextToClipboard
import com.kaizehuang.vernier.core.ui.presentation.formatBuildTimeUtc
import com.kaizehuang.vernier.features.settings.ui.R
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveExpandIcon
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveSectionGroup

@Composable
internal fun VersionItem(
    versionName: String,
    versionCode: Int,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    shapes: ListItemShapes,
) {
    val toggleLabel = stringResource(
        if (expanded) CoreUiR.string.card_collapse else CoreUiR.string.card_expand,
    )
    val toggle = { onExpandedChange(!expanded) }
    SettingsItem(
        headline = stringResource(R.string.about_label_version),
        shapes = shapes,
        // SegmentedListItem takes no click label, so the row's click action is restated with one.
        modifier = Modifier.semantics {
            onClick(label = toggleLabel) {
                toggle()
                true
            }
        },
        onClick = toggle,
        leadingContent = { AboutLeadingIcon(icon = Icons.Rounded.Info) },
        supportingContent = {
            WrapSafeText(text = stringResource(R.string.about_value_version, versionName, versionCode))
        },
        trailingContent = {
            AdaptiveExpandIcon(
                expanded = expanded,
                contentDescription = toggleLabel,
            )
        },
    )
}

/** The build time, build hash and copy rows, at [firstIndex] of a group of [count] rows. */
@Composable
internal fun BuildDetailItems(
    versionName: String,
    versionCode: Int,
    buildTimeUtc: String,
    buildHash: String,
    firstIndex: Int,
    count: Int,
) {
    val context = LocalContext.current
    val buildTime = formatBuildTimeUtc(buildTimeUtc)
    val clipboardLabel = stringResource(R.string.about_clipboard_label)
    val clipboardText = listOf(
        stringResource(R.string.about_clipboard_version_line, versionName, versionCode),
        stringResource(R.string.about_clipboard_build_time_line, buildTime),
        stringResource(R.string.about_clipboard_build_hash_line, buildHash),
    ).joinToString(separator = "\n")
    val copyConfirmation = stringResource(R.string.about_copy_toast)

    AdaptiveSectionGroup {
        item {
            SettingsItem(
                headline = stringResource(R.string.about_label_build_time),
                shapes = settingsItemShapes(index = firstIndex, count = count),
                leadingContent = { AboutLeadingIcon(icon = Icons.Rounded.Schedule) },
                supportingContent = {
                    WrapSafeText(text = stringResource(R.string.about_value_build_time, buildTime))
                },
            )
        }
        item {
            SettingsItem(
                headline = stringResource(R.string.about_label_build_hash),
                shapes = settingsItemShapes(index = firstIndex + 1, count = count),
                leadingContent = { AboutLeadingIcon(icon = Icons.Rounded.Tag) },
                supportingContent = {
                    WrapSafeText(
                        text = buildHash,
                        style = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                    )
                },
            )
        }
        item {
            SettingsItem(
                headline = stringResource(R.string.about_label_copy_build_info),
                shapes = settingsItemShapes(index = firstIndex + 2, count = count),
                onClick = {
                    copyPlainTextToClipboard(context, clipboardLabel, clipboardText, copyConfirmation)
                },
                leadingContent = { AboutLeadingIcon(icon = Icons.Rounded.ContentCopy) },
                supportingContent = {
                    WrapSafeText(text = stringResource(R.string.about_copy_build_info_summary))
                },
            )
        }
    }
}
