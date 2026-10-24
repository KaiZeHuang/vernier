package com.kaizehuang.vernier.features.selinux.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.selinux.domain.SelinuxCheckResult
import com.kaizehuang.vernier.features.selinux.domain.SelinuxMode
import com.kaizehuang.vernier.features.selinux.domain.SelinuxOracle
import com.kaizehuang.vernier.features.selinux.domain.SelinuxProcAttrCurrentVerdict
import com.kaizehuang.vernier.features.selinux.domain.SelinuxReport
import com.kaizehuang.vernier.features.selinux.domain.SelinuxStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SelinuxProcAttrCurrentPresentationTest {

    private val rawRecord = "Controls=CONTROLS_PASSED raw=control0 malformed(open_errno=0, write_errno=22, returned=-1) | " +
        "KernelSU=NORMAL_EINVAL target=u:r:ksu:s0 raw=round0(open_errno=0, write_errno=22, returned=-1)"

    @Test
    fun `tested contexts not recognized add one concise info line and no clean claim`() {
        val model = SelinuxCardModelMapper().map(report(SelinuxProcAttrCurrentVerdict.NOT_RECOGNIZED))
        val lines = model.impactItems.filter { it.text.contains("attr/current writes") }

        assertEquals(1, lines.size)
        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), lines.single().status)
        assertTrue(lines.single().text.contains("did not recognize the tested privileged contexts"))
        assertTrue(lines.single().text.contains("hidden backup policy"))
        assertTrue(model.impactItems.none { it.text.contains("round0(") })
        assertEquals(DetectorStatus.info(InfoKind.SUPPORT), model.methodRows.single().status)
    }

    @Test
    fun `failed collection is a named coverage gap rather than the raw transaction record`() {
        for (verdict in listOf(
            SelinuxProcAttrCurrentVerdict.PERMISSION_LIMITED,
            SelinuxProcAttrCurrentVerdict.UNSUPPORTED,
            SelinuxProcAttrCurrentVerdict.UNAVAILABLE,
            SelinuxProcAttrCurrentVerdict.INCONCLUSIVE,
        )) {
            val model = SelinuxCardModelMapper().map(report(verdict))
            val line = model.impactItems.single { it.text.contains("attr/current writes") }

            assertEquals(DetectorStatus.info(InfoKind.SUPPORT), line.status)
            assertEquals(
                "Controlled app_zygote attr/current writes: ${verdict.label}. This is a coverage gap, not a clean result.",
                line.text,
            )
            assertTrue(model.impactItems.none { it.text.contains("round0(") })
            assertEquals(DetectorStatus.allClear(), model.status)
        }
    }

    private fun report(verdict: SelinuxProcAttrCurrentVerdict) = SelinuxReport(
        stage = SelinuxStage.READY,
        mode = SelinuxMode.ENFORCING,
        resolvedStatusLabel = "Enforcing",
        filesystemMounted = true,
        paradoxDetected = false,
        methods = listOf(
            SelinuxCheckResult(
                method = SelinuxOracle.PROC_ATTR_CURRENT_WRITE.label,
                status = verdict.label,
                isSecure = null,
                permissionDenied = verdict == SelinuxProcAttrCurrentVerdict.PERMISSION_LIMITED,
                details = rawRecord,
                oracle = SelinuxOracle.PROC_ATTR_CURRENT_WRITE,
                attrCurrentVerdict = verdict,
            ),
        ),
        processContext = "u:r:untrusted_app:s0:c1,c2",
        contextType = "untrusted_app",
        policyAnalysis = null,
        auditIntegrity = null,
        androidVersion = "16",
        apiLevel = 36,
    )
}
