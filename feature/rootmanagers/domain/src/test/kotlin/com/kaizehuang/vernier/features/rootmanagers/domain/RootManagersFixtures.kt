package com.kaizehuang.vernier.features.rootmanagers.domain

import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageVisibility

internal fun certOf(family: RootManagerFamily): CertificateFingerprint =
    RootManagerCatalog.signatures.single { it.family == family }.signingCertificates.first()

internal fun launcherApp(
    packageName: String,
    profileUserId: Int = 0,
    applicationClassName: String? = null,
    componentClassName: String? = null,
    label: String? = null,
    zygotePreloadName: String? = null,
    nativePayloads: Set<String> = emptySet(),
    hidesLauncherIcon: Boolean = false,
): ObservedApp = ObservedApp(
    profileUserId = profileUserId,
    packageName = packageName,
    source = ObservationSource.LAUNCHER_APPS,
    componentClassName = componentClassName,
    applicationClassName = applicationClassName,
    label = label,
    zygotePreloadName = zygotePreloadName,
    nativePayloads = nativePayloads,
    hidesLauncherIcon = hidesLauncherIcon,
    sourceDir = "/data/app/$packageName/base.apk",
    uid = 10_123,
    firstInstallTime = 1L,
)

internal fun packageManagerApp(
    packageName: String,
    profileUserId: Int = 0,
    applicationClassName: String? = null,
    zygotePreloadName: String? = null,
    signingCertificates: List<CertificateFingerprint> = emptyList(),
    nativePayloads: Set<String> = emptySet(),
): ObservedApp = ObservedApp(
    profileUserId = profileUserId,
    packageName = packageName,
    source = ObservationSource.PACKAGE_MANAGER,
    applicationClassName = applicationClassName,
    zygotePreloadName = zygotePreloadName,
    signingCertificates = signingCertificates,
    nativePayloads = nativePayloads,
)

internal fun entry(
    confidence: RootManagerConfidence,
    hidesLauncherIcon: Boolean = false,
    matchedCertificate: CertificateFingerprint? = null,
): RootManagerEntry = RootManagerEntry(
    family = RootManagerFamily.KERNEL_SU,
    packageName = "me.weishu.kernelsu",
    displayName = "KernelSU",
    profileUserId = 0,
    componentClassName = "me.weishu.kernelsu.ui.MainActivity",
    applicationClassName = "me.weishu.kernelsu.KernelSUApplication",
    sourceDir = null,
    uid = null,
    firstInstallTime = null,
    anchors = setOf(RootManagerAnchor.PACKAGE_NAME, RootManagerAnchor.APPLICATION_CLASS),
    confidence = confidence,
    matchedCertificate = matchedCertificate,
    hidesLauncherIcon = hidesLauncherIcon,
)

internal fun searched(profileUserId: Int, apps: Int = 40): RootManagersProfileScan =
    RootManagersProfileScan(profileUserId, ProfileScanState.SEARCHED, launcherActivitiesSeen = apps, appsChecked = apps)

internal fun fullCoverage(): RootManagersCoverage = RootManagersCoverage(
    callerSelfObserved = true,
    packageVisibility = InstalledPackageVisibility.FULL,
    certificatesRead = 120,
    payloadDirectoriesRead = 80,
)

internal fun evaluated(
    entries: List<RootManagerEntry> = emptyList(),
    profileScans: List<RootManagersProfileScan> = listOf(searched(0), searched(10, apps = 12)),
    coverage: RootManagersCoverage = fullCoverage(),
): RootManagersReport = RootManagersReport.evaluated(entries, profileScans, coverage)
