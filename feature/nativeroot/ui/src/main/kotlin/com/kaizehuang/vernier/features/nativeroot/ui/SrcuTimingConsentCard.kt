package com.kaizehuang.vernier.features.nativeroot.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Timer
import com.kaizehuang.vernier.core.ui.detector.ConsentCard
import com.kaizehuang.vernier.core.ui.detector.ConsentPrompt
import com.kaizehuang.vernier.core.ui.detector.ConsentSetting
import com.kaizehuang.vernier.features.nativeroot.detector.NativeRootSrcuTimingConsent

internal val srcuTimingConsentCard = ConsentCard(
    consent = NativeRootSrcuTimingConsent,
    prompt = ConsentPrompt(
        icon = Icons.Rounded.Timer,
        title = R.string.srcu_timing_title,
        headline = R.string.srcu_timing_headline,
        detail = R.string.srcu_timing_detail,
        allowLabel = R.string.srcu_timing_allow,
        declineLabel = R.string.srcu_timing_decline,
        grantedHeadline = R.string.srcu_timing_granted,
        grantedDetail = R.string.srcu_timing_detail,
        declinedStatus = R.string.srcu_timing_disabled,
        declinedHeadline = R.string.srcu_timing_decline,
        declinedDetail = R.string.srcu_timing_declined_detail,
    ),
    setting = ConsentSetting(
        icon = Icons.Rounded.Timer,
        title = R.string.srcu_timing_title,
        summary = R.string.srcu_timing_headline,
        footer = R.string.srcu_timing_detail,
    ),
)
