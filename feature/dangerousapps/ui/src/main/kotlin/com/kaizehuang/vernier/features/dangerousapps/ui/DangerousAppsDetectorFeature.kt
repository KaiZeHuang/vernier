package com.kaizehuang.vernier.features.dangerousapps.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.dangerousapps.detector.DangerousAppsDetector
import com.kaizehuang.vernier.features.dangerousapps.ui.card.DangerousAppsDetectorCard

/** The Dangerous Apps card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(DangerousAppsDetector) { model -> DangerousAppsDetectorCard(model = model) }
