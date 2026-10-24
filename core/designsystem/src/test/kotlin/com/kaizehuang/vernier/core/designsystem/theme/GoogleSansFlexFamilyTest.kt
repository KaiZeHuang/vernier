package com.kaizehuang.vernier.core.designsystem.theme

import androidx.compose.ui.text.font.FontListFontFamily
import org.junit.Assert.assertEquals
import org.junit.Test

class GoogleSansFlexFamilyTest {
    @Test
    fun everyWeightSetsTheWeightAxisToThatWeight() {
        val fonts = (GoogleSansFlexFamily as FontListFontFamily).fonts

        assertEquals((400..900 step 100).toList(), fonts.map { it.weight.weight })
        fonts.forEach { font ->
            val settings = font.variationSettings.settings
            assertEquals("${font.weight}", listOf("wght"), settings.map { it.axisName })
            assertEquals(
                "${font.weight}",
                font.weight.weight.toFloat(),
                settings.single().toVariationValue(null),
                0f,
            )
        }
    }
}
