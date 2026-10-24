package com.kaizehuang.vernier.features.virtualization.data.probes

import android.os.Build
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignal
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalGroup
import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity

open class VirtualizationBuildProbe {

    open fun probe(): List<VirtualizationSignal> {
        val fields = listOf(
            "Build.FINGERPRINT" to Build.FINGERPRINT.orEmpty(),
            "Build.PRODUCT" to Build.PRODUCT.orEmpty(),
            "Build.MODEL" to Build.MODEL.orEmpty(),
            "Build.BOARD" to Build.BOARD.orEmpty(),
            "Build.DEVICE" to Build.DEVICE.orEmpty(),
            "Build.BRAND" to Build.BRAND.orEmpty(),
            "Build.MANUFACTURER" to Build.MANUFACTURER.orEmpty(),
            "Build.HOST" to Build.HOST.orEmpty(),
            "Build.TAGS" to Build.TAGS.orEmpty(),
        ).filter { it.second.isNotBlank() }
        return evaluate(fields)
    }

    internal fun evaluate(
        fields: List<Pair<String, String>>,
    ): List<VirtualizationSignal> {
        val strongTokens = listOf(
            "goldfish",
            "ranchu",
            "sdk_gphone",
            "google_sdk",
            "android sdk built for x86",
            "emulator",
            "genymotion",
            "vbox",
            "vbox86p",
        )
        val weakTokens = listOf("generic", "unknown", "test-keys")

        val strongMatches = fields.mapNotNull { (label, value) ->
            strongTokens.firstOrNull { token -> value.contains(token, ignoreCase = true) }
                ?.let { hit ->
                    Triple(label, value, hit)
                }
        }
        val weakMatches = fields.mapNotNull { (label, value) ->
            weakTokens.firstOrNull { token -> value.contains(token, ignoreCase = true) }
                ?.let { hit ->
                    Triple(label, value, hit)
                }
        }

        return buildList {
            strongMatches.forEachIndexed { index, (fieldName, value, token) ->
                add(
                    VirtualizationSignal(
                        id = "virt_build_strong_$index",
                        label = fieldName,
                        value = token,
                        group = VirtualizationSignalGroup.ENVIRONMENT,
                        severity = VirtualizationSignalSeverity.DANGER,
                        detail = "Known emulator build token '$token' matched in $fieldName=$value",
                        detailMonospace = true,
                    ),
                )
            }

            if (strongMatches.isEmpty() && weakMatches.size >= 2) {
                add(
                    VirtualizationSignal(
                        id = "virt_build_weak_cluster",
                        label = "Generic build tokens",
                        value = "${weakMatches.size} hit(s)",
                        group = VirtualizationSignalGroup.ENVIRONMENT,
                        severity = VirtualizationSignalSeverity.WARNING,
                        detail = weakMatches.joinToString(separator = "\n") { (fieldName, value, token) ->
                            "$fieldName matched '$token' in $value"
                        },
                        detailMonospace = true,
                    ),
                )
            }
        }
    }
}
