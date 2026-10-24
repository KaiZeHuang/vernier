package com.kaizehuang.vernier.features.lsposed.data.probes

import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignalSeverity
import com.kaizehuang.vernier.testhelpers.clean.NeutralExceptionHandler
import com.kaizehuang.vernier.testhelpers.suspicious.LsposedInjectedHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LSPosedHookCallbackProbeTest {

    private val probe = LSPosedHookCallbackProbe()

    @Test
    fun `lsposed named default handler reports danger`() {
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        try {
            Thread.setDefaultUncaughtExceptionHandler(LsposedInjectedHandler())

            val result = probe.run()

            assertEquals(1, result.hitCount)
            assertEquals(LSPosedSignalSeverity.DANGER, result.signals.single().severity)
            assertTrue(result.signals.single().detail.contains("LsposedInjectedHandler"))
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(originalHandler)
        }
    }

    @Test
    fun `clean handler stays clear`() {
        val result = probe.evaluate(NeutralExceptionHandler())

        assertTrue(result.signals.isEmpty())
        assertEquals(0, result.hitCount)
    }
}
