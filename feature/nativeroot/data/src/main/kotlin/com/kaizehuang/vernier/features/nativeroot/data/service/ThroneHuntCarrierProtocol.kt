package com.kaizehuang.vernier.features.nativeroot.data.service

import android.os.IBinder

object ThroneHuntCarrierProtocol {
    const val DESCRIPTOR = "com.kaizehuang.vernier.features.nativeroot.throne_hunt"

    // Setup call: reports the watch descriptor without touching the event stream.
    const val TRANSACTION_COLLECT_SNAPSHOT = IBinder.FIRST_CALL_TRANSACTION + 0

    // Verdict call: the only transaction that reads (and therefore consumes) the event stream.
    const val TRANSACTION_DRAIN_EVENTS = IBinder.FIRST_CALL_TRANSACTION + 1
}
