package com.kaizehuang.vernier.features.rootmanagers.data.probes

import android.content.pm.ApplicationInfo
import java.lang.reflect.Field
import org.lsposed.hiddenapibypass.HiddenApiBypass

/**
 * Reads the `@hide` field `ApplicationInfo.zygotePreloadName`, which `ApplicationInfo.writeToParcel`
 * carries across Binder but the SDK does not expose.
 *
 * `HiddenApiBypass.getInstanceFields` resolves the field from ART's field table without adding a
 * hidden-API exemption, so the process-wide policy that other detectors observe stays untouched.
 * Parsing every manifest through `getResourcesForApplication` would avoid the hidden field too, but
 * costs an AssetManager per app across the whole inventory.
 */
internal object HiddenZygotePreloadField {

    private const val FIELD_NAME = "zygotePreloadName"

    private val zygoteField: Field? by lazy {
        runCatching {
            HiddenApiBypass.getInstanceFields(ApplicationInfo::class.java).firstOrNull { it.name == FIELD_NAME }
        }.getOrNull()
    }

    /** False when the field could not be resolved, so the zygote anchor is missing, not absent. */
    val available: Boolean
        get() = zygoteField != null

    fun read(info: ApplicationInfo): String? {
        val resolved = zygoteField ?: return null
        return runCatching { resolved.get(info) as? String }.getOrNull()
    }
}
