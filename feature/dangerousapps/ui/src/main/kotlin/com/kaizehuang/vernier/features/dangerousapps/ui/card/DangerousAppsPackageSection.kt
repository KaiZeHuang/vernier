package com.kaizehuang.vernier.features.dangerousapps.ui.card

import com.kaizehuang.vernier.features.dangerousapps.ui.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.AdaptiveShapeTokens
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.ShapeTokens
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.dangerousapps.presentation.model.DangerousAppsCardModel
import com.kaizehuang.vernier.features.dangerousapps.presentation.model.DangerousAppsPackageItemModel
import io.github.xiaotong6666.uihelper.adaptive.adaptiveValue

@Composable
internal fun DangerousAppsPackageSection(
    model: DangerousAppsCardModel,
) {
    when {
        model.packageItems.isEmpty() -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .background(color = SurfaceTheme.palette.groupedInset, shape = AdaptiveShapeTokens.CornerLarge)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                WrapSafeText(
                    text = stringResource(R.string.dangerousapps_no_package_hits),
                    style = SurfaceTypography.CalloutEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                WrapSafeText(
                    text = model.summary,
                    style = SurfaceTypography.Footnote,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                model.packageItems.forEachIndexed { index, item ->
                    DangerousAppsPackageRow(item = item)
                    if (index < model.packageItems.lastIndex) {
                        DetectorHairline()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DangerousAppsPackageRow(
    item: DangerousAppsPackageItemModel,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        WrapSafeText(
            text = item.appName,
            style = SurfaceTypography.CalloutEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
        )
        WrapSafeText(
            text = item.packageName,
            style = SurfaceTypography.Footnote.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item.methods.forEach { method ->
                DangerousAppsMethodChip(label = method)
            }
        }
    }
}

@Composable
internal fun DangerousAppsMethodChip(
    label: String,
    warningTone: Boolean = false,
) {
    val critical = SurfaceTheme.palette.critical
    val containerColor = adaptiveValue(
        material = if (warningTone) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        miuix = if (warningTone) critical.copy(alpha = 0.12f) else SurfaceTheme.palette.groupedInset,
    )
    val contentColor = adaptiveValue(
        material = if (warningTone) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
        miuix = MaterialTheme.colorScheme.onSurface,
    )
    val iconTint = adaptiveValue(
        material = if (warningTone) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary,
        miuix = if (warningTone) critical else MaterialTheme.colorScheme.primary,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .background(
                color = containerColor,
                shape = AdaptiveShapeTokens.CornerMedium,
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SurfaceIcon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(13.dp),
        )
        WrapSafeText(
            text = label,
            style = SurfaceTypography.Caption.copy(fontFamily = FontFamily.Monospace),
            color = contentColor,
        )
    }
}
