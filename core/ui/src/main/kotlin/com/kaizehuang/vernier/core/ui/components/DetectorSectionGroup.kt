package com.kaizehuang.vernier.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveSectionGroup

@DslMarker
public annotation class DetectorSectionGroupDsl

@DetectorSectionGroupDsl
public class DetectorSectionGroupScope internal constructor() {
    internal data class Entry(
        val content: @Composable () -> Unit,
    )

    internal val entries = mutableListOf<Entry>()

    public fun item(
        visible: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        if (visible) entries += Entry(content)
    }
}

/** Vernier DSL facade; cross-skin segmented/grouped geometry lives in uihelper. */
@Composable
public fun DetectorSectionGroup(
    modifier: Modifier = Modifier,
    content: DetectorSectionGroupScope.() -> Unit,
) {
    val entries = DetectorSectionGroupScope().apply(content).entries
    AdaptiveSectionGroup(modifier = modifier) {
        entries.forEach { entry ->
            item { entry.content() }
        }
    }
}
