package com.kaizehuang.vernier.features.nativeroot.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.nativeroot.detector.NativeRootDetector
import com.kaizehuang.vernier.features.nativeroot.ui.card.NativeRootDetectorCard

/** The Native Root card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(NativeRootDetector, consentCards = listOf(srcuTimingConsentCard)) { model -> NativeRootDetectorCard(model = model) }
