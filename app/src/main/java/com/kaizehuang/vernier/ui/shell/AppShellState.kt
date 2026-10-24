package com.kaizehuang.vernier.ui.shell

import com.kaizehuang.vernier.notifications.ScanNotificationPermissionState
import com.kaizehuang.vernier.notifications.preferences.ScanNotificationPrefs
import com.kaizehuang.vernier.sdk.PackageVisibility

enum class AppDestination {
    MAIN,
    RECORDS,
    SETTINGS,
}

enum class StartupGateState {
    LOADING,
    REQUIRES_POLICY_REVIEW,
    READY,
}

/** Detector consents never block the first scan; the gate only waits for their decisions to load. */
fun resolveStartupGateState(
    consentDecisionsLoaded: Boolean,
    notificationPrefs: ScanNotificationPrefs?,
    notificationPermissionState: ScanNotificationPermissionState,
    packageVisibilityLoaded: Boolean,
    packageVisibility: PackageVisibility.Scope,
    packageVisibilityReviewAcknowledged: Boolean,
): StartupGateState {
    return when {
        !consentDecisionsLoaded || notificationPrefs == null || !packageVisibilityLoaded ->
            StartupGateState.LOADING

        !notificationPrefs.notificationsPrompted &&
                !notificationPermissionState.notificationsGranted ->
            StartupGateState.REQUIRES_POLICY_REVIEW

        notificationPermissionState.notificationsGranted &&
                notificationPermissionState.liveUpdatesSupported &&
                !notificationPermissionState.liveUpdatesGranted &&
                !notificationPrefs.liveUpdatesPrompted ->
            StartupGateState.REQUIRES_POLICY_REVIEW

        packageVisibility == PackageVisibility.Scope.RESTRICTED &&
                !packageVisibilityReviewAcknowledged ->
            StartupGateState.REQUIRES_POLICY_REVIEW

        else -> StartupGateState.READY
    }
}

fun shouldCreateDetectorViewModels(gateState: StartupGateState): Boolean {
    return gateState == StartupGateState.READY
}
