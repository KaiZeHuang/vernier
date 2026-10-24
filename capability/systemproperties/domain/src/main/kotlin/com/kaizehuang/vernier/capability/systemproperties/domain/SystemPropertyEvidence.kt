package com.kaizehuang.vernier.capability.systemproperties.domain

public enum class SystemPropertySeverity {
    SAFE,
    WARNING,
    DANGER,
    NEUTRAL,
}

public enum class SystemPropertyCategory {
    SECURITY_CORE,
    VERIFIED_BOOT,
    PARTITION_VERITY,
    BUILD_PROFILE,
    ROOT_RUNTIME,
    CUSTOM_ROM,
    DEVICE_INFO,
    BUILD_FINGERPRINT,
    SOURCE_CONSISTENCY,
    PROPERTY_CONSISTENCY,
}

public enum class SystemPropertySource {
    REFLECTION,
    GETPROP,
    JVM,
    BUILD,
    NATIVE_LIBC,
    CMDLINE,
    BOOTCONFIG,
}

public data class SystemPropertySignal(
    val property: String,
    val description: String,
    val value: String,
    val category: SystemPropertyCategory,
    val severity: SystemPropertySeverity,
    val source: SystemPropertySource,
    val detail: String? = null,
)
