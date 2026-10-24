package com.kaizehuang.vernier.features.update.presentation

import com.kaizehuang.vernier.features.update.domain.GitHubAcceleration
import java.util.Locale

/**
 * The offer goes to users who read the app in Chinese, in any script or region, and have not chosen
 * yet. The language only hints at a network where GitHub is hard to reach, so the user is asked
 * rather than switched over.
 */
fun shouldOfferGitHubAcceleration(acceleration: GitHubAcceleration, appLocale: Locale): Boolean =
    acceleration == GitHubAcceleration.UNDECIDED && appLocale.language == Locale.CHINESE.language
