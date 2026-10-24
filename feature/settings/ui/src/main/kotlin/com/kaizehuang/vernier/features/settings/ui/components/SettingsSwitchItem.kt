package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.xiaotong6666.uihelper.adaptive.NativeSettingsToggleItem

@Composable
internal fun SettingsSwitchItem(
    headline: String,
    summary: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shapes = settingsItemShapes(index = 0, count = 1)
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        SettingsGroup {
            NativeSettingsToggleItem(
                checked = checked,
                title = headline,
                description = summary,
                icon = icon,
                onCheckedChange = onCheckedChange,
                materialShapes = shapes,
                materialColors = settingsItemColors(),
            )
        }
    }
}
