package com.kaizehuang.vernier.features.settings.ui.licenses

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.ListItemShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.settings.ui.R
import com.kaizehuang.vernier.features.settings.ui.components.AboutLeadingIcon
import com.kaizehuang.vernier.features.settings.ui.components.SettingsItem
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import io.github.xiaotong6666.uihelper.adaptive.SettingsNavigationItem

@Composable
internal fun OpenSourceLicensesItem(
    shapes: ListItemShapes,
    onClick: () -> Unit,
) {
    val title = stringResource(R.string.licenses_entry_title)
    val description = stringResource(R.string.licenses_entry_subtitle)
    AdaptiveContent(
        miuix = {
            SettingsNavigationItem(
                title = title,
                description = description,
                icon = Icons.Rounded.Description,
                onClick = onClick,
            )
        },
        material = {
            SettingsItem(
                headline = title,
                shapes = shapes,
                onClick = onClick,
                leadingContent = { AboutLeadingIcon(icon = Icons.Rounded.Description) },
                supportingContent = { WrapSafeText(text = description) },
                trailingContent = {
                    SurfaceIcon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                    )
                },
            )
        },
    )
}
