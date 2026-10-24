package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsItemShapesTest {
    private val inner = CornerSize(4.dp)
    private val outer = CornerSize(32.dp)

    @Test
    fun onlyTheGroupEdgesTakeTheOuterCorners() {
        assertCorners(segmentShape(0, 3, inner, outer), top = outer, bottom = inner)
        assertCorners(segmentShape(1, 3, inner, outer), top = inner, bottom = inner)
        assertCorners(segmentShape(2, 3, inner, outer), top = inner, bottom = outer)
    }

    @Test
    fun aSingleRowIsRoundedOnEverySide() {
        assertCorners(segmentShape(0, 1, inner, outer), top = outer, bottom = outer)
    }

    private fun assertCorners(shape: CornerBasedShape, top: CornerSize, bottom: CornerSize) {
        assertEquals(top, shape.topStart)
        assertEquals(top, shape.topEnd)
        assertEquals(bottom, shape.bottomStart)
        assertEquals(bottom, shape.bottomEnd)
    }
}
