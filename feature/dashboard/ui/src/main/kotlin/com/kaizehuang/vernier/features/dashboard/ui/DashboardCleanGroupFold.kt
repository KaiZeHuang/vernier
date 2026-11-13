package com.kaizehuang.vernier.features.dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.VernierStatus
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import io.github.xiaotong6666.uihelper.adaptive.adaptiveSecondaryTextColor

/** A group's finding state, kept free of detector handles so the fold can be unit tested. */
internal data class GroupFinding(
    val group: VernierGroup,
    val hasFinding: Boolean,
)

internal data class CleanGroupFold(
    val hiddenGroups: Set<VernierGroup>,
    val cleanGroupCount: Int,
)

/**
 * Hide groups whose checks all came back clear once a scan has finished.
 *
 * A running scan keeps every group visible so progress stays legible. Expanding
 * reveals the clean groups again, but [CleanGroupFold.cleanGroupCount] still
 * counts them so the row can offer "Hide" instead of disappearing.
 */
internal fun foldCleanGroups(
    findings: List<GroupFinding>,
    scanFinished: Boolean,
    showClean: Boolean,
): CleanGroupFold {
    val clean = findings.filter { !it.hasFinding }.map { it.group }.toSet()
    val hidden = if (scanFinished && !showClean) clean else emptySet()
    return CleanGroupFold(hiddenGroups = hidden, cleanGroupCount = clean.size)
}

internal data class FoldedSections(
    val visible: List<VernierSection>,
    val cleanGroupCount: Int,
)

/** Fold whole sections, not individual cards: a group with no finding contributes nothing. */
internal fun foldSections(
    sections: List<VernierSection>,
    scanFinished: Boolean,
    showClean: Boolean,
): FoldedSections {
    val fold = foldCleanGroups(
        findings = sections.map { section ->
            GroupFinding(
                group = section.group,
                hasFinding = section.status == VernierStatus.WARNING ||
                    section.status == VernierStatus.DANGER,
            )
        },
        scanFinished = scanFinished,
        showClean = showClean,
    )
    return FoldedSections(
        visible = sections.filter { it.group !in fold.hiddenGroups },
        cleanGroupCount = fold.cleanGroupCount,
    )
}

@Composable
internal fun CleanGroupsFoldRow(
    cleanGroupCount: Int,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(R.string.dashboard_clean_groups_folded, cleanGroupCount)
    val action = stringResource(
        if (expanded) R.string.dashboard_clean_groups_collapse
        else R.string.dashboard_clean_groups_expand,
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 12.dp),
    ) {
        WrapSafeText(
            text = "$label · $action",
            style = SurfaceTypography.LoadingSupporting,
            color = adaptiveSecondaryTextColor(),
        )
    }
}
