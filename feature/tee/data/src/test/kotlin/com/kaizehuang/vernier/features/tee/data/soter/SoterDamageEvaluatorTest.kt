package com.kaizehuang.vernier.features.tee.data.soter

import com.kaizehuang.vernier.features.tee.domain.TeeSignalLevel
import com.kaizehuang.vernier.features.tee.domain.TeeSoterAnomaly
import com.kaizehuang.vernier.features.tee.domain.TeeSoterAnomalyKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SoterDamageEvaluatorTest {

    private val evaluator = SoterDamageEvaluator()

    @Test
    fun `service failure skips probe without warning`() {
        val state = evaluator.evaluate(
            serviceReachable = false,
            keyPrepared = false,
            signSessionAvailable = false,
            errorMessage = "skipped",
        )

        assertFalse(state.serviceReachable)
        assertFalse(state.damaged)
        assertTrue(state.summary.contains("soter", ignoreCase = true))
        assertTrue(state.summary.contains("skipped", ignoreCase = true))
    }

    @Test
    fun `key or signing failure becomes damaged`() {
        val state = evaluator.evaluate(
            serviceReachable = true,
            keyPrepared = true,
            signSessionAvailable = false,
            errorMessage = "sign failed",
        )

        assertFalse(state.available)
        assertTrue(state.damaged)
        assertTrue(state.summary.contains("soter", ignoreCase = true))
    }

    @Test
    fun `successful sequence stays available`() {
        val state = evaluator.evaluate(
            serviceReachable = true,
            keyPrepared = true,
            signSessionAvailable = true,
            errorMessage = null,
        )

        assertTrue(state.available)
        assertFalse(state.damaged)
        assertEquals(
            "Soter checks succeeded: Treble service was reachable and ASK/AuthKey/initSigh all succeeded.",
            state.summary,
        )
    }

    @Test
    fun `abnormal environment stays warning without damage`() {
        val state = evaluator.evaluate(
            serviceReachable = false,
            keyPrepared = false,
            signSessionAvailable = false,
            errorMessage = "skipped",
            abnormalEnvironment = true,
        )

        assertFalse(state.available)
        assertFalse(state.damaged)
        assertTrue(state.abnormalEnvironment)
        assertTrue(state.summary.contains("abnormal soter environment", ignoreCase = true))
    }

    @Test
    fun `anomaly keeps available soter available but reports evidence`() {
        val state = evaluator.evaluate(
            serviceReachable = true,
            keyPrepared = true,
            signSessionAvailable = true,
            errorMessage = null,
            anomalies = listOf(relayAnomaly),
        )

        assertTrue(state.available)
        assertFalse(state.damaged)
        assertEquals(listOf(relayAnomaly), state.anomalies)
        assertEquals(
            "Soter ASK/AuthKey/initSigh calls succeeded, but the replies need review: relay cpu_id in ASK.",
            state.summary,
        )
    }

    @Test
    fun `anomaly is appended to a damaged summary`() {
        val state = evaluator.evaluate(
            serviceReachable = true,
            keyPrepared = true,
            signSessionAvailable = false,
            errorMessage = "Soter signing resultCode=7",
            anomalies = listOf(relayAnomaly),
        )

        assertTrue(state.damaged)
        assertEquals("Soter signing resultCode=7. Replies need review: relay cpu_id in ASK.", state.summary)
    }

    private val relayAnomaly = TeeSoterAnomaly(
        TeeSoterAnomalyKind.KNOWN_RELAY_CPU_ID,
        TeeSignalLevel.WARN,
        "relay cpu_id in ASK",
    )
}
