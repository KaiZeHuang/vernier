package com.kaizehuang.vernier.features.su.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.su.detector.SuDetector
import com.kaizehuang.vernier.features.su.ui.card.SuDetectorCard

/** The SU card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(SuDetector) { model -> SuDetectorCard(model = model) }
