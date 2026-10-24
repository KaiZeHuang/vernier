package com.kaizehuang.vernier.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The Vernier instrument mark: a rounded "instrument face" holding a vernier scale — a main scale
 * bar, three ticks (the centre one longer) and the bold accent slider. Drawn at any size; the
 * proportions come from the prototype's 34x34 viewBox.
 *
 * This glyph is the future app icon, the wordmark's companion and the memory hook of the brand.
 */
@Composable
public fun VernierScaleGlyph(
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
    color: Color = Color.Unspecified,
    sliderColor: Color = Color.Unspecified,
) {
    val ink = if (color == Color.Unspecified) Color(0xFF1A1A18) else color
    val slider = if (sliderColor == Color.Unspecified) Color(0xFFC24A00) else sliderColor
    Canvas(modifier = modifier.size(size)) {
        val unit = this.size.minDimension / 34f
        val stroke = 2f * unit
        drawRoundRect(
            color = ink,
            topLeft = Offset(1.6f * unit, 1.6f * unit),
            size = androidx.compose.ui.geometry.Size(30.8f * unit, 30.8f * unit),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(9f * unit, 9f * unit),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        val scaleY = 18f * unit
        val lineStroke = 1.5f * unit
        drawLine(
            color = ink,
            start = Offset(8f * unit, scaleY),
            end = Offset(26f * unit, scaleY),
            strokeWidth = lineStroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = ink,
            start = Offset(11f * unit, 13.5f * unit),
            end = Offset(11f * unit, scaleY),
            strokeWidth = lineStroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = ink,
            start = Offset(17f * unit, 12f * unit),
            end = Offset(17f * unit, scaleY),
            strokeWidth = lineStroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = ink,
            start = Offset(23f * unit, 13.5f * unit),
            end = Offset(23f * unit, scaleY),
            strokeWidth = lineStroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = slider,
            start = Offset(17f * unit, 7f * unit),
            end = Offset(17f * unit, 26f * unit),
            strokeWidth = 3f * unit,
            cap = StrokeCap.Round,
        )
    }
}
