package com.kaizehuang.vernier.features.settings.ui.licenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.ShapeTokens
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveClickableCard
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveLabelChip
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveSurfaceTone
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveText
import io.github.xiaotong6666.uihelper.adaptive.adaptiveBodyStyle
import io.github.xiaotong6666.uihelper.adaptive.adaptiveFootnoteStyle
import io.github.xiaotong6666.uihelper.adaptive.adaptiveHeadlineStyle
import io.github.xiaotong6666.uihelper.adaptive.adaptiveOnSurfaceColor
import io.github.xiaotong6666.uihelper.adaptive.adaptiveSecondaryTextColor
import io.github.xiaotong6666.uihelper.adaptive.adaptiveSurfaceColor
import io.github.xiaotong6666.uihelper.adaptive.adaptiveValue
import io.github.xiaotong6666.uihelper.common.StatusTag
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.util.author

private const val DESCRIPTION_MAX_LINES = 2

@Composable
internal fun LazyItemScope.LicenseLibraryRow(
    library: Library,
    onClick: () -> Unit,
) {
    AdaptiveClickableCard(
        onClick = onClick,
        modifier = adaptiveValue(
            material = Modifier.animateItem().padding(vertical = 4.dp).fillMaxWidth(),
            miuix = Modifier.animateItem().fillMaxWidth(),
        ),
        materialShape = MaterialTheme.shapes.large,
        materialContainerColor = SurfaceTheme.palette.groupedSurface,
        materialElevated = true,
        materialContentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        miuixCornerRadius = 16.dp,
        miuixContentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        miuixPressTransformEnabled = false,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(adaptiveValue(material = 12.dp, miuix = 10.dp)),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(adaptiveValue(material = 2.dp, miuix = 3.dp)),
                ) {
                    // A library's name is a proper name: it may wrap between words but is never hyphenated.
                    AdaptiveText(
                        text = library.name,
                        style = adaptiveHeadlineStyle(SurfaceTypography.Headline).copy(hyphens = Hyphens.None),
                        color = adaptiveOnSurfaceColor(),
                    )
                    library.author
                        .takeIf { it.isNotBlank() }
                        ?.let { author ->
                            AdaptiveText(
                                text = author,
                                style = adaptiveFootnoteStyle(SurfaceTypography.Footnote),
                                color = adaptiveSecondaryTextColor(),
                            )
                        }
                }

                library.artifactVersion
                    ?.takeIf { it.isNotBlank() }
                    ?.let { version -> LicensePill(text = version, version = true) }
            }

            library.description
                ?.takeIf { it.isNotBlank() }
                ?.let { description ->
                    AdaptiveText(
                        text = description,
                        style = adaptiveBodyStyle(SurfaceTypography.Footnote),
                        color = adaptiveSecondaryTextColor(),
                        maxLines = DESCRIPTION_MAX_LINES,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

            if (library.licenses.isNotEmpty()) {
                FlowRow(
                    modifier = adaptiveValue(
                        material = Modifier.padding(top = 2.dp),
                        miuix = Modifier,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    library.licenses.forEach { license ->
                        LicensePill(text = license.name)
                    }
                }
            }
        }
    }
}

/** A short label, such as a version or a license name, in a capsule on the inset fill. */
@Composable
internal fun LicensePill(
    text: String,
    modifier: Modifier = Modifier,
    version: Boolean = false,
) {
    if (version) {
        AdaptiveContent(
            miuix = {
                // Match Vernier's High/Warning status tag, rather than MIUIX Badge's oval shape.
                StatusTag(
                    label = text,
                    modifier = modifier,
                    backgroundColor = adaptiveSurfaceColor(AdaptiveSurfaceTone.Highest),
                    contentColor = adaptiveOnSurfaceColor(),
                )
            },
            material = {
                AdaptiveLabelChip(
                    label = text,
                    modifier = modifier,
                    materialShape = ShapeTokens.CornerFull,
                    materialContainerColor = SurfaceTheme.palette.groupedInset,
                    materialTextStyle = SurfaceTypography.Caption,
                )
            },
        )
    } else {
        AdaptiveLabelChip(
            label = text,
            modifier = modifier,
            materialShape = ShapeTokens.CornerFull,
            materialContainerColor = SurfaceTheme.palette.groupedInset,
            materialTextStyle = SurfaceTypography.Caption,
        )
    }
}
