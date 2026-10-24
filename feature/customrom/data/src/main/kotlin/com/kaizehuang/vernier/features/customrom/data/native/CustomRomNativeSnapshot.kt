package com.kaizehuang.vernier.features.customrom.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus
import com.kaizehuang.vernier.features.customrom.domain.CustomRomFinding
import com.kaizehuang.vernier.features.customrom.domain.CustomRomModificationFinding

data class CustomRomNativeSnapshot(
    val available: Boolean = false,
    val propertyAreaAvailable: Boolean = false,
    val propertyAreaContextCount: Int = 0,
    val propertyAreaAnomalyCount: Int = 0,
    val propertyAreaItemCount: Int = 0,
    val symbolScanAvailable: Boolean = false,
    val platformFiles: List<CustomRomFinding> = emptyList(),
    val modificationFindings: List<CustomRomModificationFinding> = emptyList(),
    val resourceInjectionFindings: List<CustomRomFinding> = emptyList(),
    val recoveryScripts: List<String> = emptyList(),
    val policyFindings: List<CustomRomFinding> = emptyList(),
    val overlayFindings: List<CustomRomFinding> = emptyList(),
    val symbolFindings: List<CustomRomFinding> = emptyList(),
    /**
     * Why this snapshot is or is not usable. [available] alone cannot distinguish "the probe ran and
     * found nothing" from "the probe never ran", so the reason is carried here.
     */
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
) {
    val propertyAreaItemAnomalyCount: Int
        get() = propertyAreaItemCount
}
