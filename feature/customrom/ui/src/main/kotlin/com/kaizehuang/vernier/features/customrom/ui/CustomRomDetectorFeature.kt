package com.kaizehuang.vernier.features.customrom.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.customrom.detector.CustomRomDetector
import com.kaizehuang.vernier.features.customrom.ui.card.CustomRomDetectorCard

/** The Custom ROM card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(CustomRomDetector) { model -> CustomRomDetectorCard(model = model) }
