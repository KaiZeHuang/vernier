package com.kaizehuang.vernier.features.customrom.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CustomRomBootloaderFindingTest {

    @Test
    fun `an explicitly unlocked value is a finding`() {
        listOf("0", " unlocked ", "FALSE").forEach { value ->
            assertEquals(value, "Unlocked bootloader", bootloaderUnlockFinding(value)?.summary)
        }
    }

    @Test
    fun `a locked, empty, unreadable or unknown value is not`() {
        listOf("1", "locked", "true", "", "   ", null, "2").forEach { value ->
            assertNull("$value", bootloaderUnlockFinding(value))
        }
    }
}
