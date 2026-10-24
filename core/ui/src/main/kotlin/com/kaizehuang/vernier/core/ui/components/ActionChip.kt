package com.kaizehuang.vernier.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.AdaptiveShapeTokens
import com.kaizehuang.vernier.core.ui.model.ActionItemModel

@Composable
public fun ActionChip(
    action: ActionItemModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(min = 96.dp, max = 220.dp)
            .background(color = SurfaceTheme.palette.groupedInset, shape = AdaptiveShapeTokens.CornerMedium)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        WrapSafeText(
            text = action.label,
            style = SurfaceTypography.CalloutEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
        )
        action.counter?.let { counter ->
            WrapSafeText(
                text = counter,
                style = SurfaceTypography.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
