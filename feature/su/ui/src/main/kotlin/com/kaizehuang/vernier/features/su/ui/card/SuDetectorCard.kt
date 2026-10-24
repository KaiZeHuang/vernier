package com.kaizehuang.vernier.features.su.ui.card

import com.kaizehuang.vernier.features.su.ui.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CrisisAlert
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.ui.components.DetectorCardFrame
import com.kaizehuang.vernier.core.ui.components.DetectorDetailRowBlock
import com.kaizehuang.vernier.core.ui.components.DetectorFact
import com.kaizehuang.vernier.core.ui.components.DetectorFactPair
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.DetectorSectionFrame
import com.kaizehuang.vernier.core.ui.components.DetectorSectionGroup
import com.kaizehuang.vernier.core.ui.components.highestSectionSeverity
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.core.ui.presentation.rememberStatusAppearance
import com.kaizehuang.vernier.features.su.presentation.model.SuCardModel
import com.kaizehuang.vernier.features.su.presentation.model.SuDetailRowModel
import com.kaizehuang.vernier.features.su.presentation.model.SuHeaderFact
import com.kaizehuang.vernier.features.su.presentation.model.SuHeaderFactModel
import com.kaizehuang.vernier.features.su.presentation.model.SuImpactItemModel

@Composable
internal fun SuDetectorCard(
    model: SuCardModel,
    modifier: Modifier = Modifier,
) {
    DetectorCardFrame(
        title = model.title,
        subtitle = model.subtitle,
        status = model.status,
        verdict = model.verdict,
        summary = model.summary,
        leadingIcon = Icons.Rounded.AdminPanelSettings,
        miuixLeadingIcon = ImageVector.vectorResource(R.drawable.ic_kernelsu),
        modifier = modifier,
        headerFacts = {
            SuCollapsedOverview(model = model)
        },
    ) {
        DetectorSectionGroup {
            item(visible = model.artifactRows.isNotEmpty()) {
                SuDetailSection(
                title = stringResource(R.string.su_section_root_artifacts),
                icon = Icons.Rounded.Shield,
                rows = model.artifactRows,
                showDivider = model.contextRows.isNotEmpty() || model.impactItems.isNotEmpty() ||
                    model.methodRows.isNotEmpty() || model.scanRows.isNotEmpty(),
                )
            }

            item(visible = model.contextRows.isNotEmpty()) {
                SuDetailSection(
                title = stringResource(R.string.su_section_native_context),
                icon = Icons.Rounded.Policy,
                rows = model.contextRows,
                showDivider = model.impactItems.isNotEmpty() || model.methodRows.isNotEmpty() || model.scanRows.isNotEmpty(),
                )
            }

            item(visible = model.impactItems.isNotEmpty()) {
                SuImpactSection(
                title = stringResource(R.string.su_section_impact),
                icon = Icons.Rounded.CrisisAlert,
                items = model.impactItems,
                showDivider = model.methodRows.isNotEmpty() || model.scanRows.isNotEmpty(),
                )
            }

            item(visible = model.methodRows.isNotEmpty()) {
                SuDetailSection(
                title = stringResource(R.string.su_section_detection_methods),
                icon = Icons.Rounded.Search,
                rows = model.methodRows,
                showDivider = model.scanRows.isNotEmpty(),
                )
            }

            item(visible = model.scanRows.isNotEmpty()) {
                SuDetailSection(
                title = stringResource(R.string.su_section_scan_summary),
                icon = Icons.Rounded.Info,
                rows = model.scanRows,
                showDivider = false,
                )
            }
        }
    }
}

@Composable
private fun SuCollapsedOverview(
    model: SuCardModel,
) {
    val artifacts = model.headerFacts.firstOrNull { it.fact == SuHeaderFact.ARTIFACTS } ?: return
    val daemons = model.headerFacts.firstOrNull { it.fact == SuHeaderFact.DAEMONS } ?: return
    val context = model.headerFacts.firstOrNull { it.fact == SuHeaderFact.CONTEXT } ?: return
    val processes = model.headerFacts.firstOrNull { it.fact == SuHeaderFact.PROCESSES } ?: return

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        SuFactPairCard(
            primary = artifacts,
            secondary = daemons,
            modifier = Modifier.weight(1f),
        )
        SuFactPairCard(
            primary = context,
            secondary = processes,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SuFactPairCard(
    primary: SuHeaderFactModel,
    secondary: SuHeaderFactModel,
    modifier: Modifier = Modifier,
) {
    DetectorFactPair(
        primary = primary.asDetectorFact(),
        secondary = secondary.asDetectorFact(),
        modifier = modifier,
    )
}

private fun SuHeaderFactModel.asDetectorFact() =
    DetectorFact(label = label, value = value, status = status)

@Composable
private fun SuDetailSection(
    title: String,
    icon: ImageVector,
    rows: List<SuDetailRowModel>,
    showDivider: Boolean = true,
) {
    DetectorSectionFrame(
        title = title,
        icon = icon,
        severity = highestSectionSeverity(rows.map { it.status }),
        showDivider = showDivider,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            rows.forEachIndexed { index, row ->
                SuDetailRow(row = row)
                if (index < rows.lastIndex) {
                    DetectorHairline()
                }
            }
        }
    }
}

@Composable
private fun SuDetailRow(
    row: SuDetailRowModel,
) {
    DetectorDetailRowBlock(
        label = row.label,
        value = row.value,
        status = row.status,
        detail = row.detail,
        detailMonospace = row.detailMonospace,
    )
}

@Composable
private fun SuImpactSection(
    title: String,
    icon: ImageVector,
    items: List<SuImpactItemModel>,
    showDivider: Boolean = true,
) {
    DetectorSectionFrame(
        title = title,
        icon = icon,
        severity = highestSectionSeverity(items.map { it.status }),
        showDivider = showDivider,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                SuImpactRow(item = item)
            }
        }
    }
}

@Composable
private fun SuImpactRow(
    item: SuImpactItemModel,
) {
    val appearance = rememberStatusAppearance(item.status)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SurfaceIcon(
            imageVector = appearance.icon,
            contentDescription = null,
            tint = appearance.iconTint,
            modifier = Modifier.size(16.dp),
        )
        WrapSafeText(
            text = item.text,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
