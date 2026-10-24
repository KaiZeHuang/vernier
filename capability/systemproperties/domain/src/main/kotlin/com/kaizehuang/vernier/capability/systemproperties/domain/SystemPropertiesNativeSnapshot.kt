package com.kaizehuang.vernier.capability.systemproperties.domain

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

public data class PropAreaFinding(
    val context: String,
    val holeCount: Int,
    val detail: String,
)

public data class SystemPropertiesNativeSnapshot(
    val available: Boolean = false,
    val libcProperties: Map<String, String> = emptyMap(),
    val cmdlineBootParams: Map<String, String> = emptyMap(),
    val bootconfigBootParams: Map<String, String> = emptyMap(),
    val rawCmdline: String = "",
    val rawBootconfig: String = "",
    val propAreaAvailable: Boolean = false,
    val propAreaContextCount: Int = 0,
    val propAreaHoleCount: Int = 0,
    val propAreaFindings: List<PropAreaFinding> = emptyList(),
    val readOnlyPropertyHandleAvailable: Boolean = false,
    val readOnlyPropertyHandleCheckedCount: Int = 0,
    /**
     * Why this snapshot is or is not usable. [available] alone cannot distinguish "the probe ran and
     * found nothing" from "the probe never ran", so the reason is carried here.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
) {
    val nativePropertyHitCount: Int
        get() = libcProperties.values.count { sanitizeLibcValue(it).isNotBlank() }

    val bootParamHitCount: Int
        get() = (cmdlineBootParams.keys + bootconfigBootParams.keys).toSet().size

    public fun libcValue(
        property: String,
    ): String {
        return sanitizeLibcValue(libcProperties[property])
    }

    public fun findBootValueForProperty(
        property: String,
    ): Pair<SystemPropertySource, String>? {
        val bootKey = propertyToBootKey(property) ?: return null
        bootconfigBootParams[bootKey]
            ?.takeIf { it.isNotBlank() }
            ?.let { return SystemPropertySource.BOOTCONFIG to it }
        cmdlineBootParams[bootKey]
            ?.takeIf { it.isNotBlank() }
            ?.let { return SystemPropertySource.CMDLINE to it }
        return null
    }

    private fun propertyToBootKey(
        property: String,
    ): String? {
        if (!property.startsWith(RO_BOOT_PREFIX)) {
            return null
        }
        return ANDROIDBOOT_PREFIX + property.removePrefix(RO_BOOT_PREFIX)
    }

    private companion object {
        private const val RO_BOOT_PREFIX = "ro.boot."
        private const val ANDROIDBOOT_PREFIX = "androidboot."
        private const val CALLBACK_REQUIRED_MESSAGE =
            "Must use __system_property_read_callback() to read"
    }

    private fun sanitizeLibcValue(
        value: String?,
    ): String {
        val trimmed = value?.trim().orEmpty()
        return if (trimmed.contains(CALLBACK_REQUIRED_MESSAGE, ignoreCase = true)) {
            ""
        } else {
            trimmed
        }
    }
}
