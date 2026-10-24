package com.kaizehuang.vernier.capability.selinuxpolicy.data

import android.os.IBinder
import android.os.Parcel

internal class SelinuxContextValidityCarrierProxy(
    private val remote: IBinder,
) {

    fun collectSnapshot(): String {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(SelinuxContextValidityCarrierProtocol.DESCRIPTOR)
            remote.transact(
                SelinuxContextValidityCarrierProtocol.TRANSACTION_COLLECT_SNAPSHOT,
                data,
                reply,
                0,
            )
            reply.readException()
            reply.readString().orEmpty()
        } finally {
            data.recycle()
            reply.recycle()
        }
    }
}
