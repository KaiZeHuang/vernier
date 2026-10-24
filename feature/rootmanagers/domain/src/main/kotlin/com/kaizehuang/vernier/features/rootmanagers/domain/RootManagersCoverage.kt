package com.kaizehuang.vernier.features.rootmanagers.domain

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility

/**
 * How much of the caller's own profile the identity probes could read. These decide whether an
 * empty result may read as clean: each gap is a way a present manager could go unseen.
 */
data class RootManagersCoverage(
    /**
     * Whether the caller's own launcher activity appeared in its own profile's launcher list; null
     * when the caller declares no launcher activity, so there is nothing to expect. False means the
     * list no longer satisfies the platform baseline, so its absences are not defensible.
     */
    val callerSelfObserved: Boolean? = null,
    /** PackageManager visibility for the caller-profile sweep, from the shared package-visibility policy. */
    val packageVisibility: InstalledPackageVisibility = InstalledPackageVisibility.UNKNOWN,
    val certificatesRead: Int = 0,
    val certificateReadFailures: Int = 0,
    val payloadDirectoriesRead: Int = 0,
    val payloadDirectoryFailures: Int = 0,
) {
    /** No certificate could be read although apps were checked, so the key anchor never had a chance. */
    val certificatesUnavailable: Boolean
        get() = certificatesRead == 0 && certificateReadFailures > 0
}
