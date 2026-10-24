package com.kaizehuang.vernier.features.tee.data.verification.rkp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RkpProvisionedManufacturerProbeTest {

    @Test
    fun `matching manufacturers are clean`() {
        assertEquals(
            RkpProvisionedManufacturerAnomalyKind.NONE,
            classifyRkpProvisionedManufacturer("samsung", "samsung"),
        )
    }

    @Test
    fun `spelling differences are not a mismatch`() {
        // map[3] 和 ATTESTATION_ID_MANUFACTURER 的写法经常不同，归一化后单向包含即视为一致
        // map[3] and ATTESTATION_ID_MANUFACTURER are often spelled differently. Containment after normalising counts as agreement.
        assertEquals(
            RkpProvisionedManufacturerAnomalyKind.NONE,
            classifyRkpProvisionedManufacturer("samsung", "Samsung Electronics Co., Ltd."),
        )
        assertTrue(manufacturerNamesAgree("Google", "google"))
        assertFalse(manufacturerNamesAgree("samsung", "Xiaomi"))
    }

    @Test
    fun `disagreeing manufacturers are reported`() {
        assertEquals(
            RkpProvisionedManufacturerAnomalyKind.MISMATCH,
            classifyRkpProvisionedManufacturer("samsung", "Xiaomi"),
        )
    }

    @Test
    fun `certs issued never dismisses the check`() {
        // certs_issued 只是报告上下文，不参与判定：无论签发量多大，这条交叉核对都照常给结论
        // certs_issued is reporting context only and takes no part in the verdict: however large the issuance count,
        // this cross-check still reaches a conclusion.
        assertEquals(
            RkpProvisionedManufacturerAnomalyKind.MISMATCH,
            classifyRkpProvisionedManufacturer("samsung", "Xiaomi"),
        )
        assertEquals(
            RkpProvisionedManufacturerAnomalyKind.NONE,
            classifyRkpProvisionedManufacturer("samsung", "samsung"),
        )
    }

    @Test
    fun `missing sides dismiss instead of judging`() {
        assertEquals(
            RkpProvisionedManufacturerAnomalyKind.DISMISSED_NO_PROVISIONING_MANUFACTURER,
            classifyRkpProvisionedManufacturer(null, "samsung"),
        )
        assertEquals(
            RkpProvisionedManufacturerAnomalyKind.DISMISSED_NO_ATTESTED_MANUFACTURER,
            classifyRkpProvisionedManufacturer("samsung", null),
        )
    }

    @Test
    fun `detail names both sides on a mismatch`() {
        val detail = buildRkpProvisionedManufacturerDetail(
            anomalyKind = RkpProvisionedManufacturerAnomalyKind.MISMATCH,
            provisioningManufacturer = "samsung",
            attestedManufacturer = "Xiaomi",
        )
        assertTrue(detail.contains("samsung"))
        assertTrue(detail.contains("Xiaomi"))
    }
}
