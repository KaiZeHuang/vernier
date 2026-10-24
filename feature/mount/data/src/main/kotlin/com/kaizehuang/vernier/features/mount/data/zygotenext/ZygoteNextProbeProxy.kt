package com.kaizehuang.vernier.features.mount.data.zygotenext

import android.os.IBinder
import android.os.Parcel

class ZygoteNextProbeProxy(
    private val remote: IBinder,
) {

    fun collect(): String {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            // AIBinder_Class enforces its descriptor before invoking the native onTransact.
            data.writeInterfaceToken(ZygoteNextProbeProtocol.DESCRIPTOR)
            val handled = remote.transact(
                ZygoteNextProbeProtocol.TRANSACTION_COLLECT,
                data,
                reply,
                0,
            )
            check(handled) { "Native zygote_next Binder rejected the snapshot transaction." }
            reply.readException()
            reply.readString().orEmpty()
        } finally {
            data.recycle()
            reply.recycle()
        }
    }
}
