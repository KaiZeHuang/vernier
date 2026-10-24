package com.kaizehuang.vernier.capability.selinuxpolicy.data

import com.kaizehuang.vernier.core.native.NativeSnapshotCollector

/** Runs direct selinuxfs transactions in a disposable child; never invokes libselinux's AVC cache. */
internal class SelinuxSidtabProbe(
    private val collector: NativeSnapshotCollector = NativeSnapshotCollector.Default,
) {
    fun inspect(): SelinuxSidtabSnapshot = collector.collect(
        readPayload = ::nativeCollectSidtab,
        parse = SelinuxSidtabPayloadCodec::decode,
        unavailable = { failure ->
            SelinuxSidtabSnapshot(
                collection = SelinuxSidtabCollection.UNAVAILABLE,
                failureReason = failure.explain("SID-table experiment was unavailable"),
            )
        },
    )

    private external fun nativeCollectSidtab(): String
}
