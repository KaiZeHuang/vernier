package com.kaizehuang.vernier.features.dangerousapps.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VisibilityOff
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.AdaptiveShapeTokens
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.dangerousapps.presentation.model.DangerousAppsHiddenPackageItemModel
import com.kaizehuang.vernier.features.dangerousapps.presentation.model.DangerousAppsHmaAlertModel

@Composable
internal fun DangerousAppsHmaSection(
    alert: DangerousAppsHmaAlertModel,
) {
    val critical = SurfaceTheme.palette.critical
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = critical.copy(alpha = 0.08f), shape = AdaptiveShapeTokens.CornerLarge)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = critical.copy(alpha = 0.14f), shape = AdaptiveShapeTokens.CornerMedium),
                contentAlignment = Alignment.Center,
            ) {
                SurfaceIcon(
                    imageVector = Icons.Rounded.VisibilityOff,
                    contentDescription = null,
                    tint = critical,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                WrapSafeText(
                    text = alert.title,
                    style = SurfaceTypography.Headline,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                WrapSafeText(
                    text = alert.summary,
                    style = SurfaceTypography.Footnote,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            alert.hiddenPackages.forEachIndexed { index, item ->
                DangerousAppsHiddenPackageRow(item = item)
                if (index < alert.hiddenPackages.lastIndex) {
                    DetectorHairline()
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DangerousAppsHiddenPackageRow(
    item: DangerousAppsHiddenPackageItemModel,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
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
                DangerousAppsMethodChip(
                    label = method,
                    warningTone = true,
                )
            }
        }
    }
}
