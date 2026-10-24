package com.kaizehuang.vernier.features.nativeroot.data.probes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThroneHuntStimulusTest {

    private val stimulus = ThroneHuntStimulus()

    @Test
    fun `mark always differs and never contains a slash`() {
        assertFalse(stimulus.MARK_A.contains('/'))
        assertFalse(stimulus.MARK_B.contains('/'))
        assertFalse(stimulus.isFrameworkValidMime(stimulus.MARK_A))
        assertFalse(stimulus.isFrameworkValidMime(stimulus.MARK_B))
    }

    @Test
    fun `valid mime values are preserved while only the sentinel flips`() {
        val current = listOf("application/vnd.vernier", stimulus.MARK_A)
        val next = stimulus.nextMark(current)

        assertEquals(listOf("application/vnd.vernier", stimulus.MARK_B), next)
        assertTrue(next.contains("application/vnd.vernier"))
        assertFalse(next.contains(stimulus.MARK_A))
    }

    @Test
    fun `unset group still produces a real change`() {
        assertEquals(listOf(stimulus.MARK_A), stimulus.nextMark(null))
        assertEquals(listOf(stimulus.MARK_A), stimulus.nextMark(emptyList()))
    }

    @Test
    fun `the choice depends only on the stored value, never on call count`() {
        repeat(4) {
            assertEquals(listOf(stimulus.MARK_A), stimulus.nextMark(listOf(stimulus.MARK_B)))
        }
        repeat(4) {
            assertEquals(listOf(stimulus.MARK_B), stimulus.nextMark(listOf(stimulus.MARK_A)))
        }
    }

    @Test
    fun `observation window outlasts the settings write delay`() {
        assertTrue(
            ThroneHuntStimulus.SETTINGS_WRITE_WINDOW_MS > ThroneHuntStimulus.SETTINGS_WRITE_DELAY_MS,
        )
        assertEquals(
            ThroneHuntStimulus.SETTINGS_WRITE_DELAY_MS + ThroneHuntStimulus.SEARCH_MANAGER_ALLOWANCE_MS,
            ThroneHuntStimulus.SETTINGS_WRITE_WINDOW_MS,
        )
    }
}
