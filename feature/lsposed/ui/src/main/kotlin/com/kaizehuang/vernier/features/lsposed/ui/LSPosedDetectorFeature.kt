package com.kaizehuang.vernier.features.lsposed.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.lsposed.detector.LSPosedDetector
import com.kaizehuang.vernier.features.lsposed.ui.card.LSPosedDetectorCard

/** The LSPosed card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(LSPosedDetector) { model -> LSPosedDetectorCard(model = model) }
