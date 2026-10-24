package com.kaizehuang.vernier.features.tee.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Details
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VerifiedUser
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.IntrinsicSize
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.AdaptiveShapeTokens
import com.kaizehuang.vernier.core.designsystem.theme.ShapeTokens
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.ui.components.DetectorActionButton
import com.kaizehuang.vernier.core.ui.components.DetectorCardFrame
import com.kaizehuang.vernier.core.ui.components.DetectorSectionGroup
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.core.ui.presentation.rememberStatusAppearance
import com.kaizehuang.vernier.features.tee.presentation.model.TeeCardModel
import com.kaizehuang.vernier.features.tee.presentation.model.TeeFooterActionId
import com.kaizehuang.vernier.features.tee.presentation.model.TeeFooterActionModel
import com.kaizehuang.vernier.features.tee.presentation.model.TeeHeaderFact
import com.kaizehuang.vernier.features.tee.ui.TeeCertificatesDialog
import com.kaizehuang.vernier.features.tee.ui.TeeDetailsDialog
import io.github.xiaotong6666.uihelper.adaptive.adaptiveValue

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TeeDetectorCard(
    model: TeeCardModel,
    showDetailsDialog: Boolean,
    showCertificatesDialog: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onFooterAction: (TeeFooterActionId) -> Unit,
    onDismissDetails: () -> Unit,
    onDismissCertificates: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Keep the dialog composables mounted while show changes to false. Native MIUIX
    // overlays need the same instance to finish their transition before releasing the host.
    TeeDetailsDialog(
        show = showDetailsDialog,
        exportText = model.exportText,
        certificateCount = model.certificateSummary.certificates.size,
        onDismiss = onDismissDetails,
    )

    TeeCertificatesDialog(
        show = showCertificatesDialog,
        label = model.certificateSummary.label,
        count = model.certificateSummary.count,
        certificates = model.certificateSummary.certificates,
        onDismiss = onDismissCertificates,
    )

    DetectorCardFrame(
        title = model.title,
        subtitle = model.subtitle,
        status = model.status,
        verdict = model.verdict,
        summary = model.summary,
        leadingIcon = Icons.Rounded.Fingerprint,
        modifier = modifier,
        expanded = model.isExpanded,
        onExpandedChange = onExpandedChange,
        headerFacts = {
            TeeCollapsedOverview(model = model)
        },
        footerActions = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TeeNetworkBanner(model = model)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    model.actions.forEach { action ->
                        TeeFooterButton(
                            action = action,
                            onClick = onFooterAction,
                            modifier = Modifier.weight(
                                when (action.id) {
                                    TeeFooterActionId.DETAILS -> 0.8f
                                    TeeFooterActionId.CERTIFICATES -> 1.2f
                                    TeeFooterActionId.RESCAN -> 1f
                                },
                            ),
                            singleLineLabel = true,
                        )
                    }
                }
            }
        },
    ) {
        if (model.highlightSignals.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(
                    horizontal = adaptiveValue(material = 0.dp, miuix = 12.dp),
                    vertical = 8.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                model.highlightSignals.forEach { signal ->
                    TeeHighlightPill(signal = signal)
                }
            }
        }

        DetectorSectionGroup {
            model.factGroups.forEachIndexed { index, group ->
                item {
                    TeeFactGroup(
                        group = group,
                        stateKey = "tee-fact-$index",
                        showDivider = index < model.factGroups.lastIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun TeeCollapsedOverview(
    model: TeeCardModel,
) {
    val verdict = model.headerFacts.firstOrNull { it.fact == TeeHeaderFact.VERDICT } ?: return
    val score = model.headerFacts.firstOrNull { it.fact == TeeHeaderFact.SCORE } ?: return
    val tier = model.headerFacts.firstOrNull { it.fact == TeeHeaderFact.TIER } ?: return
    val trust = model.headerFacts.firstOrNull { it.fact == TeeHeaderFact.TRUST } ?: return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        model.rkpBadgeLabel?.let { label ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TeeRkpBadge(label = label)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            TeeFactPairCard(
                primary = verdict,
                secondary = score,
                modifier = Modifier.weight(1f),
            )
            TeeFactPairCard(
                primary = tier,
                secondary = trust,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TeeRkpBadge(
    label: String,
    modifier: Modifier = Modifier,
) {
    val appearance = rememberStatusAppearance(DetectorStatus.allClear())
    Row(
        modifier = modifier
            .background(color = appearance.tintWash, shape = ShapeTokens.CornerFull)
            .padding(start = 8.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        SurfaceIcon(
            imageVector = Icons.Rounded.Verified,
            contentDescription = null,
            tint = appearance.iconTint,
            modifier = Modifier.size(14.dp),
        )
        WrapSafeText(
            text = label,
            style = SurfaceTypography.Caption,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TeeNetworkBanner(model: TeeCardModel) {
    val appearance = rememberStatusAppearance(model.networkState.status)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = SurfaceTheme.palette.groupedInset, shape = AdaptiveShapeTokens.CornerLarge)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SurfaceIcon(
            imageVector = appearance.icon,
            contentDescription = null,
            tint = appearance.iconTint,
            modifier = Modifier.size(20.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            WrapSafeText(
                text = model.networkState.label,
                style = SurfaceTypography.CalloutEmphasized,
                color = MaterialTheme.colorScheme.onSurface,
            )
            WrapSafeText(
                text = model.networkState.summary,
                style = SurfaceTypography.Footnote,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TeeFooterButton(
    action: TeeFooterActionModel,
    onClick: (TeeFooterActionId) -> Unit,
    modifier: Modifier = Modifier,
    singleLineLabel: Boolean = false,
) {
    DetectorActionButton(
        label = action.counter?.let { "${action.label} (${it})" } ?: action.label,
        icon = when (action.id) {
            TeeFooterActionId.DETAILS -> Icons.Rounded.Details
            TeeFooterActionId.CERTIFICATES -> Icons.Rounded.VerifiedUser
            TeeFooterActionId.RESCAN -> Icons.Rounded.Refresh
        },
        onClick = { onClick(action.id) },
        modifier = modifier,
        enabled = action.enabled,
        prominent = action.id == TeeFooterActionId.RESCAN,
        singleLineLabel = singleLineLabel,
    )
}
