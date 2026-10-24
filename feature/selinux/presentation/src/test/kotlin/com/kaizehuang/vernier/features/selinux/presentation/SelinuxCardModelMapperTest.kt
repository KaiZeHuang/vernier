package com.kaizehuang.vernier.features.selinux.presentation

import com.kaizehuang.vernier.core.evidence.DetectionSeverity
import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.features.selinux.domain.SelinuxAuditEvidence
import com.kaizehuang.vernier.features.selinux.domain.SelinuxAuditIntegrityAnalysis
import com.kaizehuang.vernier.features.selinux.domain.SelinuxAuditIntegrityState
import com.kaizehuang.vernier.features.selinux.domain.SelinuxAuditNote
import com.kaizehuang.vernier.features.selinux.domain.SelinuxAuditNoteKind
import com.kaizehuang.vernier.features.selinux.domain.SelinuxMode
import com.kaizehuang.vernier.features.selinux.domain.SelinuxReport
import com.kaizehuang.vernier.features.selinux.domain.SelinuxStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelinuxCardModelMapperTest {

    private val mapper = SelinuxCardModelMapper()

    @Test
    fun `audit exposure maps to warning instead of root claim`() {
        val model = mapper.map(
            SelinuxReport(
                stage = SelinuxStage.READY,
                mode = SelinuxMode.ENFORCING,
                resolvedStatusLabel = "Enforcing",
                filesystemMounted = true,
                paradoxDetected = false,
                methods = emptyList(),
                processContext = "u:r:untrusted_app:s0:c1,c2",
                contextType = "untrusted_app",
                policyAnalysis = null,
                auditIntegrity = SelinuxAuditIntegrityAnalysis(
                    state = SelinuxAuditIntegrityState.EXPOSED,
                    residueHits = emptyList(),
                    runtimeHits = emptyList(),
                    sideChannelHits = listOf(
                        SelinuxAuditEvidence(
                            label = "AVC denial leak",
                            value = "comm=su",
                            detail = "avc: denied ...",
                        ),
                    ),
                    suspiciousActorHits = listOf(
                        SelinuxAuditEvidence(
                            label = "su-related AVC",
                            value = "comm=su",
                            detail = "avc: denied ...",
                        ),
                    ),
                    logcatChecked = true,
                    directProbeUsed = true,
                    notes = listOf(
                        SelinuxAuditNote(
                            SelinuxAuditNoteKind.SIDE_CHANNEL_LEAK,
                            "A direct libselinux callback probe and app-visible auditd event logs both observed the same nonce-tagged AVC denial. Treat this as audit side-channel leakage, not direct root-process proof.",
                        ),
                    ),
                ),
                androidVersion = "15",
                apiLevel = 35,
            ),
        )

        assertEquals(DetectorStatus.warning(), model.status)
        assertEquals("Enforcing with audit exposure", model.verdict)
        assertTrue(model.auditRows.any { it.label == "AVC side-channel" && it.value == "1 hit(s)" })
        assertTrue(model.auditRows.any { it.label == "su-related AVC" && it.value == "1 hit(s)" })
        assertTrue(model.auditNotes.any { it.text.contains("not direct root-process proof") })
    }

    @Test
    fun `residue locations this app cannot stat are not shown as clear`() {
        fun residueRow(observable: Boolean) = mapper.map(
            SelinuxReport(
                stage = SelinuxStage.READY,
                mode = SelinuxMode.ENFORCING,
                resolvedStatusLabel = "Enforcing",
                filesystemMounted = true,
                paradoxDetected = false,
                methods = emptyList(),
                processContext = "u:r:untrusted_app:s0:c1,c2",
                contextType = "untrusted_app",
                policyAnalysis = null,
                auditIntegrity = SelinuxAuditIntegrityAnalysis(
                    state = SelinuxAuditIntegrityState.INCONCLUSIVE,
                    residueHits = emptyList(),
                    residueObservable = observable,
                    runtimeHits = emptyList(),
                    sideChannelHits = emptyList(),
                    logcatChecked = false,
                    notes = emptyList(),
                ),
                androidVersion = "16",
                apiLevel = 36,
            ),
        ).auditRows.first { it.label == "Residue paths" }

        assertEquals("Not observable", residueRow(observable = false).value)
        assertEquals(DetectionSeverity.INFO, residueRow(observable = false).status.severity)
        assertEquals("None", residueRow(observable = true).value)
        assertEquals(DetectionSeverity.ALL_CLEAR, residueRow(observable = true).status.severity)
    }

    @Test
    fun `inconclusive audit scan stays support instead of clear`() {
        val model = mapper.map(
            SelinuxReport(
                stage = SelinuxStage.READY,
                mode = SelinuxMode.ENFORCING,
                resolvedStatusLabel = "Enforcing",
                filesystemMounted = true,
                paradoxDetected = false,
                methods = emptyList(),
                processContext = "u:r:untrusted_app:s0:c1,c2",
                contextType = "untrusted_app",
                policyAnalysis = null,
                auditIntegrity = SelinuxAuditIntegrityAnalysis(
                    state = SelinuxAuditIntegrityState.INCONCLUSIVE,
                    residueHits = emptyList(),
                    runtimeHits = emptyList(),
                    sideChannelHits = emptyList(),
                    suspiciousActorHits = emptyList(),
                    logcatChecked = true,
                    directProbeUsed = true,
                    notes = listOf(
                        SelinuxAuditNote(
                            SelinuxAuditNoteKind.EVENTS_NOT_GUARANTEED,
                            "AOSP does not guarantee that every device emits or exposes matching audit events to app-visible log readers, so this remains non-proving.",
                        ),
                    ),
                ),
                androidVersion = "16",
                apiLevel = 36,
            ),
        )

        val runtimeRow = model.auditRows.first { it.label == "Runtime markers" }
        val avcRow = model.auditRows.first { it.label == "AVC side-channel" }
        val suAvcRow = model.auditRows.first { it.label == "su-related AVC" }

        assertEquals("Inconclusive", model.auditRows.first { it.label == "Surface" }.value)
        assertEquals("Not observed", runtimeRow.value)
        assertEquals("Not observed", avcRow.value)
        assertEquals("Not observed", suAvcRow.value)
        assertFalse(runtimeRow.status == DetectorStatus.allClear())
        assertFalse(avcRow.status == DetectorStatus.allClear())
        assertTrue(model.summary.contains("non-proving"))
    }
}
