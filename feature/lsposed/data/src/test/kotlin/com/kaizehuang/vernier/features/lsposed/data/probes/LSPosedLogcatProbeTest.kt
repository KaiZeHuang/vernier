package com.kaizehuang.vernier.features.lsposed.data.probes

import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignalSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LSPosedLogcatProbeTest {

    @Test
    fun `explicit tags and daemon process report danger`() {
        val probe = LSPosedLogcatProbe()
        val result = probe.evaluate(
            mapOf(
                "overview" to LSPosedLogcatCommandOutput(
                    output = """
                        I/LSPosed( 123): bridge attached to target process
                    """.trimIndent(),
                ),
                "tag:LSPosed" to LSPosedLogcatCommandOutput(output = "I/LSPosed( 123): hello"),
                "tag:LSPosed-Bridge" to LSPosedLogcatCommandOutput(),
                "tag:LSPosedService" to LSPosedLogcatCommandOutput(),
                "process" to LSPosedLogcatCommandOutput(
                    output = "I/org.lsposed.daemon( 321): daemon ready",
                ),
            ),
        )

        assertTrue(result.available)
        assertTrue(result.dangerHitCount >= 2)
        assertTrue(result.signals.any { it.id == "logcat_tag_lsposed" })
        assertTrue(result.signals.any { it.id == "logcat_process_lsposed_daemon" })
    }

    @Test
    fun `pattern only hit stays warning`() {
        val probe = LSPosedLogcatProbe()
        val result = probe.evaluate(
            mapOf(
                "overview" to LSPosedLogcatCommandOutput(
                    output = "I/OtherTag( 123): Loading module from cache",
                ),
                "tag:LSPosed" to LSPosedLogcatCommandOutput(),
                "tag:LSPosed-Bridge" to LSPosedLogcatCommandOutput(),
                "tag:LSPosedService" to LSPosedLogcatCommandOutput(),
                "process" to LSPosedLogcatCommandOutput(),
            ),
        )

        assertTrue(result.available)
        assertEquals(1, result.hitCount)
        assertEquals(0, result.dangerHitCount)
        assertEquals(1, result.warningHitCount)
        assertEquals(LSPosedSignalSeverity.WARNING, result.signals.single().severity)
    }

    @Test
    fun `vernier marked selinux avc line does not become lsposed hit`() {
        val probe = LSPosedLogcatProbe()
        val result = probe.evaluate(
            mapOf(
                "overview" to LSPosedLogcatCommandOutput(
                    output = """
                        W/auditd( 123): type=1400 audit(0.0:123): avc: denied { read } for scontext=u:r:untrusted_app:s0:c1,c2 tcontext=u:object_r:lsposed_file:s0 tclass=file permissive=0 vernier_probe=dddirty_123_1
                    """.trimIndent(),
                ),
                "tag:LSPosed" to LSPosedLogcatCommandOutput(),
                "tag:LSPosed-Bridge" to LSPosedLogcatCommandOutput(),
                "tag:LSPosedService" to LSPosedLogcatCommandOutput(),
                "process" to LSPosedLogcatCommandOutput(),
            ),
        )

        assertTrue(result.available)
        assertTrue(result.signals.isEmpty())
    }

    @Test
    fun `dirty policy lsposed file avc denial does not become lsposed hit`() {
        val probe = LSPosedLogcatProbe()
        val result = probe.evaluate(
            mapOf(
                "overview" to LSPosedLogcatCommandOutput(
                    output = """
                        W/auditd( 123): type=1400 audit(0.0:123): avc: denied { read } for scontext=u:r:untrusted_app:s0:c1,c2 tcontext=u:object_r:lsposed_file:s0 tclass=file permissive=0
                    """.trimIndent(),
                ),
                "tag:LSPosed" to LSPosedLogcatCommandOutput(),
                "tag:LSPosed-Bridge" to LSPosedLogcatCommandOutput(),
                "tag:LSPosedService" to LSPosedLogcatCommandOutput(),
                "process" to LSPosedLogcatCommandOutput(),
            ),
        )

        assertTrue(result.available)
        assertTrue(result.signals.isEmpty())
    }

    @Test
    fun `selinux avc without type marker does not become direct lsposed hit`() {
        val probe = LSPosedLogcatProbe()
        val result = probe.evaluate(
            mapOf(
                "overview" to LSPosedLogcatCommandOutput(
                    output = "05-26 09:07:16.652 28028 28028 E SELinux : avc:  denied  { read } for  scontext=u:r:untrusted_app:s0 tcontext=u:object_r:lsposed_file:s0 tclass=file permissive=0",
                ),
                "tag:LSPosed" to LSPosedLogcatCommandOutput(),
                "tag:LSPosed-Bridge" to LSPosedLogcatCommandOutput(),
                "tag:LSPosedService" to LSPosedLogcatCommandOutput(),
                "process" to LSPosedLogcatCommandOutput(),
            ),
        )

        assertTrue(result.available)
        assertTrue(result.signals.isEmpty())
    }

    @Test
    fun `own app zygote preload trace does not become lsposed hit`() {
        val probe = LSPosedLogcatProbe()
        val result = probe.evaluate(
            mapOf(
                "overview" to LSPosedLogcatCommandOutput(
                    output = """
                        I/VernierZygotePreloadNative( 6602): detector: lsposed
                        I/VernierZygotePreloadNative( 6602): selinux: proc attr current write u:r:lsposed_file:s0
                    """.trimIndent(),
                ),
                "tag:LSPosed" to LSPosedLogcatCommandOutput(),
                "tag:LSPosed-Bridge" to LSPosedLogcatCommandOutput(),
                "tag:LSPosedService" to LSPosedLogcatCommandOutput(),
                "process" to LSPosedLogcatCommandOutput(),
            ),
        )

        assertTrue(result.available)
        assertTrue(result.signals.isEmpty())
    }

    @Test
    fun `line only mentioning the preload trace tag still reports`() {
        val probe = LSPosedLogcatProbe()
        val result = probe.evaluate(
            mapOf(
                "overview" to LSPosedLogcatCommandOutput(
                    output = "I/OtherTag( 123): VernierZygotePreloadNative: lsposed bridge attached",
                ),
                "tag:LSPosed" to LSPosedLogcatCommandOutput(),
                "tag:LSPosed-Bridge" to LSPosedLogcatCommandOutput(),
                "tag:LSPosedService" to LSPosedLogcatCommandOutput(),
                "process" to LSPosedLogcatCommandOutput(),
            ),
        )

        assertEquals(1, result.dangerHitCount)
        assertEquals("Logcat direct hit", result.signals.single().label)
    }

    @Test
    fun `log access denied downgrades to unavailable`() {
        val probe = LSPosedLogcatProbe(
            commandRunner = LSPosedLogcatCommandRunner { _, _ ->
                LSPosedLogcatCommandOutput(errorMessage = "Permission denied: not allowed to read logs")
            },
        )

        val result = probe.run()

        assertFalse(result.available)
        assertTrue(result.signals.isEmpty())
        assertTrue(result.failureReason?.contains("not readable") == true)
    }
}
