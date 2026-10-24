package com.kaizehuang.vernier.capability.selinuxpolicy.data

import com.kaizehuang.vernier.core.native.NativeSnapshotCollector

/** Runs the rejected writes in a disposable child of the app_zygote preload, never in the carrier itself. */
internal class SelinuxAvcLookupProbe(
    private val collector: NativeSnapshotCollector = NativeSnapshotCollector.Default,
) {
    fun inspect(): SelinuxAvcLookupSnapshot = collector.collect(
        readPayload = ::nativeCollectAvcLookup,
        parse = SelinuxAvcLookupPayloadCodec::decode,
        unavailable = { failure ->
            SelinuxAvcLookupSnapshot(
                state = SelinuxAvcLookupState.UNAVAILABLE,
                failureReason = failure.explain("App zygote AVC lookup experiment was unavailable"),
            )
        },
    )

    private external fun nativeCollectAvcLookup(): String
}
