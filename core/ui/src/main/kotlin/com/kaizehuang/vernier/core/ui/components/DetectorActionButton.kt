package com.kaizehuang.vernier.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.components.SurfaceButtonDefaults
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveButton
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveIcon

/**
 * An action at the foot of a detector card. The [prominent] one is washed in the accent color;
 * the others sit on the card's inset fill, so a row of them reads as one quiet group.
 */
@Composable
public fun DetectorActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    prominent: Boolean = false,
    singleLineLabel: Boolean = false,
) {
    val content: @Composable RowScope.() -> Unit = {
        AdaptiveIcon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        WrapSafeText(
            text = label,
            style = SurfaceTypography.ActionLabel,
            maxLines = if (singleLineLabel) 1 else Int.MAX_VALUE,
            overflow = if (singleLineLabel) TextOverflow.Ellipsis else TextOverflow.Clip,
        )
    }
    AdaptiveContent(
        material = {
            if (prominent) {
                Button(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    colors = SurfaceButtonDefaults.filledColors(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    content = content,
                )
            } else {
                FilledTonalButton(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    content = content,
                )
            }
        },
        miuix = {
            AdaptiveButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                prominent = prominent,
                materialContentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                content = content,
            )
        },
    )
}
