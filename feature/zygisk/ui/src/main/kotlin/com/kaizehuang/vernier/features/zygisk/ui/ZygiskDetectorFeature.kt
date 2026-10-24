package com.kaizehuang.vernier.features.zygisk.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.zygisk.detector.ZygiskDetector
import com.kaizehuang.vernier.features.zygisk.ui.card.ZygiskDetectorCard

/** The Zygisk card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(ZygiskDetector) { model -> ZygiskDetectorCard(model = model) }
