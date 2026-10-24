package com.kaizehuang.vernier.features.lsposed.data.probes

import android.content.Context
import com.kaizehuang.vernier.capability.packageinventory.data.AndroidInstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledApplicationQueryOptions
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedPackageVisibility
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedProbe
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignal
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignalGroup
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignalSeverity

data class LSPosedPackageProbeResult(
    val signals: List<LSPosedSignal>,
    val managerPackageCount: Int,
    val moduleAppCount: Int,
    val packageVisibility: LSPosedPackageVisibility,
)

class LSPosedPackageProbe(
    private val inventoryReaderFactory: (Context) -> InstalledPackageInventoryReader = { context ->
        AndroidInstalledPackageInventoryReader(
            context = context,
            queryOptions = InstalledApplicationQueryOptions(
                includeMetadata = true,
                resolveLabelsForMetadataKeys = XPOSED_METADATA_KEYS.toSet(),
            ),
        )
    },
) {

    fun run(context: Context): LSPosedPackageProbeResult {
        return evaluate(inventoryReaderFactory(context.applicationContext).read())
    }

    internal fun evaluate(
        inventoryResult: InstalledPackageInventoryResult,
    ): LSPosedPackageProbeResult {
        val inventory = (inventoryResult as? InstalledPackageInventoryResult.Available)?.inventory
        val installedApps = inventory?.applications.orEmpty()
        val packageVisibility = when (inventory?.visibility ?: InstalledPackageVisibility.UNKNOWN) {
            InstalledPackageVisibility.FULL -> LSPosedPackageVisibility.FULL
            InstalledPackageVisibility.RESTRICTED -> LSPosedPackageVisibility.RESTRICTED
            InstalledPackageVisibility.UNKNOWN -> LSPosedPackageVisibility.UNKNOWN
        }

        val signals = mutableListOf<LSPosedSignal>()
        val installedByPackage = installedApps.associateBy { it.packageName }
        KNOWN_PACKAGES.forEach { (packageName, label) ->
            if (packageName !in installedByPackage) {
                return@forEach
            }
            signals += LSPosedSignal(
                id = "pkg_${packageName.replace('.', '_')}",
                probe = LSPosedProbe.PACKAGE,
                label = label,
                value = "Installed",
                group = LSPosedSignalGroup.PACKAGES,
                severity = LSPosedSignalSeverity.WARNING,
                detail = packageName,
                detailMonospace = true,
            )
        }

        val moduleSignals = installedApps.mapNotNull { appInfo ->
            val keys = XPOSED_METADATA_KEYS.filter { it in appInfo.metadataKeys }
            if (keys.isEmpty()) {
                return@mapNotNull null
            }
            LSPosedSignal(
                id = "module_${appInfo.packageName.replace('.', '_')}",
                probe = LSPosedProbe.PACKAGE,
                label = appInfo.label,
                value = "Module",
                group = LSPosedSignalGroup.PACKAGES,
                severity = LSPosedSignalSeverity.WARNING,
                detail = buildString {
                    appendLine(appInfo.packageName)
                    append("Meta-data: ")
                    append(keys.joinToString())
                },
                detailMonospace = true,
            )
        }
        signals += moduleSignals

        return LSPosedPackageProbeResult(
            signals = signals,
            managerPackageCount = KNOWN_PACKAGES.count { (packageName, _) -> packageName in installedByPackage },
            moduleAppCount = moduleSignals.size,
            packageVisibility = packageVisibility,
        )
    }

    private companion object {
        private val KNOWN_PACKAGES = linkedMapOf(
            "org.lsposed.manager" to "LSPosed Manager",
            "org.lsposed.manager.debug" to "LSPosed Debug",
            "moe.matsuri.lsposed" to "LSPosed Fork",
            "org.lsposed.lspatch" to "LSPatch",
            "de.robv.android.xposed.installer" to "Xposed Installer",
            "org.meowcat.edxposed.manager" to "EdXposed Manager",
            "com.solohsu.android.edxp.manager" to "EdXposed Manager (old)",
            "me.weishu.exp" to "TaiChi/Exposed",
            "io.va.exposed" to "VirtualXposed",
            "com.tsng.hidemyapplist" to "Hide My Applist",
            "eu.faircode.xlua" to "XLua",
        )

        private val XPOSED_METADATA_KEYS = listOf(
            "xposedmodule",
            "xposedminversion",
            "xposeddescription",
            "xposedscope",
            "xposedsharedprefs",
        )
    }
}
