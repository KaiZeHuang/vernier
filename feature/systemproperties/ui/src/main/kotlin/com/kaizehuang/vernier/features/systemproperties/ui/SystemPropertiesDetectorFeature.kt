package com.kaizehuang.vernier.features.systemproperties.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.systemproperties.detector.SystemPropertiesDetector
import com.kaizehuang.vernier.features.systemproperties.ui.card.SystemPropertiesDetectorCard

/** The System Properties card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(SystemPropertiesDetector) { model -> SystemPropertiesDetectorCard(model = model) }
