package com.kaizehuang.vernier.features.customrom.data.repository

import com.kaizehuang.vernier.features.customrom.data.native.CustomRomNativeSnapshot
import com.kaizehuang.vernier.features.customrom.data.rules.CustomRomCatalog
import com.kaizehuang.vernier.features.customrom.domain.CustomRomModificationFinding

class CustomRomModificationProbe {

    val checkedPropertyCount: Int
        get() = CustomRomCatalog.modificationProperties.size

    fun inspect(nativeSnapshot: CustomRomNativeSnapshot): List<CustomRomModificationFinding> {
        return nativeSnapshot.modificationFindings.distinct()
    }
}
