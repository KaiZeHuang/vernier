package com.kaizehuang.vernier.features.heapresidue.ui.card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.kaizehuang.vernier.core.ui.components.DetectorCardFrame
import com.kaizehuang.vernier.core.ui.components.DetectorDetailRowBlock
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.DetectorSectionFrame
import com.kaizehuang.vernier.core.ui.components.highestSectionSeverity
import com.kaizehuang.vernier.features.heapresidue.presentation.model.HeapResidueCardModel
import com.kaizehuang.vernier.features.heapresidue.presentation.model.HeapResidueDetailRowModel

@Composable
internal fun HeapResidueDetectorCard(
    model: HeapResidueCardModel,
    modifier: Modifier = Modifier,
) {
    DetectorCardFrame(
        title = model.title,
        subtitle = model.subtitle,
        status = model.status,
        verdict = model.verdict,
        summary = model.summary,
        leadingIcon = Icons.Rounded.Search,
        modifier = modifier,
    ) {
        HeapResidueRowSection(
            title = "Signals",
            icon = Icons.Rounded.Policy,
            rows = model.signalRows,
            showDivider = model.scanRows.isNotEmpty(),
        )
        HeapResidueRowSection(title = "Scan", icon = Icons.Rounded.Info, rows = model.scanRows, showDivider = false)
    }
}

@Composable
private fun HeapResidueRowSection(
    title: String,
    icon: ImageVector,
    rows: List<HeapResidueDetailRowModel>,
    showDivider: Boolean,
) {
    if (rows.isEmpty()) {
        return
    }
    DetectorSectionFrame(
        title = title,
        icon = icon,
        severity = highestSectionSeverity(rows.map { it.status }),
        showDivider = showDivider,
    ) {
        rows.forEachIndexed { index, row ->
            DetectorDetailRowBlock(
                label = row.label,
                value = row.value,
                status = row.status,
                detail = row.detail,
            )
            if (index < rows.lastIndex) {
                DetectorHairline()
            }
        }
    }
}
