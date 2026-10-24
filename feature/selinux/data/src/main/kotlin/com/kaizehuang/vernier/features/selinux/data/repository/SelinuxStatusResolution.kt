package com.kaizehuang.vernier.features.selinux.data.repository

import com.kaizehuang.vernier.features.selinux.domain.SelinuxCheckResult
import com.kaizehuang.vernier.features.selinux.domain.SelinuxMode

internal fun determineStatusWithParadoxLogic(
    results: List<SelinuxCheckResult>,
): StatusResolution {
    val filesystemActive = results.any {
        it.method == METHOD_FILESYSTEM && (it.status == FILESYSTEM_ACTIVE || it.status == FILESYSTEM_MOUNTED)
    }

    results.forEach { result ->
        if (result.status == SELINUX_PERMISSIVE) {
            return StatusResolution(
                SelinuxMode.PERMISSIVE,
                SELINUX_PERMISSIVE,
                paradoxDetected = false
            )
        }
        if (result.status == SELINUX_DISABLED || result.status == FILESYSTEM_NOT_MOUNTED) {
            return StatusResolution(
                SelinuxMode.DISABLED,
                SELINUX_DISABLED,
                paradoxDetected = false
            )
        }
    }

    if (results.any { it.readsEnforcing }) {
        return StatusResolution(
            SelinuxMode.ENFORCING,
            SELINUX_ENFORCING,
            paradoxDetected = false
        )
    }

    // Only a denied read of the enforce node proves enforcing mode: every UID may read it (S_IRUGO
    // in security/selinux/selinuxfs.c), and in permissive mode avc_denied() grants instead of
    // returning -EACCES (security/selinux/avc.c).
    val enforceReadDenied = results.any { it.permissionDenied && it.method in ENFORCE_NODE_METHODS }
    if (enforceReadDenied && filesystemActive) {
        return StatusResolution(
            SelinuxMode.ENFORCING,
            "Enforcing (paradox)",
            paradoxDetected = true
        )
    }

    return StatusResolution(SelinuxMode.UNKNOWN, "Unknown", paradoxDetected = false)
}

internal data class StatusResolution(
    val mode: SelinuxMode,
    val label: String,
    val paradoxDetected: Boolean,
)
