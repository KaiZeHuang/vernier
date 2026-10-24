package com.kaizehuang.vernier.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.ContinuousCornerShape
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val HeroGlyphShape = ContinuousCornerShape(22.dp)

/**
 * The symbol that opens a first-run screen, drawn like the app's logo: a glyph in the text color on
 * a card-colored tile, so it inverts with the theme.
 */
@Composable
internal fun StartupHeroGlyph(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    AdaptiveContent(
        miuix = {
            Card(
                modifier = modifier.size(80.dp),
                insideMargin = PaddingValues(0.dp),
            ) {
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    SurfaceIcon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onSurface,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        },
        material = {
            Box(
                modifier = modifier
                    .size(80.dp)
                    .background(color = SurfaceTheme.palette.groupedSurface, shape = HeroGlyphShape),
                contentAlignment = Alignment.Center,
            ) {
                SurfaceIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(42.dp),
                )
            }
        },
    )
}
