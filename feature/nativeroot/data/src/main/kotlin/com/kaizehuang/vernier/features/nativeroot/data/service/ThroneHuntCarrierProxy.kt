package com.kaizehuang.vernier.features.nativeroot.data.service

import android.os.IBinder
import android.os.Parcel

class ThroneHuntCarrierProxy(
    private val remote: IBinder,
) {

    fun collectSnapshot(): String = transact(ThroneHuntCarrierProtocol.TRANSACTION_COLLECT_SNAPSHOT)

    fun drainEvents(): String = transact(ThroneHuntCarrierProtocol.TRANSACTION_DRAIN_EVENTS)

    private fun transact(code: Int): String {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(ThroneHuntCarrierProtocol.DESCRIPTOR)
            remote.transact(code, data, reply, 0)
            reply.readException()
            reply.readString().orEmpty()
        } finally {
            data.recycle()
            reply.recycle()
        }
    }
}
