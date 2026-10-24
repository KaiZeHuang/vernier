package com.kaizehuang.vernier.features.adbruntime.ui.card

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
import com.kaizehuang.vernier.features.adbruntime.presentation.model.AdbRuntimeCardModel
import com.kaizehuang.vernier.features.adbruntime.presentation.model.AdbRuntimeDetailRowModel

@Composable
internal fun AdbRuntimeDetectorCard(
    model: AdbRuntimeCardModel,
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
        AdbRuntimeRowSection(
            title = "Inconsistencies",
            icon = Icons.Rounded.Policy,
            rows = model.signalRows,
            showDivider = model.scanRows.isNotEmpty(),
        )
        AdbRuntimeRowSection(title = "Sources", icon = Icons.Rounded.Info, rows = model.scanRows, showDivider = false)
    }
}

@Composable
private fun AdbRuntimeRowSection(
    title: String,
    icon: ImageVector,
    rows: List<AdbRuntimeDetailRowModel>,
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
