package com.kaizehuang.vernier.core.platform

/**
 * Reads `android.os.SystemProperties`, which is hidden from the public SDK.
 *
 * Probes compare this Java path with native and getprop reads, so it calls the hidden method
 * reflectively rather than a native equivalent. The two `get` overloads are separate methods and a
 * hook can target either one, so each is exposed as it is. Every read looks the method up again.
 */
public object HiddenSystemProperties {

    private const val CLASS_NAME = "android.os.SystemProperties"

    /** `SystemProperties.get(key)`, or the failure that prevented the read. */
    public fun read(key: String): Result<String?> = runCatching {
        systemProperties().getMethod("get", String::class.java).invoke(null, key) as? String
    }

    /** `SystemProperties.get(key, default)`, or the failure that prevented the read. */
    public fun read(key: String, default: String): Result<String?> = runCatching {
        systemProperties()
            .getMethod("get", String::class.java, String::class.java)
            .invoke(null, key, default) as? String
    }

    private fun systemProperties(): Class<*> = Class.forName(CLASS_NAME)
}
