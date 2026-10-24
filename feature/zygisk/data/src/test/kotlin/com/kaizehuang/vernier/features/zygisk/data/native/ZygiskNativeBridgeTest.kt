package com.kaizehuang.vernier.features.zygisk.data.native

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ZygiskNativeBridgeTest {

    private val bridge = ZygiskNativeBridge()

    @Test
    fun `parse decodes scalar keys and traces`() {
        val snapshot = bridge.parse(
            """
                AVAILABLE=1
                HEAP_AVAILABLE=1
                TRACER_PID=42
                STRONG_HITS=1
                HEURISTIC_HITS=2
                LINKER_HOOK_HITS=1
                VMAP_HITS=1
                HEAP_HITS=1
                TRACE=LINKER	DANGER	Linker hook	dlopen resolved outside linker @ 0x1234
                TRACE=HEAP	WARNING	Heap entropy	free-kept heap crossed threshold\nfordblks stayed elevated
            """.trimIndent(),
        )

        assertTrue(snapshot.available)
        assertTrue(snapshot.heapAvailable)
        assertEquals(42, snapshot.tracerPid)
        assertEquals(1, snapshot.strongHitCount)
        assertEquals(2, snapshot.heuristicHitCount)
        assertEquals(2, snapshot.traces.size)
        assertEquals("LINKER", snapshot.traces[0].group)
        assertTrue(snapshot.traces[1].detail.contains('\n'))
    }

    @Test
    fun `parse falls back safely on blank raw data`() {
        val snapshot = bridge.parse("")

        assertFalse(snapshot.available)
        assertFalse(snapshot.heapAvailable)
        assertEquals(0, snapshot.strongHitCount)
        assertTrue(snapshot.traces.isEmpty())
    }

    @Test
    fun `parse decodes NeoZygisk TMP_PATH marker trace`() {
        val snapshot = bridge.parse(
            """
                AVAILABLE=1
                STRONG_HITS=1
                TRACE=RUNTIME	DANGER	NeoZygisk environment marker	Environment contains NeoZygisk marker: TMP_PATH=/data/adb/neozygisk.
            """.trimIndent(),
        )

        assertTrue(snapshot.available)
        assertEquals(1, snapshot.strongHitCount)
        assertEquals(1, snapshot.traces.size)
        assertEquals("RUNTIME", snapshot.traces[0].group)
        assertEquals("DANGER", snapshot.traces[0].severity)
        assertEquals("NeoZygisk environment marker", snapshot.traces[0].label)
        assertEquals(
            "Environment contains NeoZygisk marker: TMP_PATH=/data/adb/neozygisk.",
            snapshot.traces[0].detail,
        )
    }
}
