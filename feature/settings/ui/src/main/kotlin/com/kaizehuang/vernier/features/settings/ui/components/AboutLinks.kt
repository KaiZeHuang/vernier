package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.ui.R as CoreUiR
import com.kaizehuang.vernier.core.ui.openExternalUri
import com.kaizehuang.vernier.features.settings.ui.R
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveFilledIconButton
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon

private const val ABOUT_WEBSITE = "github.com/KaiZeHuang"
private const val ABOUT_EMAIL = "161717826+KaiZeHuang@users.noreply.github.com"
private const val ABOUT_GITHUB_URL = "https://github.com/KaiZeHuang/vernier"

@Composable
internal fun AboutLinks(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    ) {
        AboutLinkButton(onClick = { openExternalUri(context, "https://$ABOUT_WEBSITE") }) {
            SurfaceIcon(
                imageVector = Icons.Rounded.Language,
                contentDescription = stringResource(R.string.about_label_website),
                tint = aboutMiuixIconColor(),
            )
        }
        AboutLinkButton(onClick = { openExternalUri(context, "mailto:$ABOUT_EMAIL") }) {
            SurfaceIcon(
                imageVector = Icons.Rounded.Email,
                contentDescription = stringResource(R.string.about_label_email),
                tint = aboutMiuixIconColor(),
            )
        }
        AboutLinkButton(onClick = { openExternalUri(context, ABOUT_GITHUB_URL) }) {
            SurfaceIcon(
                painter = painterResource(CoreUiR.drawable.ic_github),
                contentDescription = stringResource(CoreUiR.string.social_github),
                tint = aboutMiuixIconColor(),
            )
        }
    }
}

@Composable
private fun AboutLinkButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    AdaptiveFilledIconButton(
        onClick = onClick,
        content = content,
    )
}
