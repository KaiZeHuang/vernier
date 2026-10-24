package com.kaizehuang.vernier.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaizehuang.vernier.R

@Composable
internal fun StartupBootstrapLoadingScreen(
    modifier: Modifier = Modifier,
) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) Color.Black else Color.White
    val foreground = if (dark) Color.White else Color.Black
    val secondary = foreground.copy(alpha = 0.62f)
    val rotation by rememberInfiniteTransition(label = "bootstrapSpinner").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "bootstrapSpinnerRotation",
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Canvas(
                modifier = Modifier
                    .size(36.dp)
                    .rotate(rotation),
            ) {
                drawArc(
                    color = foreground,
                    startAngle = -90f,
                    sweepAngle = 250f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                )
            }
            BasicText(
                text = stringResource(R.string.startup_preparing_title),
                style = TextStyle(
                    color = foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )
            BasicText(
                text = stringResource(R.string.startup_preparing_detail),
                style = TextStyle(
                    color = secondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}
