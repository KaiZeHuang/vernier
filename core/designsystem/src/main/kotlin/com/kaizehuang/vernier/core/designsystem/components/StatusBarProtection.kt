package com.kaizehuang.vernier.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme

/**
 * Keeps the status bar icons legible over content that scrolls beneath the transparent status bar
 * of an edge-to-edge screen: a band of the page color behind the status bar. Draw it above the
 * scrolling content, aligned to the top.
 */
@Composable
public fun StatusBarProtection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsTopHeight(WindowInsets.statusBars)
            .background(SurfaceTheme.palette.groupedBackground),
    )
}
