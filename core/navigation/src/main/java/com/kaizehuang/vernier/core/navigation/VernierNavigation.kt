package com.kaizehuang.vernier.core.navigation

import androidx.compose.runtime.Composable
import io.github.xiaotong6666.uihelper.navigation3.Navigator
import io.github.xiaotong6666.uihelper.navigation3.logicalBackSwipeDirection
import io.github.xiaotong6666.uihelper.navigation3.rememberNavigator
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import top.yukonga.miuix.kmp.nav.core.NavBackStack
import top.yukonga.miuix.kmp.nav.core.NavDisplay
import top.yukonga.miuix.kmp.nav.core.NavKey
import top.yukonga.miuix.kmp.nav.transition.NavSwipeDirection

/** Tabs are pages *within* Main. Only secondary pages live in this one MIUIX Nav stack. */
@Serializable
sealed interface VernierRoute : NavKey {
    @Serializable @SerialName("main")
    data object Main : VernierRoute
    @Serializable @SerialName("licenses")
    data object Licenses : VernierRoute
}

@Composable
fun rememberVernierRoutes(): Navigator<VernierRoute> = rememberNavigator<VernierRoute>(VernierRoute.Main)

/** Non-inline bridge: the JVM-17 application never inlines MIUIX Nav's JVM-21 DSL. */
@Composable
fun VernierNavHost(
    backStack: NavBackStack,
    onBack: () -> Unit,
    main: @Composable () -> Unit,
    licenses: @Composable () -> Unit,
) {
    val dismissDirection = logicalBackSwipeDirection()
    NavDisplay(backStack = backStack, onBack = onBack) {
        entry<VernierRoute.Main>(swipeDismiss = NavSwipeDirection.None) { main() }
        entry<VernierRoute.Licenses>(swipeDismiss = dismissDirection) { licenses() }
    }
}
