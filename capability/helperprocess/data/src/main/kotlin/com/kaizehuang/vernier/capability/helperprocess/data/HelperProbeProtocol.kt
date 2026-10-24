package com.kaizehuang.vernier.capability.helperprocess.data

import android.os.IBinder

internal object HelperProbeProtocol {
    const val DESCRIPTOR: String = "com.kaizehuang.vernier.capability.helperprocess.probe"
    const val TRANSACTION_COLLECT_SNAPSHOT: Int = IBinder.FIRST_CALL_TRANSACTION + 0
    const val TRANSACTION_IS_NATIVE_AVAILABLE: Int = IBinder.FIRST_CALL_TRANSACTION + 1
    const val TRANSACTION_RUN_SACRIFICIAL_SYSCALL_PACK: Int = IBinder.FIRST_CALL_TRANSACTION + 2
    const val TRANSACTION_COLLECT_PROC_MOUNT_VIEW: Int = IBinder.FIRST_CALL_TRANSACTION + 3
    const val LIST_SEPARATOR: String = "\u001f"
}
