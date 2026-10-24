package com.kaizehuang.vernier.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveInteractiveCardSurface

/** The dashboard and detail cards share one surface without changing their content or state. */
@Composable
public fun SurfacePanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    onClick: (() -> Unit)? = null,
    materialShape: Shape = MaterialTheme.shapes.large,
    content: @Composable ColumnScope.() -> Unit,
) {
    AdaptiveInteractiveCardSurface(
        modifier = modifier,
        onClick = onClick,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        materialShape = materialShape,
        materialContainerColor = SurfaceTheme.palette.groupedSurface,
        content = content,
    )
}
