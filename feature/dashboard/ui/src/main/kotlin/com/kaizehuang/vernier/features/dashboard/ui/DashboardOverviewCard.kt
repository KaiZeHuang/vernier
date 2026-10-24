package com.kaizehuang.vernier.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.components.SurfacePanel
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.ui.LocalAppBuildInfo
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.core.ui.presentation.rememberStatusAppearance
import com.kaizehuang.vernier.core.ui.presentation.formatBuildTimeUtc
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewMetricModel
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewModel
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveStatusHeroCard
import io.github.xiaotong6666.uihelper.adaptive.StatusHeroTone
import io.github.xiaotong6666.uihelper.adaptive.adaptiveValue

@Composable
internal fun DashboardOverviewCard(
    model: DashboardOverviewModel,
    onExportReport: () -> Unit,
) {
    val appearance = rememberStatusAppearance(model.status)
    val buildInfo = LocalAppBuildInfo.current
    val versionLabel = stringResource(R.string.dashboard_version_label, buildInfo.versionName, buildInfo.versionCode)
    val buildTimeLabel = stringResource(R.string.dashboard_build_time_utc, formatBuildTimeUtc(buildInfo.buildTimeUtc))
    val tone = model.status.severity
    val heroTone = when (tone) {
        DetectionSeverity.DANGER -> StatusHeroTone.Danger
        DetectionSeverity.WARNING -> StatusHeroTone.Warning
        DetectionSeverity.ALL_CLEAR -> StatusHeroTone.Success
        DetectionSeverity.INFO -> StatusHeroTone.Neutral
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AdaptiveStatusHeroCard(
            title = dashboardOverviewHeadline(model),
            summary = dashboardOverviewSummary(model),
            icon = when (tone) {
                DetectionSeverity.DANGER -> Icons.Rounded.ErrorOutline
                DetectionSeverity.WARNING -> Icons.Rounded.WarningAmber
                DetectionSeverity.ALL_CLEAR -> Icons.Rounded.CheckCircleOutline
                DetectionSeverity.INFO -> Icons.Rounded.Info
            },
            tone = heroTone,
            accentColor = appearance.iconTint,
            miuixMetaContentEndPadding = 16.dp,
            onClick = onExportReport,
            metaContent = { contentColor ->
                ReportHeroMetadata(
                    versionLabel = versionLabel,
                    buildTimeLabel = buildTimeLabel,
                    contentColor = contentColor,
                )
            },
            actionContent = { contentColor ->
                ReportHeroAction(contentColor = contentColor)
            },
        )
        SurfacePanel(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OverviewTitle(model = model)
            DetectorHairline()
            OverviewMetrics(metrics = model.metrics)
        }
    }
}

@Composable
private fun ReportHeroMetadata(
    versionLabel: String,
    buildTimeLabel: String,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        ReportMetaLine(icon = Icons.Rounded.Badge, label = versionLabel, color = contentColor.copy(alpha = 0.82f))
        ReportMetaLine(icon = Icons.Rounded.Schedule, label = buildTimeLabel, color = contentColor.copy(alpha = 0.72f))
    }
}

@Composable
private fun ReportHeroAction(contentColor: Color) {
    val endDividerPadding = adaptiveValue(material = 16.dp, miuix = 108.dp)
    val endContentPadding = adaptiveValue(material = 16.dp, miuix = 106.dp)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .drawBehind {
                    val startX = 20.dp.toPx()
                    val endX = size.width - endDividerPadding.toPx()
                    drawLine(
                        color = contentColor.copy(alpha = 0.16f),
                        start = Offset(startX, 0f),
                        end = Offset(endX.coerceAtLeast(startX), 0f),
                        strokeWidth = 1f,
                    )
                }
                .padding(
                    start = 16.dp,
                    end = endContentPadding,
                    top = 8.dp,
                    bottom = 8.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SurfaceIcon(Icons.Rounded.FileDownload, contentDescription = null, modifier = Modifier.size(19.dp), tint = contentColor)
            WrapSafeText(
                text = stringResource(R.string.dashboard_export_report_hint),
                modifier = Modifier.weight(1f),
                style = SurfaceTypography.ReportAction,
                color = contentColor,
            )
            AdaptiveContent(
                material = {
                    SurfaceIcon(
                        Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = contentColor.copy(alpha = 0.78f),
                    )
                },
                miuix = {},
            )
        }
    }
}

@Composable
private fun ReportMetaLine(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SurfaceIcon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = color)
        WrapSafeText(
            text = label,
            modifier = Modifier.weight(1f),
            style = SurfaceTypography.ReportMeta,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** The title is one line, or the scan's completion time over its duration. */
@Composable
private fun OverviewTitle(model: DashboardOverviewModel) {
    val lines = dashboardOverviewTitle(model).lines().filter { it.isNotBlank() }
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (model.showTitleIcon) {
            SurfaceIcon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(14.dp),
            )
        }
        WrapSafeText(
            text = lines.joinToString(separator = " · "),
            style = SurfaceTypography.Footnote,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun OverviewMetrics(
    metrics: List<DashboardOverviewMetricModel>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        metrics.forEach { metric ->
            OverviewMetric(metric = metric, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun OverviewMetric(
    metric: DashboardOverviewMetricModel,
    modifier: Modifier = Modifier,
) {
    val appearance = rememberStatusAppearance(metric.status)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        WrapSafeText(
            text = metric.value,
            style = SurfaceTypography.MetricNumeral,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(color = appearance.iconTint, shape = CircleShape),
            )
            WrapSafeText(
                text = dashboardMetricLabel(metric.metric),
                style = SurfaceTypography.PanelCaption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
