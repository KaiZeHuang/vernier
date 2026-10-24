package com.kaizehuang.vernier.ui.shell

import androidx.compose.runtime.Composable
import com.kaizehuang.vernier.features.dashboard.ui.DashboardTopBarBrandIcon
import io.github.xiaotong6666.uihelper.chrome.MiuixLargeTitleTopBar
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@Composable
internal fun StartupMiuixTopBar(
    title: String,
    scrollBehavior: ScrollBehavior,
) {
    MiuixLargeTitleTopBar(
        title = title,
        scrollBehavior = scrollBehavior,
        expandedLeadingContent = { DashboardTopBarBrandIcon() },
        compactLeadingContent = { DashboardTopBarBrandIcon() },
    )
}
