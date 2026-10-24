package com.kaizehuang.vernier.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.ui.presentation.rememberStatusAppearance
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveDetailValueRow

@Composable
public fun DetectorDetailRowBlock(
    label: String,
    value: String,
    status: DetectorStatus,
    modifier: Modifier = Modifier,
    valueModifier: Modifier = Modifier,
    detail: String? = null,
    detailMonospace: Boolean = false,
    statusIcon: ImageVector? = null,
    verticalPadding: Dp = 8.dp,
) {
    val appearance = rememberStatusAppearance(status)
    AdaptiveDetailValueRow(
        label = label,
        value = value,
        icon = statusIcon ?: appearance.icon,
        iconTint = appearance.iconTint,
        modifier = modifier,
        valueModifier = valueModifier,
        detail = detail,
        detailMonospace = detailMonospace,
        materialVerticalPadding = verticalPadding,
        materialLabelStyle = MaterialTheme.typography.labelMedium,
        materialValueStyle = MaterialTheme.typography.bodyMedium,
        materialDetailStyle = MaterialTheme.typography.bodySmall,
    )
}
