package com.kaizehuang.vernier.features.virtualization.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.virtualization.detector.VirtualizationDetector
import com.kaizehuang.vernier.features.virtualization.ui.card.VirtualizationDetectorCard

/** The Virtualization card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(VirtualizationDetector) { model -> VirtualizationDetectorCard(model = model) }
