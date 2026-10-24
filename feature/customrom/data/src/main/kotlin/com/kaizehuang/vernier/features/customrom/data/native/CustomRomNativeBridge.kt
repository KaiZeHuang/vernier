package com.kaizehuang.vernier.features.customrom.data.native

import com.kaizehuang.vernier.core.native.NativePayloadCodec
import com.kaizehuang.vernier.core.native.NativePayloadContract
import com.kaizehuang.vernier.core.native.NativeSnapshotCollector
import com.kaizehuang.vernier.features.customrom.domain.CustomRomFinding
import com.kaizehuang.vernier.features.customrom.domain.CustomRomModificationFinding

class CustomRomNativeBridge(
    private val collector: NativeSnapshotCollector = NativeSnapshotCollector.Default,
) {

    fun collectSnapshot(): CustomRomNativeSnapshot = collector.collect(
        readPayload = ::nativeCollectSnapshot,
        parse = ::parse,
        unavailable = { status -> CustomRomNativeSnapshot(collection = status) },
    )

    internal fun parse(raw: String): CustomRomNativeSnapshot {
        if (raw.isBlank()) {
            return CustomRomNativeSnapshot()
        }

        NativePayloadContract.requireKeys(raw, "AVAILABLE")

        var available = false
        var propertyAreaAvailable = false
        var propertyAreaContextCount = 0
        var propertyAreaAnomalyCount = 0
        var propertyAreaItemCount = 0
        var symbolScanAvailable = false
        val platformFiles = mutableListOf<CustomRomFinding>()
        val modificationFindings = mutableListOf<CustomRomModificationFinding>()
        val resourceInjectionFindings = mutableListOf<CustomRomFinding>()
        val recoveryScripts = mutableListOf<String>()
        val policyFindings = mutableListOf<CustomRomFinding>()
        val overlayFindings = mutableListOf<CustomRomFinding>()
        val symbolFindings = mutableListOf<CustomRomFinding>()

        raw.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.contains('=') }
            .forEach { line ->
                val key = line.substringBefore('=')
                val value = line.substringAfter('=')
                when (key) {
                    "AVAILABLE" -> available = NativePayloadCodec.decodeFlag(value)
                    "PROPAREA_AVAILABLE" -> propertyAreaAvailable = NativePayloadCodec.decodeFlag(value)
                    "PROPAREA_CONTEXTS" -> propertyAreaContextCount = value.toIntOrNull() ?: 0
                    "PROPAREA_AREA_ANOMALIES",
                    "PROPAREA_AREA_COUNT" -> propertyAreaAnomalyCount = value.toIntOrNull() ?: 0

                    "PROPAREA_ITEM_ANOMALIES",
                    "PROPAREA_ITEM_COUNT" -> propertyAreaItemCount = value.toIntOrNull() ?: 0
                    "SYMBOL_AVAILABLE" -> symbolScanAvailable = NativePayloadCodec.decodeFlag(value)
                    "PLATFORM" -> parseFinding(value)?.let(platformFiles::add)
                    "MODIFICATION" -> parseModificationFinding(value)?.let(modificationFindings::add)
                    "MAP" -> parseMapFinding(value)?.let(resourceInjectionFindings::add)
                    "SCRIPT" -> if (value.isNotBlank()) recoveryScripts += value
                    "POLICY" -> parsePolicyFinding(value)?.let(policyFindings::add)
                    "OVERLAY" -> parseFinding(value)?.let(overlayFindings::add)
                    "SYMBOL" -> parseSymbolFinding(value)?.let(symbolFindings::add)
                }
            }

        return CustomRomNativeSnapshot(
            available = available,
            propertyAreaAvailable = propertyAreaAvailable,
            propertyAreaContextCount = propertyAreaContextCount,
            propertyAreaAnomalyCount = propertyAreaAnomalyCount,
            propertyAreaItemCount = propertyAreaItemCount,
            symbolScanAvailable = symbolScanAvailable,
            platformFiles = platformFiles,
            modificationFindings = modificationFindings,
            resourceInjectionFindings = resourceInjectionFindings,
            recoveryScripts = recoveryScripts,
            policyFindings = policyFindings,
            overlayFindings = overlayFindings,
            symbolFindings = symbolFindings,
        )
    }

    private fun parseFinding(raw: String): CustomRomFinding? {
        val parts = raw.split('|')
        if (parts.size < 2) {
            return null
        }
        return CustomRomFinding(
            romName = parts[0],
            signal = parts[1].substringAfterLast('/'),
            detail = parts[1],
        )
    }

    private fun parsePolicyFinding(raw: String): CustomRomFinding? {
        val parts = raw.split('|')
        if (parts.size < 3) {
            return null
        }
        if (parts.size >= 4) {
            return CustomRomFinding(
                romName = parts[0],
                signal = parts[2],
                detail = "${parts[1]} (${parts[3]} hits)",
            )
        }
        return CustomRomFinding(
            romName = parts[0],
            signal = parts[1].substringAfterLast('/'),
            detail = "${parts[1]} (${parts[2]} hits)",
        )
    }

    private fun parseSymbolFinding(raw: String): CustomRomFinding? {
        val parts = raw.split('|', limit = 3)
        if (parts.size < 3) {
            return null
        }
        return CustomRomFinding(
            romName = parts[0],
            signal = parts[1],
            detail = parts[2],
        )
    }

    private fun parseModificationFinding(raw: String): CustomRomModificationFinding? {
        val parts = raw.split('|', limit = 4)
        if (parts.size < 4) {
            return null
        }
        return CustomRomModificationFinding(
            category = parts[0].decodeValue(),
            signal = parts[1].decodeValue(),
            summary = parts[2].decodeValue(),
            detail = parts[3].decodeValue(),
        )
    }

    private fun parseMapFinding(raw: String): CustomRomFinding? {
        val parts = raw.split('|', limit = 3)
        if (parts.size < 3) {
            return null
        }
        return CustomRomFinding(
            romName = parts[0],
            signal = parts[1],
            detail = parts[2].decodeValue(),
        )
    }

    private fun String.decodeValue(): String {
        return replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\u007c", "|")
    }

    private external fun nativeCollectSnapshot(): String
}
