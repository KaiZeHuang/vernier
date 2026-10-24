package com.kaizehuang.vernier.features.dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.components.SurfacePanel
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.ui.components.AdaptiveSeverityTag
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardFindingModel
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent

private val FindingInset = 16.dp
private const val FINDING_DETAIL_MAX_LINES = 3

@Composable
internal fun DashboardFindingsCard(
    findings: List<DashboardFindingModel>,
    onFindingClick: (DashboardFindingModel) -> Unit,
) {
    AdaptiveContent(
        material = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            ) {
                findings.forEachIndexed { index, finding ->
                    val baseShapes = ListItemDefaults.segmentedShapes(index, findings.size)
                    val shapes = if (findings.size == 1) {
                        baseShapes.copy(shape = MaterialTheme.shapes.large)
                    } else {
                        baseShapes
                    }
                    val colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceBright,
                        supportingContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 16.dp,
                    )
                    if (finding.detectorId != null) {
                        SegmentedListItem(
                            onClick = { onFindingClick(finding) },
                            modifier = Modifier.fillMaxWidth(),
                            shapes = shapes,
                            colors = colors,
                            contentPadding = contentPadding,
                            content = {
                                DashboardFindingContent(finding = finding)
                            },
                        )
                    } else {
                        SegmentedListItem(
                            modifier = Modifier.fillMaxWidth(),
                            shapes = shapes,
                            colors = colors,
                            contentPadding = contentPadding,
                            content = {
                                DashboardFindingContent(finding = finding)
                            },
                        )
                    }
                }
            }
        },
        miuix = {
            SurfacePanel {
                findings.forEachIndexed { index, finding ->
                    DashboardFindingRow(
                        finding = finding,
                        onClick = if (finding.detectorId != null) {
                            { onFindingClick(finding) }
                        } else {
                            null
                        },
                    )
                    if (index < findings.lastIndex) {
                        DetectorHairline(
                            modifier = Modifier.padding(end = FindingInset),
                            startInset = FindingInset,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun DashboardFindingRow(
    finding: DashboardFindingModel,
    onClick: (() -> Unit)?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = FindingInset, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DashboardFindingContent(finding = finding)
    }
}

@Composable
private fun DashboardFindingContent(
    finding: DashboardFindingModel,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            WrapSafeText(
                text = dashboardFindingTitle(finding),
                modifier = Modifier.weight(1f),
                style = SurfaceTypography.FindingEyebrow,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AdaptiveSeverityTag(
                status = finding.status,
                label = findingSeverityLabel(finding),
            )
        }
        WrapSafeText(
            text = dashboardFindingHeadline(finding),
            modifier = Modifier.fillMaxWidth(),
            style = SurfaceTypography.PanelTitle,
            color = MaterialTheme.colorScheme.onSurface,
        )
        // The full evidence stays in the detector's card; a finding only points to it.
        WrapSafeText(
            text = dashboardFindingDetail(finding),
            modifier = Modifier.fillMaxWidth(),
            style = SurfaceTypography.PanelSupporting,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = FINDING_DETAIL_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun findingSeverityLabel(
    finding: DashboardFindingModel,
): String {
    return when (finding.status.severity) {
        DetectionSeverity.DANGER -> stringResource(R.string.dashboard_severity_high)
        DetectionSeverity.WARNING -> stringResource(R.string.dashboard_severity_medium)
        DetectionSeverity.INFO -> stringResource(R.string.dashboard_severity_check)
        DetectionSeverity.ALL_CLEAR -> stringResource(R.string.dashboard_severity_clear)
    }
}
