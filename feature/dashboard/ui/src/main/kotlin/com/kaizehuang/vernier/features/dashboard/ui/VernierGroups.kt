package com.kaizehuang.vernier.features.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.VernierStatus
import com.kaizehuang.vernier.core.designsystem.theme.VernierTheme
import com.kaizehuang.vernier.core.designsystem.theme.VernierTypography
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.ui.components.WrapSafeText

/**
 * The five measurement groups of the Vernier inspector.
 *
 * Eighteen checks are too many to read as one list, so they are grouped by what they measure:
 * how the device booted and what it attests, what runs in this process, what the filesystem
 * shows, what the policy and property layers claim, and the environment around all of it.
 */
internal enum class VernierGroup {
    BOOT_TRUST,
    RUNTIME_HOOKS,
    MOUNTS_FILES,
    POLICY_PROPERTIES,
    ENVIRONMENT,
}

private val GROUP_MEMBERS: Map<VernierGroup, Set<String>> = mapOf(
    VernierGroup.BOOT_TRUST to setOf("bootloader", "tee", "play_integrity_fix"),
    VernierGroup.RUNTIME_HOOKS to setOf("zygisk", "lsposed", "memory", "heap_residue"),
    VernierGroup.MOUNTS_FILES to setOf("mount", "su", "native_root", "root_managers"),
    VernierGroup.POLICY_PROPERTIES to setOf("selinux", "system_properties", "custom_rom"),
    VernierGroup.ENVIRONMENT to setOf(
        "adb_runtime",
        "dangerous_apps",
        "kernel_check",
        "virtualization",
    ),
)

/** Detectors that no group claims still belong on the page; they land in the environment group. */
internal fun DetectorId.measurementGroup(): VernierGroup =
    GROUP_MEMBERS.entries.firstOrNull { (_, members) -> value in members }?.key
        ?: VernierGroup.ENVIRONMENT

/** Groups in page order. */
internal val vernierGroupOrder: List<VernierGroup> = VernierGroup.entries.toList()

@Composable
internal fun vernierGroupName(group: VernierGroup): String = when (group) {
    VernierGroup.BOOT_TRUST -> stringResource(R.string.verdict_group_boot_trust)
    VernierGroup.RUNTIME_HOOKS -> stringResource(R.string.verdict_group_runtime_hooks)
    VernierGroup.MOUNTS_FILES -> stringResource(R.string.verdict_group_mounts_files)
    VernierGroup.POLICY_PROPERTIES -> stringResource(R.string.verdict_group_policy_properties)
    VernierGroup.ENVIRONMENT -> stringResource(R.string.verdict_group_environment)
}

/**
 * A group row: the group name, then the row end carries the count and the status word — the
 * reading rule is "the word says the status, the row end says how many".
 */
@Composable
internal fun VernierGroupRow(
    group: VernierGroup,
    status: VernierStatus,
    checkedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    val palette = VernierTheme.palette
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 10.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        WrapSafeText(
            text = vernierGroupName(group),
            style = VernierTypography.groupName,
            color = palette.ink,
            modifier = Modifier.weight(1f),
        )
        WrapSafeText(
            text = "$checkedCount/$totalCount",
            style = VernierTypography.count,
            color = palette.ink3,
        )
        GroupLamp(status = status)
    }
}

@Composable
private fun GroupLamp(status: VernierStatus) {
    val palette = VernierTheme.palette
    val color = palette.statusColor(status)
    androidx.compose.foundation.Canvas(modifier = Modifier.size(7.dp)) {
        drawCircle(color = color)
    }
}

internal fun DetectorStatus.toVernierStatus(): VernierStatus = when (severity) {
    DetectionSeverity.DANGER -> VernierStatus.DANGER
    DetectionSeverity.WARNING -> VernierStatus.WARNING
    DetectionSeverity.ALL_CLEAR -> VernierStatus.CLEAR
    DetectionSeverity.INFO -> VernierStatus.WARNING
}

/** The worst status of a group is the group's status: a lamp is never an average. */
internal fun worstStatus(statuses: List<VernierStatus>): VernierStatus {
    if (statuses.isEmpty()) return VernierStatus.NOT_CHECKED
    if (statuses.any { it == VernierStatus.DANGER }) return VernierStatus.DANGER
    if (statuses.any { it == VernierStatus.WARNING }) return VernierStatus.WARNING
    if (statuses.all { it == VernierStatus.CLEAR }) return VernierStatus.CLEAR
    return VernierStatus.NOT_CHECKED
}
