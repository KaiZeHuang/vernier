package com.kaizehuang.vernier.capability.helperprocess.data

import android.os.IBinder
import android.os.Parcel

internal class HelperProbeProxy(
    private val remote: IBinder,
) {

    fun collectSnapshot(): String {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(HelperProbeProtocol.DESCRIPTOR)
            remote.transact(
                HelperProbeProtocol.TRANSACTION_COLLECT_SNAPSHOT,
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

    fun collectProcMountView(): String {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(HelperProbeProtocol.DESCRIPTOR)
            remote.transact(
                HelperProbeProtocol.TRANSACTION_COLLECT_PROC_MOUNT_VIEW,
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

    fun isNativeAvailable(): Boolean {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(HelperProbeProtocol.DESCRIPTOR)
            remote.transact(
                HelperProbeProtocol.TRANSACTION_IS_NATIVE_AVAILABLE,
                data,
                reply,
                0,
            )
            reply.readException()
            reply.readInt() != 0
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    fun runSacrificialSyscallPack(): String {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(HelperProbeProtocol.DESCRIPTOR)
            remote.transact(
                HelperProbeProtocol.TRANSACTION_RUN_SACRIFICIAL_SYSCALL_PACK,
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
