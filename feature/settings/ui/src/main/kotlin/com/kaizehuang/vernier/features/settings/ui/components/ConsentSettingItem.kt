package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kaizehuang.vernier.features.settings.ui.ConsentToggle

@Composable
internal fun ConsentSettingItem(
    toggle: ConsentToggle,
    modifier: Modifier = Modifier,
) {
    SettingsSwitchItem(
        headline = stringResource(toggle.setting.title),
        summary = stringResource(toggle.setting.summary),
        icon = toggle.setting.icon,
        checked = toggle.checked,
        onCheckedChange = toggle.onCheckedChange,
        modifier = modifier,
    )
}
