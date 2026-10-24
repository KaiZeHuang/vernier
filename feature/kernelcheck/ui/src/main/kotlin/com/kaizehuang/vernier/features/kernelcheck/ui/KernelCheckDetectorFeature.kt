package com.kaizehuang.vernier.features.kernelcheck.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.kernelcheck.detector.KernelCheckDetector
import com.kaizehuang.vernier.features.kernelcheck.ui.card.KernelCheckDetectorCard

/** The Kernel Check card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(KernelCheckDetector) { model -> KernelCheckDetectorCard(model = model) }
