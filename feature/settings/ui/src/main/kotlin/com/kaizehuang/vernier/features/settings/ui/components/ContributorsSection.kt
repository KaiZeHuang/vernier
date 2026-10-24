package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.components.SurfacePanel
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.settings.ui.R
import io.github.xiaotong6666.uihelper.dialog.rememberRetainedDialogPayload
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

@Composable
internal fun ContributorsSection(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val miuix = LocalUiMode.current == UiMode.Miuix
    val contributorSnapshots = remember(context) { loadContributorSnapshots(context) }
    val authors = contributorSnapshots.map { snapshot ->
        AuthorProfile(
            login = snapshot.login,
            name = snapshot.name,
            profileUrl = snapshot.profileUrl,
            avatarAssetPath = snapshot.avatarAssetPath,
            contributionSummary = stringResource(summaryResIdForKey(snapshot.summaryKey)),
            contributions = snapshot.contributionKeys.mapNotNull(::authorContributionForKey),
        )
    }
    if (authors.isEmpty()) return

    var selectedLogin by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedProfile = authors.firstOrNull { it.login == selectedLogin }
    val retainedProfile = rememberRetainedDialogPayload(
        value = selectedProfile,
        retainDuringExit = miuix,
    )

    SettingsSection(
        title = stringResource(R.string.author_wall_title),
        badge = authors.size.toString(),
        modifier = modifier,
    ) {
        SurfacePanel(
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ContributorWallCanvas(
                authors = authors,
                onSelect = { selectedLogin = it.login },
            )
            WrapSafeText(
                text = stringResource(R.string.author_wall_hint),
                style = SurfaceTypography.Footnote,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    retainedProfile.value?.let { profile ->
        AuthorDetailsDialog(
            show = selectedLogin != null,
            profile = profile,
            onDismiss = { selectedLogin = null },
            onDismissFinished = retainedProfile.onDismissFinished,
        )
    }
}
