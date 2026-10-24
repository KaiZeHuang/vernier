package com.kaizehuang.vernier.startup.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.ContinuousCornerShape
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.ShapeTokens
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val SectionIconShape = ContinuousCornerShape(10.dp)

@Composable
internal fun AgreementSection(
    icon: ImageVector,
    title: String,
    content: String,
    tone: AgreementSectionTone = AgreementSectionTone.Standard,
) {
    AdaptiveContent(
        miuix = {
            MiuixCard(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                BasicComponent(
                    title = title,
                    startAction = {
                        SurfaceIcon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp).size(20.dp),
                            tint = MiuixTheme.colorScheme.onSurface,
                        )
                    },
                    modifier = Modifier.semantics { heading() },
                )
                AgreementSectionContent(
                    content = content,
                    tone = tone,
                    miuix = true,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                )
            }
        },
        material = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SurfaceTheme.palette.groupedSurface,
                        shape = MaterialTheme.shapes.large,
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color = SurfaceTheme.palette.groupedInset, shape = MaterialTheme.shapes.medium),
                        contentAlignment = Alignment.Center,
                    ) {
                        SurfaceIcon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Text(
                        text = title,
                        modifier = Modifier
                            .weight(1f)
                            .semantics { heading() },
                        style = SurfaceTypography.Title3,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                AgreementSectionContent(content = content, tone = tone)
            }
        },
    )
}

@Composable
private fun AgreementSectionContent(
    content: String,
    tone: AgreementSectionTone,
    modifier: Modifier = Modifier,
    miuix: Boolean = false,
) {
    val lines = remember(content) { content.lines() }
    val firstContentIndex = remember(lines) { lines.indexOfFirst { it.isNotBlank() } }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        lines.forEachIndexed { index, rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                val lineStyle = when {
                    index == firstContentIndex && tone == AgreementSectionTone.Warning ->
                        AgreementLineStyle.Callout

                    NumberedHeadingRegex.matches(line) ->
                        AgreementLineStyle.SectionHeading

                    else -> AgreementLineStyle.Body
                }
                if (miuix) {
                    AgreementStyledLineMiuix(
                        text = line,
                        lineStyle = lineStyle,
                        tone = tone,
                    )
                } else {
                    AgreementStyledLine(
                        text = line,
                        lineStyle = lineStyle,
                        tone = tone,
                    )
                }
            }
        }
    }
}

@Composable
private fun AgreementStyledLineMiuix(
    text: String,
    lineStyle: AgreementLineStyle,
    tone: AgreementSectionTone,
) {
    val color = when (lineStyle) {
        AgreementLineStyle.Callout,
        AgreementLineStyle.SectionHeading -> MiuixTheme.colorScheme.onSurface
        AgreementLineStyle.Body -> when (tone) {
            AgreementSectionTone.Warning -> MiuixTheme.colorScheme.onSurface
            AgreementSectionTone.Notice,
            AgreementSectionTone.Standard -> MiuixTheme.colorScheme.onSurfaceVariantSummary
        }
    }
    val style = when (lineStyle) {
        AgreementLineStyle.Callout,
        AgreementLineStyle.SectionHeading -> MiuixTheme.textStyles.headline1
        AgreementLineStyle.Body -> MiuixTheme.textStyles.paragraph
    }
    MiuixText(text = text, style = style, color = color)
}

@Composable
private fun AgreementStyledLine(
    text: String,
    lineStyle: AgreementLineStyle,
    tone: AgreementSectionTone,
) {
    // The warning's body keeps the full text color; weight, not color, sets headings apart.
    val bodyColor = when (tone) {
        AgreementSectionTone.Warning -> MaterialTheme.colorScheme.onSurface
        AgreementSectionTone.Notice -> MaterialTheme.colorScheme.onSurfaceVariant
        AgreementSectionTone.Standard -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val (style, color) = when (lineStyle) {
        AgreementLineStyle.Callout,
        AgreementLineStyle.SectionHeading -> SurfaceTypography.Headline to MaterialTheme.colorScheme.onSurface
        AgreementLineStyle.Body -> SurfaceTypography.Callout to bodyColor
    }

    Text(
        text = text,
        style = style,
        color = color,
        lineHeight = style.lineHeight * 1.25,
    )
}

private val NumberedHeadingRegex = Regex("""^\d+\.\s.*""")

internal enum class AgreementSectionTone {
    Standard,
    Warning,
    Notice,
}

private enum class AgreementLineStyle {
    Callout,
    SectionHeading,
    Body,
}
