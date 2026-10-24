package com.kaizehuang.vernier.features.nativeroot.data.probes

import com.kaizehuang.vernier.features.nativeroot.domain.SrcuTimingCollection

internal fun srcuTimingApplicability(apiLevel: Int, kernelRelease: String): SrcuTimingCollection? {
    // Android 11's BasePermission.addToTree stores a copy of the tree's ParsedPermission instead of
    // the supplied info, so the label readback that confirms each synchronous change cannot match.
    // Android 15 moved dynamic permissions to access.abx; API presence alone is insufficient.
    if (apiLevel !in 31..34) return SrcuTimingCollection.UNSUPPORTED_ANDROID
    val match = Regex("^(\\d+)\\.(\\d+)(?:[.-].*)?$").matchEntire(kernelRelease)
        ?: return SrcuTimingCollection.UNSUPPORTED_KERNEL
    val version = match.groupValues[1].toIntOrNull() to match.groupValues[2].toIntOrNull()
    return if (version in setOf(5 to 10, 5 to 15, 6 to 1, 6 to 6, 6 to 12)) null
        else SrcuTimingCollection.UNSUPPORTED_KERNEL
}
