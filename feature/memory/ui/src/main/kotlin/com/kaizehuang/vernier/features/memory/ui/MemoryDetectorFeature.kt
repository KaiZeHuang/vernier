package com.kaizehuang.vernier.features.memory.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.memory.detector.MemoryDetector
import com.kaizehuang.vernier.features.memory.ui.card.MemoryDetectorCard

/** The Memory card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(MemoryDetector) { model -> MemoryDetectorCard(model = model) }
