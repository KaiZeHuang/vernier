package com.kaizehuang.vernier.features.nativeroot.data.probes

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo

/** Public synchronous API on the reviewed legacy permission backend, with one bounded private name. */
internal class SrcuPermissionStimulus(context: Context) {
    private val manager = context.packageManager
    private val tree = "${context.packageName}.vernier.srcu"
    private val name = "$tree.sample"
    private var mark = false

    fun verifyUnusedName() {
        // Permission trees are not returned by getPermissionInfo. addPermission enforces tree
        // ownership in system_server; check only that our reserved sample name is unused here.
        // Refuse to overwrite even a same-package statically declared permission.
        try {
            manager.getPermissionInfo(name, 0)
            error("The experiment permission already exists; cleanup must finish before retrying")
        } catch (_: PackageManager.NameNotFoundException) { }
    }

    fun apply() {
        mark = !mark
        val label = if (mark) "SRCU timing A" else "SRCU timing B"
        val info = PermissionInfo().apply {
            this.name = this@SrcuPermissionStimulus.name
            nonLocalizedLabel = label
            protectionLevel = PermissionInfo.PROTECTION_SIGNATURE
        }
        // false means an existing permission was changed, not that persistence failed.
        manager.addPermission(info)
        check(manager.getPermissionInfo(name, 0).nonLocalizedLabel?.toString() == label) {
            "Dynamic permission readback disagreed"
        }
    }

    fun cleanup() {
        manager.removePermission(name)
        try {
            manager.getPermissionInfo(name, 0)
            error("Dynamic permission remained after cleanup")
        } catch (_: PackageManager.NameNotFoundException) { }
    }
}
