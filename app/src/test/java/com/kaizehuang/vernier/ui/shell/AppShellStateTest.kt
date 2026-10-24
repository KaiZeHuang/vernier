package com.kaizehuang.vernier.ui.shell

import com.kaizehuang.vernier.notifications.ScanNotificationPermissionState
import com.kaizehuang.vernier.notifications.preferences.ScanNotificationPrefs
import com.kaizehuang.vernier.sdk.PackageVisibility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppShellStateTest {

    @Test
    fun `null prefs stay in loading gate`() {
        val gateState = resolveStartupGateState(
            consentDecisionsLoaded = false,
            notificationPrefs = null,
            notificationPermissionState = ScanNotificationPermissionState(
                notificationsGranted = false,
                liveUpdatesSupported = true,
                liveUpdatesGranted = false,
            ),
            packageVisibilityLoaded = false,
            packageVisibility = PackageVisibility.Scope.UNKNOWN,
            packageVisibilityReviewAcknowledged = false,
        )

        assertEquals(StartupGateState.LOADING, gateState)
        assertFalse(shouldCreateDetectorViewModels(gateState))
    }

    @Test
    fun `missing notification permission requires notification decision`() {
        val gateState = resolveStartupGateState(
            consentDecisionsLoaded = true,
            notificationPrefs = ScanNotificationPrefs(
                notificationsPrompted = false,
                liveUpdatesPrompted = false,
            ),
            notificationPermissionState = ScanNotificationPermissionState(
                notificationsGranted = false,
                liveUpdatesSupported = true,
                liveUpdatesGranted = false,
            ),
            packageVisibilityLoaded = true,
            packageVisibility = PackageVisibility.Scope.FULL,
            packageVisibilityReviewAcknowledged = false,
        )

        assertEquals(StartupGateState.REQUIRES_POLICY_REVIEW, gateState)
        assertFalse(shouldCreateDetectorViewModels(gateState))
    }

    @Test
    fun `missing promoted access requires live update decision`() {
        val gateState = resolveStartupGateState(
            consentDecisionsLoaded = true,
            notificationPrefs = ScanNotificationPrefs(
                notificationsPrompted = true,
                liveUpdatesPrompted = false,
            ),
            notificationPermissionState = ScanNotificationPermissionState(
                notificationsGranted = true,
                liveUpdatesSupported = true,
                liveUpdatesGranted = false,
            ),
            packageVisibilityLoaded = true,
            packageVisibility = PackageVisibility.Scope.FULL,
            packageVisibilityReviewAcknowledged = false,
        )

        assertEquals(StartupGateState.REQUIRES_POLICY_REVIEW, gateState)
        assertFalse(shouldCreateDetectorViewModels(gateState))
    }

    @Test
    fun `detector consents do not block detector creation once their decisions load`() {
        val gateState = resolveStartupGateState(
            consentDecisionsLoaded = true,
            notificationPrefs = ScanNotificationPrefs(
                notificationsPrompted = true,
                liveUpdatesPrompted = true,
            ),
            notificationPermissionState = ScanNotificationPermissionState(
                notificationsGranted = true,
                liveUpdatesSupported = true,
                liveUpdatesGranted = true,
            ),
            packageVisibilityLoaded = true,
            packageVisibility = PackageVisibility.Scope.FULL,
            packageVisibilityReviewAcknowledged = false,
        )

        assertEquals(StartupGateState.READY, gateState)
        assertTrue(shouldCreateDetectorViewModels(gateState))
    }

    @Test
    fun `restricted package visibility requires explicit acknowledgement`() {
        val gateState = resolveStartupGateState(
            consentDecisionsLoaded = true,
            notificationPrefs = ScanNotificationPrefs(
                notificationsPrompted = true,
                liveUpdatesPrompted = true,
            ),
            notificationPermissionState = ScanNotificationPermissionState(
                notificationsGranted = true,
                liveUpdatesSupported = true,
                liveUpdatesGranted = true,
            ),
            packageVisibilityLoaded = true,
            packageVisibility = PackageVisibility.Scope.RESTRICTED,
            packageVisibilityReviewAcknowledged = false,
        )

        assertEquals(StartupGateState.REQUIRES_POLICY_REVIEW, gateState)
        assertFalse(shouldCreateDetectorViewModels(gateState))
    }

    @Test
    fun `answered prefs unlock detector creation`() {
        val gateState = resolveStartupGateState(
            consentDecisionsLoaded = true,
            notificationPrefs = ScanNotificationPrefs(
                notificationsPrompted = true,
                liveUpdatesPrompted = true,
            ),
            notificationPermissionState = ScanNotificationPermissionState(
                notificationsGranted = true,
                liveUpdatesSupported = true,
                liveUpdatesGranted = true,
            ),
            packageVisibilityLoaded = true,
            packageVisibility = PackageVisibility.Scope.FULL,
            packageVisibilityReviewAcknowledged = true,
        )

        assertEquals(StartupGateState.READY, gateState)
        assertTrue(shouldCreateDetectorViewModels(gateState))
    }

}
