package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.os.Build

class BinderHookBootstrapProbe {

    fun inspect(): BinderHookBootstrapResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return BinderHookBootstrapResult(
                executed = false,
                detail = "Binder hook bootstrap probe requires Android 12 or newer.",
            )
        }
        return runCatching {
            val installed = KeystoreBinderCaptureHook.installHook()
            BinderHookBootstrapResult(
                executed = true,
                hookInstalled = installed,
                detail = if (installed) {
                    "Binder capture hook bootstrap confirmed."
                } else {
                    "Binder capture hook bootstrap failed."
                },
            )
        }.getOrElse { throwable ->
            BinderHookBootstrapResult(
                executed = true,
                hookInstalled = false,
                detail = throwable.message ?: "Binder hook bootstrap probe failed.",
            )
        }.also {
            KeystoreBinderCaptureHook.restore()
        }
    }
}

data class BinderHookBootstrapResult(
    val executed: Boolean,
    val hookInstalled: Boolean = false,
    val detail: String,
)
