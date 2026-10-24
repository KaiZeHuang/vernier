package com.kaizehuang.vernier.capability.packageinventory.data

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.kaizehuang.vernier.capability.packageinventory.domain.DefaultInstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledApplicationQueryOptions
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledApplicationRecord
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledApplicationsQueryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledApplicationsSource
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryReader
import com.kaizehuang.vernier.capability.packageinventory.domain.InstalledPackageInventoryResult
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageInventoryFailure
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageInventoryFailureKind
import com.kaizehuang.vernier.core.platform.PlatformFailureName
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageVisibilityEnvironment
import com.kaizehuang.vernier.capability.packageinventory.domain.PackageVisibilityEnvironmentProvider

private const val QUERY_ALL_PACKAGES_PERMISSION = "android.permission.QUERY_ALL_PACKAGES"

/** Android adapter; all PackageManager API-level branching is owned here. */
public class AndroidInstalledApplicationsSource(
    context: Context,
    private val options: InstalledApplicationQueryOptions = InstalledApplicationQueryOptions(),
) : InstalledApplicationsSource {

    private val appContext = context.applicationContext

    @Suppress("DEPRECATION")
    override fun query(): InstalledApplicationsQueryResult {
        return try {
            val packageManager = appContext.packageManager
            val flags = if (options.includeMetadata) PackageManager.GET_META_DATA else 0
            val applications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledApplications(
                    PackageManager.ApplicationInfoFlags.of(flags.toLong()),
                )
            } else {
                packageManager.getInstalledApplications(flags)
            }
            InstalledApplicationsQueryResult.Available(
                applications.map { application ->
                    val metadataKeys = if (options.includeMetadata) {
                        application.metaData?.keySet()?.toSet().orEmpty()
                    } else {
                        emptySet()
                    }
                    val shouldResolveLabel = metadataKeys.any {
                        it in options.resolveLabelsForMetadataKeys
                    }
                    InstalledApplicationRecord(
                        packageName = application.packageName,
                        label = if (shouldResolveLabel) {
                            try {
                                packageManager.getApplicationLabel(application).toString()
                            } catch (_: Exception) {
                                application.packageName
                            }
                        } else {
                            application.packageName
                        },
                        metadataKeys = metadataKeys,
                    )
                },
            )
        } catch (exception: Exception) {
            InstalledApplicationsQueryResult.Unavailable(
                PackageInventoryFailure(
                    kind = PackageInventoryFailureKind.PLATFORM_QUERY_FAILED,
                    detail = PlatformFailureName.describe(exception),
                ),
            )
        }
    }
}

public class AndroidPackageVisibilityEnvironmentProvider(
    context: Context,
) : PackageVisibilityEnvironmentProvider {

    private val appContext = context.applicationContext

    @Suppress("DEPRECATION")
    override fun read(): PackageVisibilityEnvironment {
        val deviceSdk = Build.VERSION.SDK_INT
        val targetSdk = appContext.applicationInfo.targetSdkVersion
        if (deviceSdk < Build.VERSION_CODES.R || targetSdk < Build.VERSION_CODES.R) {
            return PackageVisibilityEnvironment(
                deviceSdk = deviceSdk,
                targetSdk = targetSdk,
                queryAllPackagesRequested = false,
            )
        }
        val flags = PackageManager.GET_PERMISSIONS
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appContext.packageManager.getPackageInfo(
                appContext.packageName,
                PackageManager.PackageInfoFlags.of(flags.toLong()),
            )
        } else {
            appContext.packageManager.getPackageInfo(appContext.packageName, flags)
        }
        return PackageVisibilityEnvironment(
            deviceSdk = deviceSdk,
            targetSdk = targetSdk,
            // AppsFilterUtils reads the parsed manifest's requested permissions directly. Do the
            // same rather than substituting runtime-permission state for the platform rule.
            queryAllPackagesRequested = packageInfo.requestedPermissions
                .orEmpty()
                .contains(QUERY_ALL_PACKAGES_PERMISSION),
        )
    }
}

public class AndroidInstalledPackageInventoryReader(
    context: Context,
    queryOptions: InstalledApplicationQueryOptions = InstalledApplicationQueryOptions(),
    applicationsSource: InstalledApplicationsSource =
        AndroidInstalledApplicationsSource(context, queryOptions),
    environmentProvider: PackageVisibilityEnvironmentProvider =
        AndroidPackageVisibilityEnvironmentProvider(context),
) : InstalledPackageInventoryReader {

    private val delegate = DefaultInstalledPackageInventoryReader(
        callerPackageName = context.packageName,
        applicationsSource = applicationsSource,
        environmentProvider = environmentProvider,
    )

    override fun read(): InstalledPackageInventoryResult = delegate.read()
}
