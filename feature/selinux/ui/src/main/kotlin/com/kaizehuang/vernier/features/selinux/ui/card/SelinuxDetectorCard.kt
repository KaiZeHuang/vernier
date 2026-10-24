package com.kaizehuang.vernier.features.selinux.ui.card

import com.kaizehuang.vernier.features.selinux.ui.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CrisisAlert
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.ui.components.DetectorCardFrame
import com.kaizehuang.vernier.core.ui.components.DetectorDetailRowBlock
import com.kaizehuang.vernier.core.ui.components.DetectorFact
import com.kaizehuang.vernier.core.ui.components.DetectorFactPair
import com.kaizehuang.vernier.core.ui.components.DetectorHairline
import com.kaizehuang.vernier.core.ui.components.DetectorSectionFrame
import com.kaizehuang.vernier.core.ui.components.DetectorSectionGroup
import com.kaizehuang.vernier.core.ui.components.highestSectionSeverity
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.core.ui.presentation.rememberStatusAppearance
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxCardModel
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxDetailRowModel
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxHeaderFact
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxHeaderFactModel
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxImpactItemModel
@Composable
internal fun SelinuxDetectorCard(
    model: SelinuxCardModel,
    modifier: Modifier = Modifier,
) {
    DetectorCardFrame(
        title = model.title,
        subtitle = model.subtitle,
        status = model.status,
        verdict = model.verdict,
        summary = model.summary,
        leadingIcon = Icons.Rounded.Security,
        modifier = modifier,
        headerFacts = {
            SelinuxCollapsedOverview(model = model)
        },
    ) {
        DetectorSectionGroup {
            item(visible = model.stateRows.isNotEmpty()) {
                SelinuxDetailSection(
                title = stringResource(R.string.selinux_section_security_state),
                icon = Icons.Rounded.AdminPanelSettings,
                rows = model.stateRows,
                showDivider = model.impactItems.isNotEmpty() || model.methodRows.isNotEmpty() ||
                    model.policyRows.isNotEmpty() || model.policyNotes.isNotEmpty() ||
                    model.auditRows.isNotEmpty() || model.auditNotes.isNotEmpty() ||
                    model.deviceRows.isNotEmpty() || model.references.isNotEmpty(),
                )
            }

            item(visible = model.impactItems.isNotEmpty()) {
                SelinuxImpactSection(
                title = stringResource(R.string.selinux_section_impact),
                icon = Icons.Rounded.CrisisAlert,
                items = model.impactItems,
                showDivider = model.methodRows.isNotEmpty() || model.policyRows.isNotEmpty() ||
                    model.policyNotes.isNotEmpty() || model.auditRows.isNotEmpty() || model.auditNotes.isNotEmpty() ||
                    model.deviceRows.isNotEmpty() || model.references.isNotEmpty(),
                )
            }

            item(visible = model.methodRows.isNotEmpty()) {
                SelinuxDetailSection(
                title = stringResource(R.string.selinux_section_detection_methods),
                icon = Icons.Rounded.Search,
                rows = model.methodRows,
                showDivider = model.policyRows.isNotEmpty() || model.policyNotes.isNotEmpty() ||
                    model.auditRows.isNotEmpty() || model.auditNotes.isNotEmpty() ||
                    model.deviceRows.isNotEmpty() || model.references.isNotEmpty(),
                )
            }

            item(visible = model.policyRows.isNotEmpty() || model.policyNotes.isNotEmpty()) {
                DetectorSectionFrame(
                    title = stringResource(R.string.selinux_section_policy_analysis),
                    icon = Icons.Rounded.Policy,
                    severity = highestSectionSeverity(
                        model.policyRows.map { it.status } + model.policyNotes.map { it.status },
                    ),
                    showDivider = model.auditRows.isNotEmpty() || model.auditNotes.isNotEmpty() ||
                        model.deviceRows.isNotEmpty() || model.references.isNotEmpty(),
                ) {
                    if (model.policyRows.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            model.policyRows.forEachIndexed { index, row ->
                                SelinuxDetailRow(row = row)
                                if (index < model.policyRows.lastIndex) {
                                    DetectorHairline()
                                }
                            }
                        }
                    }

                    if (model.policyNotes.isNotEmpty()) {
                        if (model.policyRows.isNotEmpty()) {
                            DetectorHairline()
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            model.policyNotes.forEach { note ->
                                SelinuxImpactRow(item = note)
                            }
                        }
                    }
                }
            }

            item(visible = model.auditRows.isNotEmpty() || model.auditNotes.isNotEmpty()) {
                DetectorSectionFrame(
                    title = stringResource(R.string.selinux_section_audit_integrity),
                    icon = Icons.AutoMirrored.Rounded.FactCheck,
                    severity = highestSectionSeverity(
                        model.auditRows.map { it.status } + model.auditNotes.map { it.status },
                    ),
                    showDivider = model.deviceRows.isNotEmpty() || model.references.isNotEmpty(),
                ) {
                    if (model.auditRows.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            model.auditRows.forEachIndexed { index, row ->
                                SelinuxDetailRow(row = row)
                                if (index < model.auditRows.lastIndex) {
                                    DetectorHairline()
                                }
                            }
                        }
                    }

                    if (model.auditNotes.isNotEmpty()) {
                        if (model.auditRows.isNotEmpty()) {
                            DetectorHairline()
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            model.auditNotes.forEach { note ->
                                SelinuxImpactRow(item = note)
                            }
                        }
                    }
                }
            }

            item(visible = model.deviceRows.isNotEmpty()) {
                SelinuxDetailSection(
                title = stringResource(R.string.selinux_section_device_info),
                icon = Icons.Rounded.Info,
                rows = model.deviceRows,
                showDivider = model.references.isNotEmpty(),
                )
            }

            item(visible = model.references.isNotEmpty()) {
                DetectorSectionFrame(
                    title = stringResource(R.string.selinux_section_reference),
                    icon = Icons.AutoMirrored.Rounded.MenuBook,
                    showDivider = false,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        model.references.forEach { reference ->
                            WrapSafeText(
                                text = reference,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelinuxCollapsedOverview(
    model: SelinuxCardModel,
) {
    val mode = model.headerFacts.firstOrNull { it.fact == SelinuxHeaderFact.MODE } ?: return
    val policy = model.headerFacts.firstOrNull { it.fact == SelinuxHeaderFact.POLICY } ?: return
    val audit = model.headerFacts.firstOrNull { it.fact == SelinuxHeaderFact.AUDIT } ?: return
    val context = model.headerFacts.firstOrNull { it.fact == SelinuxHeaderFact.CONTEXT } ?: return

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        SelinuxFactPairCard(
            primary = mode,
            secondary = policy,
            modifier = Modifier.weight(1f),
        )
        SelinuxFactPairCard(
            primary = audit,
            secondary = context,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SelinuxFactPairCard(
    primary: SelinuxHeaderFactModel,
    secondary: SelinuxHeaderFactModel,
    modifier: Modifier = Modifier,
) {
    DetectorFactPair(
        primary = primary.asDetectorFact(),
        secondary = secondary.asDetectorFact(),
        modifier = modifier,
    )
}

private fun SelinuxHeaderFactModel.asDetectorFact() =
    DetectorFact(label = label, value = value, status = status)

@Composable
private fun SelinuxDetailSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    rows: List<SelinuxDetailRowModel>,
    showDivider: Boolean = true,
) {
    DetectorSectionFrame(
        title = title,
        icon = icon,
        severity = highestSectionSeverity(rows.map { it.status }),
        showDivider = showDivider,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            rows.forEachIndexed { index, row ->
                SelinuxDetailRow(row = row)
                if (index < rows.lastIndex) {
                    DetectorHairline()
                }
            }
        }
    }
}

@Composable
private fun SelinuxDetailRow(
    row: SelinuxDetailRowModel,
) {
    DetectorDetailRowBlock(
        label = row.label,
        value = row.value,
        status = row.status,
        detail = row.detail,
    )
}

@Composable
private fun SelinuxImpactSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<SelinuxImpactItemModel>,
    showDivider: Boolean = true,
) {
    DetectorSectionFrame(
        title = title,
        icon = icon,
        severity = highestSectionSeverity(items.map { it.status }),
        showDivider = showDivider,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                SelinuxImpactRow(item = item)
            }
        }
    }
}

@Composable
private fun SelinuxImpactRow(
    item: SelinuxImpactItemModel,
) {
    val appearance = rememberStatusAppearance(item.status)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SurfaceIcon(
            imageVector = appearance.icon,
            contentDescription = null,
            tint = appearance.iconTint,
            modifier = Modifier.size(16.dp),
        )
        WrapSafeText(
            text = item.text,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
