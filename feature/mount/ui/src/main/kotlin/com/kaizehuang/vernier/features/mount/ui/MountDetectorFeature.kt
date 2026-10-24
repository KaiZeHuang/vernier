package com.kaizehuang.vernier.features.mount.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.mount.detector.MountDetector
import com.kaizehuang.vernier.features.mount.ui.card.MountDetectorCard

/** The Mount card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(MountDetector) { model -> MountDetectorCard(model = model) }
