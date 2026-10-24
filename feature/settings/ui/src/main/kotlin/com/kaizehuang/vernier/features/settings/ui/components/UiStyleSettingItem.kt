package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.runtime.Composable
import io.github.xiaotong6666.uihelper.adaptive.NativeSettingsDropdownItem

/** Vernier supplies project tokens; uihelper owns the native dropdown widget family. */
@Composable
internal fun UiStyleSettingItem(
    title: String,
    description: String,
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    NativeSettingsDropdownItem(
        title = title,
        description = description,
        items = items,
        selectedIndex = selectedIndex,
        icon = Icons.Rounded.DisplaySettings,
        onItemSelected = onItemSelected,
        materialShapes = settingsItemShapes(index = 0, count = 1),
        materialColors = settingsItemColors(),
    )
}
