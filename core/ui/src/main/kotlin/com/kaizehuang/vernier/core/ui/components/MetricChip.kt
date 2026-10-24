package com.kaizehuang.vernier.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.AdaptiveShapeTokens
import com.kaizehuang.vernier.core.ui.model.MetricChipModel
import com.kaizehuang.vernier.core.ui.presentation.rememberStatusAppearance

@Composable
public fun MetricChip(
    chip: MetricChipModel,
    modifier: Modifier = Modifier,
) {
    val appearance = rememberStatusAppearance(chip.status)

    Column(
        modifier = modifier
            .widthIn(min = 124.dp, max = 220.dp)
            .background(color = SurfaceTheme.palette.groupedInset, shape = AdaptiveShapeTokens.CornerLarge)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            AdaptiveIcon(
                imageVector = appearance.icon,
                contentDescription = null,
                tint = appearance.iconTint,
                modifier = Modifier.size(13.dp),
            )
            WrapSafeText(
                text = chip.label,
                style = SurfaceTypography.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        WrapSafeText(
            text = chip.value,
            style = SurfaceTypography.Headline,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
