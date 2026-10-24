package com.kaizehuang.vernier.capability.selinuxpolicy.data

import android.os.IBinder

internal object SelinuxContextValidityCarrierProtocol {
    const val DESCRIPTOR: String = "com.kaizehuang.vernier.capability.selinuxpolicy.context_validity"
    const val TRANSACTION_COLLECT_SNAPSHOT: Int = IBinder.FIRST_CALL_TRANSACTION + 0
}
