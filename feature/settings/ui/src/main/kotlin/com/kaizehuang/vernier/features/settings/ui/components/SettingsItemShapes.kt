package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ListItemShapes
import androidx.compose.runtime.Composable
import io.github.xiaotong6666.uihelper.adaptive.NativeSettingsItemShapes
import io.github.xiaotong6666.uihelper.adaptive.nativeSettingsSegmentShape

/**
 * Shapes for the [index]th of [count] rows of a settings group. The group's outer corners match the
 * app's cards in every state; the inner corners follow the theme corners Material gives each
 * list-item state, so a pressed row still rounds away from its neighbours.
 */
@Composable
internal fun settingsItemShapes(index: Int, count: Int): ListItemShapes {
    return NativeSettingsItemShapes(index = index, count = count)
}

/** Compatibility facade for the existing geometry unit test; implementation lives in uihelper. */
internal fun segmentShape(
    index: Int,
    count: Int,
    inner: CornerSize,
    outer: CornerSize,
): CornerBasedShape = nativeSettingsSegmentShape(index, count, inner, outer)
