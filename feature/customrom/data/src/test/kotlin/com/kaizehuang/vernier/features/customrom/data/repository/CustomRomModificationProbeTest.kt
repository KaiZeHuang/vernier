package com.kaizehuang.vernier.features.customrom.data.repository

import com.kaizehuang.vernier.features.customrom.data.native.CustomRomNativeSnapshot
import com.kaizehuang.vernier.features.customrom.data.rules.CustomRomCatalog
import com.kaizehuang.vernier.features.customrom.domain.CustomRomModificationFinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomRomModificationProbeTest {

    @Test
    fun `clean native snapshot produces no modification findings`() {
        val probe = CustomRomModificationProbe()

        val findings = probe.inspect(CustomRomNativeSnapshot())

        assertTrue(findings.isEmpty())
        assertEquals(
            CustomRomCatalog.modificationProperties.size,
            probe.checkedPropertyCount,
        )
    }

    @Test
    fun `native modification findings are forwarded unchanged`() {
        val finding = CustomRomModificationFinding(
            category = "Prop area",
            signal = "u:object_r:shell_prop:s0",
            summary = "Abnormal prop area",
            detail = "mode=644 uid=0 gid=0",
        )
        val probe = CustomRomModificationProbe()

        val findings = probe.inspect(
            CustomRomNativeSnapshot(
                available = true,
                modificationFindings = listOf(finding),
            ),
        )

        assertEquals(listOf(finding), findings)
    }
}
