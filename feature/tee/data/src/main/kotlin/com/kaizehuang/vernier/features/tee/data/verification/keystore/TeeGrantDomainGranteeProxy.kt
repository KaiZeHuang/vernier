package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.os.IBinder
import android.os.Parcel

class TeeGrantDomainGranteeProxy(
    private val remote: IBinder,
) {

    fun getUid(): Int {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(TeeGrantDomainGranteeProtocol.DESCRIPTOR)
            remote.transact(
                TeeGrantDomainGranteeProtocol.TRANSACTION_GET_UID,
                data,
                reply,
                0,
            )
            reply.readException()
            reply.readInt()
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    fun readGrantedCertificateChain(
        grantId: Long,
        keystore2Binder: IBinder,
    ): TeeGrantDomainGranteeChainResult {
        return readGrantedCertificateChainInternal(
            transactionCode = TeeGrantDomainGranteeProtocol.TRANSACTION_READ_GRANTED_CHAIN,
            grantId = grantId,
            keystore2Binder = keystore2Binder,
        )
    }

    fun readGrantedCertificateChainJavaApi(
        grantId: Long,
        hiddenApi: Boolean,
    ): TeeGrantDomainGranteeChainResult {
        return readGrantedCertificateChainInternal(
            transactionCode = if (hiddenApi) {
                TeeGrantDomainGranteeProtocol.TRANSACTION_READ_GRANTED_CHAIN_HIDDEN
            } else {
                TeeGrantDomainGranteeProtocol.TRANSACTION_READ_GRANTED_CHAIN_PUBLIC
            },
            grantId = grantId,
            keystore2Binder = null,
        )
    }

    private fun readGrantedCertificateChainInternal(
        transactionCode: Int,
        grantId: Long,
        keystore2Binder: IBinder?,
    ): TeeGrantDomainGranteeChainResult {
        // Java-layer readback sends only the grant id. The private Binder transaction additionally
        // carries the owner-resolved Keystore2 binder for the incremental isolated plane check.
        // Java 层回读只发送 grant id；private Binder transaction 额外携带 owner 解析到的 Keystore2 binder，用于增量 isolated 平面检测。
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(TeeGrantDomainGranteeProtocol.DESCRIPTOR)
            data.writeLong(grantId)
            keystore2Binder?.let { data.writeStrongBinder(it) }
            remote.transact(
                transactionCode,
                data,
                reply,
                0,
            )
            reply.readException()
            TeeGrantDomainGranteeChainResult.readFromParcel(reply)
        } finally {
            data.recycle()
            reply.recycle()
        }
    }
}
