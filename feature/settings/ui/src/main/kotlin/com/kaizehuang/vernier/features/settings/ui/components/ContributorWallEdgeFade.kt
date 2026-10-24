package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

internal fun Modifier.wallEdgeFade(): Modifier =
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithCache {
            val fade = 36.dp.toPx().coerceAtMost(size.minDimension / 4)
            fun stops(fraction: Float) = arrayOf(
                0f to Color.Transparent,
                fraction * 0.4f to Color.Black.copy(alpha = 0.35f),
                fraction to Color.Black,
                1f - fraction to Color.Black,
                1f - fraction * 0.4f to Color.Black.copy(alpha = 0.35f),
                1f to Color.Transparent,
            )
            val horizontal = Brush.horizontalGradient(*stops(fade / size.width))
            val vertical = Brush.verticalGradient(*stops(fade / size.height))
            onDrawWithContent {
                drawContent()
                drawRect(horizontal, blendMode = BlendMode.DstIn)
                drawRect(vertical, blendMode = BlendMode.DstIn)
            }
        }
