package com.kaizehuang.vernier.features.deviceinfo.presentation.model

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoSectionKind

data class DeviceInfoCardModel(
    val title: String,
    val subtitle: String,
    val status: DetectorStatus,
    val verdict: String,
    val summary: String,
    val headerFacts: List<DeviceInfoHeaderFactModel>,
    val sections: List<DeviceInfoSectionModel>,
)

/** The facts in the profile's header, in the order the export lists them. */
enum class DeviceInfoHeaderFact(val label: String) {
    BRAND("Brand"),
    MODEL("Model"),
    ANDROID("Android"),
    SDK("SDK"),
}

data class DeviceInfoHeaderFactModel(
    val fact: DeviceInfoHeaderFact,
    val value: String,
) {
    val label: String get() = fact.label
}

data class DeviceInfoSectionModel(
    val title: String,
    val rows: List<DeviceInfoRowModel>,
    /** The profile section this is; null for the section that explains a failed collection. */
    val kind: DeviceInfoSectionKind? = null,
)

data class DeviceInfoRowModel(
    val label: String,
    val value: String,
    val detailMonospace: Boolean = false,
)
