package com.kaizehuang.vernier.features.nativeroot.data.native

import com.kaizehuang.vernier.core.native.NativeCollectionStatus

data class ThroneHuntWatchSnapshot(
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
    val watchInstalled: Boolean = false,
    val watchDescriptor: Int = -1,
    val errorNumber: Int = 0,
    val packageDirectory: String = "",
    val detail: String = "",
)

data class ThroneHuntEventSummary(
    val collection: NativeCollectionStatus = NativeCollectionStatus.Collected,
    val directoryOpenCount: Int = 0,
    val directoryAccessCount: Int = 0,
    val rawEventCount: Int = 0,
    val invalidCount: Int = 0,
    val detail: String = "",
) {
    val hitCount: Int
        get() = directoryOpenCount + directoryAccessCount
}
