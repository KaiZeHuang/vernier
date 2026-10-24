package com.kaizehuang.vernier.features.dashboard.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.components.SurfacePanel
import com.kaizehuang.vernier.core.designsystem.components.VernierScaleGlyph
import com.kaizehuang.vernier.core.designsystem.theme.VernierStatus
import com.kaizehuang.vernier.core.designsystem.theme.VernierTheme
import com.kaizehuang.vernier.core.designsystem.theme.VernierTypography
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewMetric
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewModel
import com.kaizehuang.vernier.features.dashboard.presentation.model.OverviewVerdict

/**
 * The measurement readout of the Vernier instrument.
 *
 * Type first: the status word is the visual subject, the vernier scale carries the scan
 * progress, and the counter at the end carries the counts. There is no score — a status word,
 * a scale and a counter are the whole readout.
 */
@Composable
internal fun VernierVerdictHero(
    model: DashboardOverviewModel,
    modifier: Modifier = Modifier,
) {
    val palette = VernierTheme.palette
    val status = model.verdict.toVernierStatus()
    val measuring = model.verdict == OverviewVerdict.PENDING ||
        model.verdict == OverviewVerdict.READY
    val lamp = if (measuring) palette.accent else palette.statusColor(status)
    val ready = model.metrics
        .firstOrNull { it.metric == DashboardOverviewMetric.READY }
        ?.value?.trim()?.toIntOrNull() ?: 0
    val pending = model.metrics
        .firstOrNull { it.metric == DashboardOverviewMetric.PENDING }
        ?.value?.trim()?.toIntOrNull() ?: 0
    val total = (ready + pending).coerceAtLeast(1)
    val progress = (ready.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    SurfacePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VernierScaleGlyph(
                size = 18.dp,
                color = palette.ink,
                sliderColor = palette.accent,
            )
            WrapSafeText(
                text = stringResource(R.string.verdict_instrument_label),
                modifier = Modifier.weight(1f),
                style = VernierTypography.mono,
                color = palette.ink3,
            )
            StatusLamp(color = lamp, modifier = Modifier.size(8.dp))
        }

        WrapSafeText(
            text = verdictWord(status = status, measuring = measuring),
            style = VernierTypography.verdict,
            color = lamp,
        )

        VernierProgressScale(
            progress = progress,
            trackColor = palette.hairline,
            sliderColor = lamp,
            modifier = Modifier.fillMaxWidth(),
        )

        WrapSafeText(
            text = stringResource(R.string.verdict_progress, ready, total),
            style = VernierTypography.count,
            color = palette.ink2,
        )
    }
}

@Composable
private fun StatusLamp(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawCircle(color = color)
    }
}

/**
 * The vernier scale: a ruled track with measurement ticks and one thick slider whose position
 * is the scan progress. The word says the status, the scale says how far the scan got, and the
 * counter at the row end says how many modules are done.
 */
@Composable
private fun VernierProgressScale(
    progress: Float,
    trackColor: Color,
    sliderColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.height(18.dp)) {
        val y = size.height * 0.62f
        val thin = 1.5.dp.toPx()
        drawLine(
            color = trackColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = thin,
            cap = StrokeCap.Round,
        )
        // 11 major ticks; every fifth is twice as tall, like a real rule.
        val ticks = 11
        for (i in 0 until ticks) {
            val x = size.width * (i.toFloat() / (ticks - 1))
            val h = if (i % 5 == 0) 6.dp.toPx() else 3.dp.toPx()
            drawLine(
                color = trackColor,
                start = Offset(x, y),
                end = Offset(x, y - h),
                strokeWidth = thin,
                cap = StrokeCap.Butt,
            )
        }
        // The slider is the only thick mark on the scale.
        val sliderX = size.width * progress
        drawLine(
            color = sliderColor,
            start = Offset(sliderX, y - 11.dp.toPx()),
            end = Offset(sliderX, y + 2.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun verdictWord(status: VernierStatus, measuring: Boolean): String = when {
    measuring -> stringResource(R.string.verdict_measuring)
    status == VernierStatus.CLEAR -> stringResource(R.string.verdict_all_clear)
    status == VernierStatus.WARNING -> stringResource(R.string.verdict_warning)
    status == VernierStatus.DANGER -> stringResource(R.string.verdict_danger)
    else -> stringResource(R.string.verdict_not_checked)
}

private fun OverviewVerdict.toVernierStatus(): VernierStatus = when (this) {
    OverviewVerdict.DANGER -> VernierStatus.DANGER
    OverviewVerdict.WARNING,
    OverviewVerdict.INFO -> VernierStatus.WARNING

    OverviewVerdict.OK -> VernierStatus.CLEAR
    OverviewVerdict.READY,
    OverviewVerdict.PENDING -> VernierStatus.NOT_CHECKED
}
