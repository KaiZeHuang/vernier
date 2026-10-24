package com.kaizehuang.vernier.buildlogic

import org.gradle.api.Project

/** The layer that makes a feature unit a detector; supporting features such as the dashboard have none. */
const val DETECTOR_LAYER: String = "detector"

/**
 * Gradle paths of [layer] in every detector unit, sorted by unit name.
 *
 * Detector units are discovered from `feature/<name>/detector` the way settings discover modules,
 * so a composition root that depends on every detector never needs an edit when one is added.
 */
fun Project.detectorModules(layer: String): List<String> =
    rootDir.resolve("feature").listFiles().orEmpty()
        .filter { unit ->
            unit.resolve("$DETECTOR_LAYER/build.gradle.kts").isFile && unit.resolve("$layer/build.gradle.kts").isFile
        }
        .map { unit -> ":feature:${unit.name}:$layer" }
        .sorted()

/** The opt-in marker of a detector's own typed object, declared in `:core:detector`. */
const val DETECTOR_SPECIFIC_API: String = "com.kaizehuang.vernier.core.detector.DetectorSpecificApi"

/**
 * Whether this module composes detectors through their typed objects: a detector unit's detector
 * and ui layers, the SDK runtime that catalogs them and the app. They opt in to
 * [DETECTOR_SPECIFIC_API] as a whole, since they change together with the detectors; an SDK host
 * opts in where it uses one detector's typed models.
 */
internal fun Project.composesDetectors(): Boolean =
    path in setOf(":sdk:runtime", ":app") || path in detectorModules(DETECTOR_LAYER) || path in detectorModules("ui")
