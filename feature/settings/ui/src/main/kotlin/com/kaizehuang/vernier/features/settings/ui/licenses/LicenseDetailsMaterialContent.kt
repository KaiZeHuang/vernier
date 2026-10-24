package com.kaizehuang.vernier.features.settings.ui.licenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Language
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.AdaptiveShapeTokens
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.core.ui.openExternalUri
import com.kaizehuang.vernier.features.settings.ui.R
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License

private val GroupInset = 16.dp

@Composable
internal fun LibraryProjectLink(library: Library) {
    val context = LocalContext.current
    val website = library.website?.takeIf { it.isNotBlank() }
    val url = website ?: library.scm?.url?.takeIf { it.isNotBlank() } ?: return
    LinkRow(
        icon = if (website != null) Icons.Rounded.Language else Icons.Rounded.Code,
        label = stringResource(
            if (website != null) R.string.licenses_dialog_home_page else R.string.licenses_dialog_source_repo,
        ),
        url = url,
        onClick = { openExternalUri(context, url) },
        modifier = Modifier
            .clip(AdaptiveShapeTokens.CornerLarge)
            .background(color = SurfaceTheme.palette.groupedInset),
    )
}

@Composable
internal fun LicenseTextGroup(license: License) {
    val context = LocalContext.current
    val url = license.url?.takeIf { it.isNotBlank() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AdaptiveShapeTokens.CornerLarge)
            .background(color = SurfaceTheme.palette.groupedInset),
    ) {
        if (url != null) {
            LinkRow(
                icon = Icons.Rounded.Gavel,
                label = license.name,
                url = url,
                onClick = { openExternalUri(context, url) },
            )
        } else {
            LicenseNameRow(name = license.name)
        }
        DetectorHairline(startInset = GroupInset)
        SelectionContainer {
            WrapSafeText(
                text = license.licenseContent?.trim()?.takeIf { it.isNotEmpty() }
                    ?: stringResource(R.string.licenses_dialog_no_license_text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GroupInset, vertical = 14.dp),
                style = SurfaceTypography.Footnote,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LinkRow(
    icon: ImageVector,
    label: String,
    url: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = GroupInset, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SurfaceIcon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            WrapSafeText(
                text = label,
                style = SurfaceTypography.CalloutEmphasized,
                color = MaterialTheme.colorScheme.onSurface,
            )
            WrapSafeText(
                text = url,
                style = SurfaceTypography.Footnote,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        SurfaceIcon(
            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
            contentDescription = stringResource(R.string.licenses_dialog_open),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun LicenseNameRow(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = GroupInset, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SurfaceIcon(
            imageVector = Icons.Rounded.Gavel,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        WrapSafeText(
            text = name,
            style = SurfaceTypography.CalloutEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
