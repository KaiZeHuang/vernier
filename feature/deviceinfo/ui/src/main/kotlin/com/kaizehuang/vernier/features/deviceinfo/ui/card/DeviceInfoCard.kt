package com.kaizehuang.vernier.features.deviceinfo.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.SettingsEthernet
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.ui.components.DetectorCardFrame
import com.kaizehuang.vernier.core.ui.components.DetectorFact
import com.kaizehuang.vernier.core.ui.components.DetectorFactPair
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.DetectorSectionFrame
import com.kaizehuang.vernier.core.ui.components.DetectorSectionGroup
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoSectionKind
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoCardModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoHeaderFactModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoRowModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoSectionModel

@Composable
internal fun DeviceInfoCard(
    model: DeviceInfoCardModel,
    modifier: Modifier = Modifier,
) {
    DetectorCardFrame(
        title = model.title,
        subtitle = model.subtitle,
        status = model.status,
        verdict = model.verdict,
        summary = model.summary,
        leadingIcon = Icons.Rounded.PhoneAndroid,
        modifier = modifier,
        headerFacts = {
            DeviceInfoHeader(model.headerFacts)
        },
    ) {
        DetectorSectionGroup {
            model.sections.forEachIndexed { index, section ->
                item {
                    DeviceInfoSection(
                        model = section,
                        showDivider = index < model.sections.lastIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoHeader(
    facts: List<DeviceInfoHeaderFactModel>,
) {
    val brand = facts.getOrNull(0) ?: return
    val model = facts.getOrNull(1) ?: return
    val android = facts.getOrNull(2) ?: return
    val sdk = facts.getOrNull(3) ?: return

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        DeviceInfoFactCard(
            primary = brand,
            secondary = model,
            modifier = Modifier.weight(1f),
        )
        DeviceInfoFactCard(
            primary = android,
            secondary = sdk,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DeviceInfoFactCard(
    primary: DeviceInfoHeaderFactModel,
    secondary: DeviceInfoHeaderFactModel,
    modifier: Modifier = Modifier,
) {
    DetectorFactPair(
        primary = DetectorFact(label = primary.label, value = primary.value),
        secondary = DetectorFact(label = secondary.label, value = secondary.value),
        modifier = modifier,
    )
}

@Composable
private fun DeviceInfoSection(
    model: DeviceInfoSectionModel,
    showDivider: Boolean,
) {
    DetectorSectionFrame(
        title = model.title,
        icon = sectionIcon(model.kind),
        showDivider = showDivider,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            model.rows.forEachIndexed { index, row ->
                DeviceInfoRow(row)
                if (index < model.rows.lastIndex) {
                    DetectorHairline()
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoRow(
    row: DeviceInfoRowModel,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        WrapSafeText(
            text = row.label,
            modifier = Modifier.weight(0.34f),
            style = SurfaceTypography.Callout,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        WrapSafeText(
            text = row.value,
            modifier = Modifier.weight(0.66f),
            style = SurfaceTypography.CalloutEmphasized.copy(
                fontFamily = if (row.detailMonospace) FontFamily.Monospace else SurfaceTypography.Callout.fontFamily,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun sectionIcon(
    kind: DeviceInfoSectionKind?,
): ImageVector {
    return when (kind) {
        DeviceInfoSectionKind.IDENTITY -> Icons.Rounded.Badge
        DeviceInfoSectionKind.BUILD -> Icons.Rounded.Dns
        DeviceInfoSectionKind.ANDROID -> Icons.Rounded.Android
        DeviceInfoSectionKind.RUNTIME -> Icons.Rounded.Memory
        DeviceInfoSectionKind.CONTEXT -> Icons.Rounded.SettingsEthernet
        null -> Icons.Rounded.Info
    }
}
