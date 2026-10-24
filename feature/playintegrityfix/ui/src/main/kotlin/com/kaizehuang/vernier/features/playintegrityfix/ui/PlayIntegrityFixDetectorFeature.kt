package com.kaizehuang.vernier.features.playintegrityfix.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.playintegrityfix.detector.PlayIntegrityFixDetector
import com.kaizehuang.vernier.features.playintegrityfix.ui.card.PlayIntegrityFixDetectorCard

/** The Play Integrity Fix card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(PlayIntegrityFixDetector) { model -> PlayIntegrityFixDetectorCard(model = model) }
