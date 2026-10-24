package com.kaizehuang.vernier.features.nativeroot.detector

import android.content.Context
import android.content.pm.ApplicationInfo
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.detector.DetectorConsent
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.detector.DetectorSpecificApi
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.features.nativeroot.data.repository.NativeRootRepository
import com.kaizehuang.vernier.features.nativeroot.data.service.ThroneHuntWatchInstaller
import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootReport
import com.kaizehuang.vernier.features.nativeroot.presentation.NativeRootCardModelMapper
import com.kaizehuang.vernier.features.nativeroot.presentation.model.NativeRootCardModel
import com.kaizehuang.vernier.features.nativeroot.presentation.toDetectorReport

/**
 * Native Root: looks for kernel-level root such as KernelSU, KernelPatch and SuSFS through
 * native probes, process and cgroup leaks, mount namespace drift, manager fingerprints,
 * temporary root artifacts and the KernelSU throne-hunt stimulus.
 *
 * Collected by [NativeRootRepository], judged by `NativeRootReport.toDetectorStatus()` in the
 * domain layer, and described by [NativeRootCardModelMapper].
 */
@DetectorSpecificApi
public object NativeRootDetector : Detector<NativeRootReport, NativeRootCardModel> {
    override val id: DetectorId = DetectorId("native_root")

    override val consents: List<DetectorConsent> =
        listOf(NativeRootSrcuTimingConsent)

    override fun createScanner(context: Context): DetectorScanner<NativeRootReport> = NativeRootRepository(context)

    override fun loadingReport(): NativeRootReport = NativeRootReport.loading()

    override fun describe(report: NativeRootReport): NativeRootCardModel = NativeRootCardModelMapper().map(report)

    override fun export(model: NativeRootCardModel): DetectorReport = model.toDetectorReport()

    /**
     * KernelSU's pkg_observer reacts to a /data/system/packages.list rewrite by running
     * track_throne -> search_manager("/data/app", 2), which opens and iterates every package
     * directory inode. Watching this package's own directory from app_zygote is what makes that
     * kernel-side traversal observable, so the watch is installed there rather than in the child.
     * Without the app zygote preload the throne-hunt carrier reports its collection as failed,
     * because the dedicated app_zygote preload state is unavailable.
     */
    override fun appZygotePreload(appInfo: ApplicationInfo) {
        ThroneHuntWatchInstaller.publish(ThroneHuntWatchInstaller.install(appInfo))
    }
}
