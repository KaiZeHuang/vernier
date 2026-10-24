package com.kaizehuang.vernier.core.platform

/**
 * Recognises platform failures whose types are hidden from the public SDK.
 *
 * Binder calls raise `android.os.ServiceSpecificException`, `ParcelableException` and
 * `DeadSystemRuntimeException`, none of which exist in android.jar, so no type check can name
 * them. Their runtime class name is the only identity they have, so it is compared here and
 * nowhere else, and reports receive a literal name rather than the class name itself.
 */
public object HiddenPlatformFailure {

    private const val SERVICE_SPECIFIC = "android.os.ServiceSpecificException"
    private const val PARCELABLE = "android.os.ParcelableException"
    private const val DEAD_SYSTEM_RUNTIME = "android.os.DeadSystemRuntimeException"

    internal fun nameOf(failure: Throwable): String? = when (failure.javaClass.name) {
        SERVICE_SPECIFIC -> "ServiceSpecificException"
        PARCELABLE -> "ParcelableException"
        DEAD_SYSTEM_RUNTIME -> "DeadSystemRuntimeException"
        else -> null
    }

    public fun isServiceSpecific(failure: Throwable): Boolean = failure.javaClass.name == SERVICE_SPECIFIC

    public fun isParcelable(failure: Throwable): Boolean = failure.javaClass.name == PARCELABLE

    /** The public `errorCode` of a ServiceSpecificException, read reflectively because its class is hidden. */
    public fun serviceSpecificErrorCode(failure: Throwable): Int? {
        if (!isServiceSpecific(failure)) {
            return null
        }
        return runCatching {
            val field = failure.javaClass.getField("errorCode")
            field.isAccessible = true
            field.get(failure) as? Int
        }.getOrNull()
    }
}
