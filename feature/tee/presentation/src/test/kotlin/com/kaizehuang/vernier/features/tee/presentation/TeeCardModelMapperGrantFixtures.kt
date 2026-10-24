package com.kaizehuang.vernier.features.tee.presentation

import com.kaizehuang.vernier.features.tee.domain.TeeGrantEvidence
import com.kaizehuang.vernier.features.tee.domain.TeeGrantProbe

internal fun grant(probe: TeeGrantProbe, text: String) = TeeGrantEvidence(
    probe = probe,
    namesKeyVisibility = text.contains("key visibility", ignoreCase = true),
    namesMissingKey = text.contains("KEY_NOT_FOUND", ignoreCase = true),
)
