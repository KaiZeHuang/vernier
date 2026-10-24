package com.kaizehuang.vernier.features.heapresidue.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.heapresidue.detector.HeapResidueDetector
import com.kaizehuang.vernier.features.heapresidue.ui.card.HeapResidueDetectorCard

/** The Zygote heap residue card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(HeapResidueDetector) { model -> HeapResidueDetectorCard(model = model) }
