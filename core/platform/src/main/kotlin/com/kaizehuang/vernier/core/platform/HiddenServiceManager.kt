package com.kaizehuang.vernier.core.platform

import android.os.IBinder

/**
 * Calls `android.os.ServiceManager`, which is hidden from the public SDK.
 *
 * Probes ask the service manager directly for registrations that the public APIs never expose.
 * Every call looks the method up again and returns the failure instead of throwing, so each probe
 * keeps its own scope for how far a failure reaches.
 */
public object HiddenServiceManager {

    private const val CLASS_NAME = "android.os.ServiceManager"

    /** `ServiceManager.getService(name)`, or the failure that prevented the call. */
    public fun getService(name: String): Result<IBinder?> = runCatching {
        serviceManager().getMethod("getService", String::class.java).invoke(null, name) as? IBinder
    }

    /** The names `ServiceManager.listServices()` returned, or the failure that prevented the call. */
    public fun listServices(): Result<List<String>> = runCatching {
        (serviceManager().getMethod("listServices").invoke(null) as? Array<*>)
            ?.filterIsInstance<String>()
            .orEmpty()
    }

    private fun serviceManager(): Class<*> = Class.forName(CLASS_NAME)
}
