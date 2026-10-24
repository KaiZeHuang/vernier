package com.kaizehuang.vernier.capability.selinuxpolicy.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SelinuxPolicyloadSeqnoProbeTest {

    private val probe = SelinuxPolicyloadSeqnoProbe()

    @Test
    fun `matching status policyload and access seqno is clean`() {
        val result = probe.interpret(
            status = status(sequence = 12, policyload = 9),
            access = access(seqno = 9),
        )

        assertEquals(SelinuxPolicyloadSeqnoState.CLEAN, result.state)
        assertTrue(result.available)
        assertTrue(result.probeAttempted)
        assertEquals(12L, result.statusSequence)
        assertEquals(9L, result.statusPolicyload)
        assertEquals(9L, result.accessSeqno)
        assertEquals(2, result.processClass)
    }

    @Test
    fun `nonzero sequence with zero policyload and positive access seqno is suspicious`() {
        val result = probe.interpret(
            status = status(sequence = 4, policyload = 0),
            access = access(seqno = 9),
        )

        assertEquals(SelinuxPolicyloadSeqnoState.SUSPICIOUS, result.state)
        assertTrue(result.available)
        assertEquals(0L, result.statusPolicyload)
        assertEquals(9L, result.accessSeqno)
    }

    @Test
    fun `fresh zero status page stays inconclusive instead of suspicious`() {
        val result = probe.interpret(
            status = status(sequence = 0, policyload = 0),
            access = access(seqno = 9),
        )

        assertEquals(SelinuxPolicyloadSeqnoState.INCONCLUSIVE, result.state)
        assertTrue(result.available)
        assertEquals(0L, result.statusSequence)
        assertEquals(0L, result.statusPolicyload)
        assertEquals(9L, result.accessSeqno)
    }

    @Test
    fun `odd status sequence stays inconclusive instead of suspicious`() {
        val result = probe.interpret(
            status = status(sequence = 5, policyload = 0),
            access = access(seqno = 9),
        )

        assertEquals(SelinuxPolicyloadSeqnoState.INCONCLUSIVE, result.state)
        assertTrue(result.available)
        assertEquals(5L, result.statusSequence)
        assertEquals(0L, result.statusPolicyload)
        assertEquals(9L, result.accessSeqno)
    }

    @Test
    fun `positive policyload mismatch is suspicious`() {
        val result = probe.interpret(
            status = status(sequence = 12, policyload = 7),
            access = access(seqno = 9),
        )

        assertEquals(SelinuxPolicyloadSeqnoState.SUSPICIOUS, result.state)
        assertTrue(result.available)
        assertEquals(7L, result.statusPolicyload)
        assertEquals(9L, result.accessSeqno)
    }

    @Test
    fun `a status page that killed the child is a finding and never queries in process`() {
        var accessQueries = 0
        val result = probe.inspect(
            statusPage = SelinuxStatusPageResult.Faulted(
                signal = 9,
                notes = listOf("Child was killed by SIGKILL on the first read of the mapping."),
            ),
            queryAccess = {
                accessQueries += 1
                access(seqno = 9)
            },
        )

        assertEquals(0, accessQueries)
        assertEquals(SelinuxPolicyloadSeqnoState.STATUS_PAGE_FAULTED, result.state)
        assertTrue(result.available)
        assertTrue(result.probeAttempted)
        assertNull(result.failureReason)
        assertNull(result.statusPolicyload)
        assertTrue(result.notes.contains("Child was killed by SIGKILL on the first read of the mapping."))
    }

    @Test
    fun `an unread status page is unavailable with the probe's reason`() {
        var accessQueries = 0
        val unknown = SelinuxStatusPageResult.Inconclusive(
            reason = "Status page child did not finish within 1000 ms and was stopped.",
            attempted = true,
        )
        val notForked = SelinuxStatusPageResult.Inconclusive(
            reason = "Status page child could not be started (errno=11); status page probe not run.",
            attempted = false,
        )
        val unmappable = SelinuxStatusPageResult.Unavailable(
            reason = "open of /sys/fs/selinux/status failed (errno=13).",
        )

        val results = listOf(unknown, notForked, unmappable).map { statusPage ->
            probe.inspect(statusPage) {
                accessQueries += 1
                access(seqno = 9)
            }
        }

        assertEquals(0, accessQueries)
        results.forEach { assertEquals(SelinuxPolicyloadSeqnoState.UNAVAILABLE, it.state) }
        assertEquals(listOf(unknown.reason, notForked.reason, unmappable.reason), results.map { it.failureReason })
        assertEquals(listOf(true, false, true), results.map { it.probeAttempted })
    }

    @Test
    fun `an intact status page is compared from the header the child read`() {
        val result = probe.inspect(
            statusPage = SelinuxStatusPageResult.Intact(header = status(sequence = 12, policyload = 9)),
            queryAccess = { access(seqno = 9) },
        )

        assertEquals(SelinuxPolicyloadSeqnoState.CLEAN, result.state)
        assertEquals(12L, result.statusSequence)
        assertEquals(9L, result.statusPolicyload)
        assertEquals(9L, result.accessSeqno)
    }

    private fun status(
        sequence: Long,
        policyload: Long,
    ) = SelinuxStatusHeader(
        version = 1,
        sequence = sequence,
        enforcing = 1,
        policyload = policyload,
        denyUnknown = 0,
    )

    private fun access(
        seqno: Long,
    ) = SelinuxPolicyloadSeqnoProbe.AccessDecision(
        processClass = 2,
        seqno = seqno,
    )
}
