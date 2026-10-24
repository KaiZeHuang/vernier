package com.kaizehuang.vernier.features.selinux.ui

import com.kaizehuang.vernier.core.ui.detector.CardDetectorFeature
import com.kaizehuang.vernier.core.ui.detector.DetectorFeature
import com.kaizehuang.vernier.features.selinux.detector.SelinuxDetector
import com.kaizehuang.vernier.features.selinux.ui.card.SelinuxDetectorCard

/** The SELinux card on the dashboard. */
val detectorFeature: DetectorFeature = CardDetectorFeature(SelinuxDetector) { model -> SelinuxDetectorCard(model = model) }
