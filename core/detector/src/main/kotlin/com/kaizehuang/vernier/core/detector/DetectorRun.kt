package com.kaizehuang.vernier.core.detector

import android.content.Context
import com.kaizehuang.vernier.core.report.DetectorHeadline
import com.kaizehuang.vernier.core.report.DetectorResult

/**
 * Scans once with a fresh scanner and returns the verdict and the export, without any UI.
 *
 * The result is what the dashboard would show and export for the same report: the status comes
 * from the card model's headline and the report from the same export projection.
 */
public suspend fun <R : Any, M : DetectorHeadline> Detector<R, M>.run(context: Context): DetectorResult {
    val model = describe(createScanner(context).scan())
    return DetectorResult(id = id, status = model.status, report = export(model))
}
