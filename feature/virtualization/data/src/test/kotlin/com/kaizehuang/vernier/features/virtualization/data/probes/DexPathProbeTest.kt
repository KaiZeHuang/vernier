package com.kaizehuang.vernier.features.virtualization.data.probes

import com.kaizehuang.vernier.features.virtualization.domain.VirtualizationSignalSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DexPathProbeTest {

    private val probe = DexPathProbe()

    @Test
    fun `host apk path hit maps to danger`() {
        val result = probe.evaluate(
            entries = listOf(
                "/data/app/com.vmos.pro/base.apk",
                "/data/app/com.kaizehuang.vernier/base.apk",
            ),
            sourceDir = "/data/app/com.kaizehuang.vernier/base.apk",
            splitSourceDirs = emptyList(),
            packageName = "com.kaizehuang.vernier",
        )

        assertTrue(
            result.signals.any {
                it.label == "Host dex path" &&
                        it.severity == VirtualizationSignalSeverity.DANGER
            },
        )
        assertTrue(result.hostPathHit)
    }

    @Test
    fun `prepended third party dex maps to danger`() {
        val result = probe.evaluate(
            entries = listOf(
                "/data/local/tmp/injected.jar",
                "/data/app/com.kaizehuang.vernier/base.apk",
            ),
            sourceDir = "/data/app/com.kaizehuang.vernier/base.apk",
            splitSourceDirs = emptyList(),
            packageName = "com.kaizehuang.vernier",
        )

        assertTrue(
            result.signals.any {
                it.label == "Prepended third-party dex" &&
                        it.severity == VirtualizationSignalSeverity.DANGER
            },
        )
    }

    @Test
    fun `split source mismatch only maps to warning`() {
        val result = probe.evaluate(
            entries = listOf("/data/app/com.kaizehuang.vernier/base.apk"),
            sourceDir = "/data/app/com.kaizehuang.vernier/base.apk",
            splitSourceDirs = listOf("/data/app/com.kaizehuang.vernier/split_config.arm64_v8a.apk"),
            packageName = "com.kaizehuang.vernier",
        )

        assertEquals(1, result.hitCount)
        assertTrue(
            result.signals.any {
                it.label == "Classpath/source mismatch" &&
                        it.severity == VirtualizationSignalSeverity.WARNING
            },
        )
    }

    @Test
    fun `system and apex paths are filtered`() {
        val result = probe.evaluate(
            entries = listOf(
                "/system/framework/framework.jar",
                "/apex/com.android.art/javalib/core-oj.jar",
                "/data/app/com.kaizehuang.vernier/base.apk",
            ),
            sourceDir = "/data/app/com.kaizehuang.vernier/base.apk",
            splitSourceDirs = emptyList(),
            packageName = "com.kaizehuang.vernier",
        )

        assertEquals(1, result.entryCount)
        assertTrue(result.signals.isEmpty())
    }

    @Test
    fun `own overlay dex path is ignored`() {
        val result = probe.evaluate(
            entries = listOf(
                "/data/data/com.kaizehuang.vernier/code_cache/.overlay/base.apk/classes10.dex",
                "/data/app/com.kaizehuang.vernier/base.apk",
            ),
            sourceDir = "/data/app/com.kaizehuang.vernier/base.apk",
            splitSourceDirs = emptyList(),
            packageName = "com.kaizehuang.vernier",
        )

        assertEquals(1, result.entryCount)
        assertTrue(result.signals.isEmpty())
        assertTrue(!result.hostPathHit)
    }
}
