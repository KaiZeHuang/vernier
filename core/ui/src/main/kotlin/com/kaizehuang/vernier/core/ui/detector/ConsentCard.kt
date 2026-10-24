package com.kaizehuang.vernier.core.ui.detector

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.kaizehuang.vernier.core.detector.DetectorConsent

/**
 * How the application asks for one [DetectorConsent]: as a card on the startup policy screen before
 * the first scan, and as a setting afterwards. The detector's ui module supplies the icons and words,
 * so the application shows every consent without naming the detector that asks for it.
 */
@Immutable
public class ConsentCard(
    public val consent: DetectorConsent,
    public val prompt: ConsentPrompt,
    public val setting: ConsentSetting,
)

/** The consent's card on the startup policy screen, for each decision the user can have made. */
@Immutable
public class ConsentPrompt(
    public val icon: ImageVector,
    @param:StringRes public val title: Int,
    @param:StringRes public val headline: Int,
    @param:StringRes public val detail: Int,
    @param:StringRes public val allowLabel: Int,
    @param:StringRes public val declineLabel: Int,
    @param:StringRes public val grantedHeadline: Int,
    @param:StringRes public val grantedDetail: Int,
    @param:StringRes public val declinedStatus: Int,
    @param:StringRes public val declinedHeadline: Int,
    @param:StringRes public val declinedDetail: Int,
)

/** The consent's switch in settings; it is on only while the consent is granted. */
@Immutable
public class ConsentSetting(
    public val icon: ImageVector,
    @param:StringRes public val title: Int,
    @param:StringRes public val summary: Int,
    @param:StringRes public val footer: Int,
)
