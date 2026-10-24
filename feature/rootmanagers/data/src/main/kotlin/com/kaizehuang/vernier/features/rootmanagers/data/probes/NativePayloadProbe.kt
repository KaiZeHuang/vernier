package com.kaizehuang.vernier.features.rootmanagers.data.probes

import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import com.kaizehuang.vernier.core.platform.PlatformFailureName

internal sealed interface PayloadRead {
    /** The directory was observable; [present] lists the catalogued payloads found in it. */
    data class Read(val present: Set<String>) : PayloadRead

    /** A stat failed for a reason other than absence, so the payloads are unobserved. */
    data class Failed(val reason: String) : PayloadRead
}

/**
 * Looks for catalogued daemons in an app's extracted native library directory.
 *
 * The managers execute these binaries (`libksud.so`, `libapd.so`, `libmagisk.so`, ...) straight from
 * `nativeLibraryDir`, and their builds set `useLegacyPackaging` so the installer extracts them; a
 * rename of the package, label or classes leaves the file names in place. A stat needs only search
 * permission on the path, and `ENOENT` is a real absence while any other errno leaves the directory
 * unobserved rather than clean.
 */
internal class NativePayloadProbe(private val payloadNames: Set<String>) {

    fun read(nativeLibraryDir: String?): PayloadRead {
        val directory = nativeLibraryDir ?: return PayloadRead.Read(emptySet())
        val present = linkedSetOf<String>()
        for (name in payloadNames) {
            try {
                Os.stat("$directory/$name")
                present += name
            } catch (failure: ErrnoException) {
                if (failure.errno != OsConstants.ENOENT && failure.errno != OsConstants.ENOTDIR) {
                    return PayloadRead.Failed(PlatformFailureName.describe(failure))
                }
            }
        }
        return PayloadRead.Read(present)
    }
}
