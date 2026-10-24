package com.kaizehuang.vernier.features.rootmanagers.domain

enum class ProfileScanState {
    /** The profile returned launcher activities, so a clean result means it was searched. */
    SEARCHED,

    /**
     * The profile returned no launcher activity at all. A usable profile always lists some, and
     * `LauncherApps.getActivityList` also returns an empty list when the service refuses an
     * inaccessible profile, so an empty profile counts as not observed rather than as clean.
     */
    EMPTY,

    /** The platform threw `SecurityException` for this profile. */
    DENIED,
}

/**
 * What the enumeration saw in one profile. It keeps counts only: the identities of unmatched apps
 * are the user's app inventory and leave the scan, the report and the clipboard untouched.
 */
data class RootManagersProfileScan(
    val profileUserId: Int,
    val state: ProfileScanState,
    val launcherActivitiesSeen: Int = 0,
    /** Distinct apps whose identity was checked here, launcher entries and PackageManager sweep alike. */
    val appsChecked: Int = 0,
)
