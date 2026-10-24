package com.kaizehuang.vernier.features.rootmanagers.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.rootmanagers.detector.RootManagersDetector
import com.kaizehuang.vernier.features.rootmanagers.ui.card.RootManagersDetectorCard

/** The Root Managers card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(RootManagersDetector) { model -> RootManagersDetectorCard(model = model) }
