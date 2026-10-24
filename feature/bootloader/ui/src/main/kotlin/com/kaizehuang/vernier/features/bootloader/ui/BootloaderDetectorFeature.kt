package com.kaizehuang.vernier.features.bootloader.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.bootloader.detector.BootloaderDetector
import com.kaizehuang.vernier.features.bootloader.ui.card.BootloaderDetectorCard

/** The Bootloader card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(BootloaderDetector) { model -> BootloaderDetectorCard(model = model) }
