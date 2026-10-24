package com.kaizehuang.vernier.features.settings.ui

import androidx.compose.runtime.Immutable
import com.kaizehuang.vernier.core.ui.detector.ConsentSetting

/** A detector's consent as a switch in settings. */
@Immutable
class ConsentToggle(
    val setting: ConsentSetting,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
)
