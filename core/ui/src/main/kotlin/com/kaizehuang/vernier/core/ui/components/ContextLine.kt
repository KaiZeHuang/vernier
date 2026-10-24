package com.kaizehuang.vernier.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.AdaptiveShapeTokens
import com.kaizehuang.vernier.core.ui.model.ContextItemModel

@Composable
public fun ContextLine(
    item: ContextItemModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SurfaceTheme.palette.groupedInset, shape = AdaptiveShapeTokens.CornerMedium)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        WrapSafeText(
            text = item.label,
            modifier = Modifier.fillMaxWidth(),
            style = SurfaceTypography.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        WrapSafeText(
            text = item.value,
            modifier = Modifier.fillMaxWidth(),
            style = SurfaceTypography.Callout,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
