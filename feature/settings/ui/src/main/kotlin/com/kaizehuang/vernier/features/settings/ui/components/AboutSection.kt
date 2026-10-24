package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kaizehuang.vernier.features.settings.presentation.model.SettingsUiState
import com.kaizehuang.vernier.features.settings.ui.R
import com.kaizehuang.vernier.features.settings.ui.licenses.OpenSourceLicensesItem
import io.github.xiaotong6666.uihelper.adaptive.ExpandableSectionBody

// Update, version and licenses rows, with the three build detail rows between version and
// licenses while they are shown.
private const val CollapsedItemCount = 3
private const val ExpandedItemCount = 6
private const val FirstBuildDetailIndex = 2

@Composable
internal fun AboutSection(
    uiState: SettingsUiState,
    onCheckForUpdates: () -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBuildDetails by rememberSaveable { mutableStateOf(false) }
    val itemCount = if (showBuildDetails) ExpandedItemCount else CollapsedItemCount

    SettingsSection(title = stringResource(R.string.about_title), modifier = modifier) {
        SettingsGroup {
            UpdateCheckItem(
                status = uiState.updateStatus,
                shapes = settingsItemShapes(index = 0, count = itemCount),
                onCheckForUpdates = onCheckForUpdates,
            )
            VersionItem(
                versionName = uiState.versionName,
                versionCode = uiState.versionCode,
                expanded = showBuildDetails,
                onExpandedChange = { showBuildDetails = it },
                shapes = settingsItemShapes(index = 1, count = itemCount),
            )
            ExpandableSectionBody(expanded = showBuildDetails) {
                // Shaped against the expanded group, so the rows keep inner corners while they collapse.
                BuildDetailItems(
                    versionName = uiState.versionName,
                    versionCode = uiState.versionCode,
                    buildTimeUtc = uiState.buildTimeUtc,
                    buildHash = uiState.buildHash,
                    firstIndex = FirstBuildDetailIndex,
                    count = ExpandedItemCount,
                )
            }
            OpenSourceLicensesItem(
                shapes = settingsItemShapes(index = itemCount - 1, count = itemCount),
                onClick = onOpenLicenses,
            )
        }
        SettingsFootnote(
            title = stringResource(R.string.about_label_privacy),
            text = stringResource(R.string.about_privacy_summary),
            icon = Icons.Rounded.PrivacyTip,
        )
        AboutLinks()
    }
}
