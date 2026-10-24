package com.kaizehuang.vernier.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import io.github.xiaotong6666.uihelper.adaptive.adaptiveInsetSurfaceColor
import com.kaizehuang.vernier.core.ui.model.HighlightItemModel
import com.kaizehuang.vernier.core.ui.presentation.rememberStatusAppearance
import io.github.xiaotong6666.uihelper.adaptive.adaptiveSurfaceBackground
import io.github.xiaotong6666.uihelper.adaptive.adaptiveValue

@Composable
public fun HighlightRow(
    item: HighlightItemModel,
    modifier: Modifier = Modifier,
) {
    val appearance = rememberStatusAppearance(item.status)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .adaptiveSurfaceBackground(
                materialColor = SurfaceTheme.palette.groupedInset,
                materialShape = MaterialTheme.shapes.large,
                miuixColor = adaptiveInsetSurfaceColor(),
                miuixCornerRadius = 16.dp,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AdaptiveIcon(
            imageVector = appearance.icon,
            contentDescription = null,
            tint = appearance.iconTint,
            modifier = Modifier
                .padding(top = 1.dp)
                .size(18.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            WrapSafeText(
                text = item.title,
                modifier = Modifier.fillMaxWidth(),
                style = adaptiveValue(
                    material = SurfaceTypography.CalloutEmphasized,
                    miuix = SurfaceTypography.PanelTitle,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            WrapSafeText(
                text = item.detail,
                modifier = Modifier.fillMaxWidth(),
                style = SurfaceTypography.PanelSupporting,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            CompactStatusBadge(status = item.status, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
