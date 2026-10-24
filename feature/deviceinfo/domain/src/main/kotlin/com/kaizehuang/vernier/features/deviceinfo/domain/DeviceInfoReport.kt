package com.kaizehuang.vernier.features.deviceinfo.domain

enum class DeviceInfoStage {
    LOADING,
    READY,
    FAILED,
}

/** The sections of the device profile, in the order it lists them. */
enum class DeviceInfoSectionKind(val title: String) {
    IDENTITY("Identity"),
    BUILD("Build"),
    ANDROID("Android"),
    RUNTIME("Runtime"),
    CONTEXT("Context"),
}

/** The entries the profile's header summarises. */
enum class DeviceInfoKey {
    BRAND,
    MODEL,
    ANDROID_RELEASE,
    SDK,
}

data class DeviceInfoEntry(
    val label: String,
    val value: String,
    val detailMonospace: Boolean = false,
    val key: DeviceInfoKey? = null,
)

data class DeviceInfoSection(
    val kind: DeviceInfoSectionKind,
    val entries: List<DeviceInfoEntry>,
) {
    val title: String get() = kind.title
}

data class DeviceInfoReport(
    val stage: DeviceInfoStage,
    val sections: List<DeviceInfoSection>,
    val errorMessage: String? = null,
) {
    val totalCount: Int
        get() = sections.sumOf { it.entries.size }

    companion object {
        fun loading(): DeviceInfoReport {
            return DeviceInfoReport(
                stage = DeviceInfoStage.LOADING,
                sections = emptyList(),
            )
        }

        fun failed(message: String): DeviceInfoReport {
            return DeviceInfoReport(
                stage = DeviceInfoStage.FAILED,
                sections = emptyList(),
                errorMessage = message,
            )
        }
    }
}
