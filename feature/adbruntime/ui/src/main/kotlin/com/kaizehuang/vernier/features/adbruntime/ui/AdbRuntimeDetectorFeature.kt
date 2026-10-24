package com.kaizehuang.vernier.features.adbruntime.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.adbruntime.detector.AdbRuntimeDetector
import com.kaizehuang.vernier.features.adbruntime.ui.card.AdbRuntimeDetectorCard

/** The ADB Runtime card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(AdbRuntimeDetector) { model -> AdbRuntimeDetectorCard(model = model) }
