package com.kaizehuang.vernier.core.detector

/**
 * Marks a detector's own typed object, such as `SuDetector`. Its name and its report and card model
 * types follow that detector and may change in any release, unlike the [Detector] contract and
 * `DetectorResult`, whose public API dumps guard every change.
 *
 * A host that only needs results reaches every detector through `Vernier` and never opts in.
 * A host that uses one detector's typed models opts in, accepting that they change with it.
 */
@RequiresOptIn(
    message = "A detector's typed object and its report and card model types change with that detector in any release; Vernier and the Detector contract do not.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class DetectorSpecificApi
