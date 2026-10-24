package com.kaizehuang.vernier.capability.packageinventory.data

import com.kaizehuang.vernier.core.native.VernierNativeLibrary

/**
 * Checks for installed packages by stat-ing their data directories through raw syscalls.
 *
 * An error other than ENOENT counts as present, so the answer does not depend on
 * PackageManager's package visibility filtering.
 */
public class PackageDataDirectoryProbe {

    /** The packages whose data directory exists, or null when the native check could not run. */
    public fun statPackages(packageNames: List<String>): Set<String>? {
        if (packageNames.isEmpty()) {
            return emptySet()
        }
        // Asking the shared handle first is also what triggers the one-time load. Without this the
        // JNI call below would raise UnsatisfiedLinkError.
        if (!VernierNativeLibrary.isLoaded) {
            return null
        }
        return runCatching {
            nativeStatPackages(packageNames.toTypedArray())
                .lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
        }.getOrNull()
    }

    private external fun nativeStatPackages(packageNames: Array<String>): String
}
