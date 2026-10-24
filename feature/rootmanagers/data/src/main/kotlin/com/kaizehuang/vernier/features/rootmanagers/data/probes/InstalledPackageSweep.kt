package com.kaizehuang.vernier.features.rootmanagers.data.probes

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility
import com.kaizehuang.vernier.core.evidence.FailureName
import com.kaizehuang.vernier.features.rootmanagers.domain.CertificateFingerprint
import com.kaizehuang.vernier.features.rootmanagers.domain.ObservationSource
import com.kaizehuang.vernier.features.rootmanagers.domain.ObservedApp

internal data class PackageSweepResult(
    val observations: List<ObservedApp>,
    val visibility: InstalledPackageVisibility,
    val certificatesRead: Int,
    val certificateReadFailures: Int,
    val issue: String? = null,
) {
    companion object {
        fun unavailable(issue: String): PackageSweepResult = PackageSweepResult(
            observations = emptyList(),
            visibility = InstalledPackageVisibility.UNKNOWN,
            certificatesRead = 0,
            certificateReadFailures = 0,
            issue = issue,
        )
    }
}

/**
 * Reads the caller's own profile through PackageManager, the only path that reports signing
 * certificates. One `getInstalledPackages(GET_SIGNING_CERTIFICATES)` call carries the certificate of
 * every visible app, so a manager renamed past its package, label and class names is still caught
 * by the key its own kernel was built to trust, without a round trip per app.
 */
internal class InstalledPackageSweep(
    private val context: Context,
    private val profileUserId: Int,
    private val inspector: AppIdentityInspector,
    /** Maps "the caller saw its own package" to a visibility verdict through the shared policy. */
    private val visibilityFor: (callerObserved: Boolean) -> InstalledPackageVisibility,
) {

    @Suppress("DEPRECATION")
    fun sweep(): PackageSweepResult {
        val packageManager = context.packageManager
        val flags = PackageManager.GET_SIGNING_CERTIFICATES
        val packages = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                packageManager.getInstalledPackages(flags)
            }
        } catch (failure: Exception) {
            return PackageSweepResult.unavailable(
                "The installed-package sweep was unavailable: ${FailureName.messageOrName(failure)}",
            )
        }

        var certificatesRead = 0
        var certificateReadFailures = 0
        val observations = packages.map { packageInfo ->
            val certificates = SigningCertificates.of(packageInfo)
            if (certificates == null) certificateReadFailures++ else certificatesRead++
            packageInfo.toObservation(certificates.orEmpty())
        }
        val callerObserved = packages.any { packageInfo -> packageInfo.packageName == context.packageName }
        return PackageSweepResult(
            observations = observations,
            visibility = visibilityFor(callerObserved),
            certificatesRead = certificatesRead,
            certificateReadFailures = certificateReadFailures,
        )
    }

    private fun PackageInfo.toObservation(certificates: List<CertificateFingerprint>): ObservedApp {
        val info = applicationInfo
        return ObservedApp(
            profileUserId = profileUserId,
            packageName = packageName,
            source = ObservationSource.PACKAGE_MANAGER,
            applicationClassName = info?.className,
            zygotePreloadName = inspector.zygotePreloadName(info),
            signingCertificates = certificates,
            nativePayloads = inspector.nativePayloads(info),
            sourceDir = info?.sourceDir,
            uid = info?.uid,
            firstInstallTime = firstInstallTime,
        )
    }
}
