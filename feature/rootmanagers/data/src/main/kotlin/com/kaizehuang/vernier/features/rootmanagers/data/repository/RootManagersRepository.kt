package com.kaizehuang.vernier.features.rootmanagers.data.repository

import android.content.Context
import android.os.Process
import com.kaizehuang.vernier.capability.packageinventory.data.AndroidPackageVisibilityEnvironmentProvider
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibilityPolicy
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.evidence.FailureName
import com.kaizehuang.vernier.features.rootmanagers.data.probes.AppIdentityInspector
import com.kaizehuang.vernier.features.rootmanagers.data.probes.InstalledPackageSweep
import com.kaizehuang.vernier.features.rootmanagers.data.probes.LauncherEnumerationResult
import com.kaizehuang.vernier.features.rootmanagers.data.probes.LauncherProfileEnumerator
import com.kaizehuang.vernier.features.rootmanagers.data.probes.PackageSweepResult
import com.kaizehuang.vernier.features.rootmanagers.data.probes.ProfileObservation
import com.kaizehuang.vernier.features.rootmanagers.data.probes.profileId
import com.kaizehuang.vernier.features.rootmanagers.domain.ObservedApp
import com.kaizehuang.vernier.features.rootmanagers.domain.ProfileScanState
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagerEntryRules
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagersCoverage
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagersProfileScan
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagersReport
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Collects Root Managers evidence off the main thread; it enumerates and maps, never judges or words. */
class RootManagersRepository(private val context: Context) : DetectorScanner<RootManagersReport> {

    override suspend fun scan(): RootManagersReport = withContext(Dispatchers.IO) {
        try {
            collect(context.applicationContext)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (failure: Exception) {
            RootManagersReport.failed(FailureName.describe(failure))
        }
    }

    private fun collect(appContext: Context): RootManagersReport {
        val inspector = AppIdentityInspector()
        val callerUserId = Process.myUserHandle().profileId()
        val launcher = LauncherProfileEnumerator(appContext, inspector).enumerate()
        val sweep = InstalledPackageSweep(appContext, callerUserId, inspector) { callerObserved ->
            packageVisibility(appContext, callerObserved)
        }.sweep()

        val launcherProfiles = (launcher as? LauncherEnumerationResult.Enumerated)?.profiles.orEmpty()
        val observations = launcherProfiles.flatMap { profile -> profile.apps } + sweep.observations
        val entries = RootManagerEntryRules.entries(observations)
        val profileScans = profileScans(launcherProfiles, sweep, callerUserId, observations)
        val coverage = RootManagersCoverage(
            callerSelfObserved = callerSelfObserved(appContext, launcherProfiles, callerUserId),
            packageVisibility = sweep.visibility,
            certificatesRead = sweep.certificatesRead,
            certificateReadFailures = sweep.certificateReadFailures,
            payloadDirectoriesRead = inspector.payloadDirectoriesRead,
            payloadDirectoryFailures = inspector.payloadDirectoryFailures,
        )
        val issues = buildList {
            (launcher as? LauncherEnumerationResult.Enumerated)?.issues?.let(::addAll)
            sweep.issue?.let(::add)
            if (!inspector.zygotePreloadAvailable) {
                add("The zygote preload field could not be resolved, so the zygote anchor was not read.")
            }
            if (coverage.certificateReadFailures > 0) {
                add("${coverage.certificateReadFailures} app(s) returned no signing information.")
            }
            if (coverage.payloadDirectoryFailures > 0) {
                add("${coverage.payloadDirectoryFailures} native library director(ies) could not be read.")
            }
        }
        return when (launcher) {
            is LauncherEnumerationResult.Enumerated ->
                RootManagersReport.evaluated(entries, profileScans, coverage, issues)

            is LauncherEnumerationResult.Unavailable ->
                RootManagersReport.unavailable(launcher.reason, entries, profileScans, coverage, issues)

            is LauncherEnumerationResult.Undecidable ->
                RootManagersReport.undecidable(launcher.reason, entries, profileScans, coverage, issues)
        }
    }

    /**
     * One scan per launcher profile. When the launcher enumeration never ran, the caller's profile is
     * still represented through the PackageManager sweep so its entries keep a profile to belong to.
     */
    private fun profileScans(
        launcherProfiles: List<ProfileObservation>,
        sweep: PackageSweepResult,
        callerUserId: Int,
        observations: List<ObservedApp>,
    ): List<RootManagersProfileScan> {
        val appsByProfile = observations.groupBy { app -> app.profileUserId }
            .mapValues { (_, apps) -> apps.mapTo(hashSetOf()) { app -> app.packageName }.size }
        val scans = launcherProfiles.map { profile ->
            RootManagersProfileScan(
                profileUserId = profile.profileUserId,
                state = profile.state,
                launcherActivitiesSeen = profile.launcherActivitiesSeen,
                appsChecked = appsByProfile[profile.profileUserId] ?: 0,
            )
        }
        if (scans.any { scan -> scan.profileUserId == callerUserId } || sweep.observations.isEmpty()) {
            return scans
        }
        return scans + RootManagersProfileScan(
            profileUserId = callerUserId,
            state = ProfileScanState.EMPTY,
            appsChecked = appsByProfile[callerUserId] ?: 0,
        )
    }

    /**
     * The caller is itself launcher-visible in its own profile whenever it declares a launcher
     * activity, so missing from that list means the list was filtered past the platform baseline.
     */
    private fun callerSelfObserved(
        appContext: Context,
        launcherProfiles: List<ProfileObservation>,
        callerUserId: Int,
    ): Boolean? {
        val declaresLauncher = runCatching {
            appContext.packageManager.getLaunchIntentForPackage(appContext.packageName) != null
        }.getOrDefault(false)
        if (!declaresLauncher) {
            return null
        }
        val ownProfile = launcherProfiles.firstOrNull { profile -> profile.profileUserId == callerUserId }
            ?: return false
        return ownProfile.apps.any { app -> app.packageName == appContext.packageName }
    }

    private fun packageVisibility(appContext: Context, callerObserved: Boolean): InstalledPackageVisibility =
        runCatching {
            InstalledPackageVisibilityPolicy.evaluate(
                environment = AndroidPackageVisibilityEnvironmentProvider(appContext).read(),
                callerPackageObserved = callerObserved,
            )
        }.getOrDefault(InstalledPackageVisibility.UNKNOWN)
}
