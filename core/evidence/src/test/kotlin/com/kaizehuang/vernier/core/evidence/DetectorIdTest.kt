package com.kaizehuang.vernier.core.evidence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DetectorIdTest {

    @Test
    fun `accepts the identifiers detectors already use`() {
        listOf("su", "tee", "native_root", "play_integrity_fix", "system_properties").forEach { value ->
            assertEquals(value, DetectorId(value).value)
        }
    }

    @Test
    fun `rejects identifiers that look like presentation text`() {
        listOf("", "Native Root", "native-root", "_su", "su_", "native__root", "TEE").forEach { value ->
            assertThrows(IllegalArgumentException::class.java) { DetectorId(value) }
        }
    }
}
