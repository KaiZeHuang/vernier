package com.kaizehuang.vernier.core.detector

import android.content.Context
import android.content.pm.ApplicationInfo
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorHeadline
import com.kaizehuang.vernier.core.report.DetectorReport

/**
 * One detector without any UI: how it collects its report, and how that report is described and
 * exported.
 *
 * Each detector has exactly one implementation, in its `feature/<name>/detector` module. The SDK
 * runs it headlessly and the application shows it as a card, so both see the same report, card
 * model and export.
 *
 * @param R the detector's domain report
 * @param M the card model a report is described as; its [DetectorHeadline.status] is the verdict
 */
public interface Detector<R : Any, M : DetectorHeadline> {
    public val id: DetectorId

    /**
     * Creates the scanner that one detector session keeps for all of its scans. A scanner reports
     * probe failures inside the report rather than throwing them.
     */
    public fun createScanner(context: Context): DetectorScanner<R>

    /** The report a card shows while a scan is still running. */
    public fun loadingReport(): R

    public fun describe(report: R): M

    /** The structured report the export renders for [model]. */
    public fun export(model: M): DetectorReport

    /**
     * Work this detector must do in the app zygote, before any isolated process forks from it. The
     * SDK's zygote preload runs every detector's in catalog order. It runs without a Context, so
     * most detectors have none.
     */
    public fun appZygotePreload(appInfo: ApplicationInfo) {}

    /** The choices this detector asks the user to make, such as whether it may use the network. */
    public val consents: List<DetectorConsent> get() = emptyList()
}

/** Collects one fresh report; each detector's data layer implements it. */
public fun interface DetectorScanner<out R : Any> {
    public suspend fun scan(): R
}
