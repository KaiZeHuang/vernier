package com.kaizehuang.vernier.features.tee.data.verification.keystore

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimingSideChannelThresholdTest {

    @Test
    fun `ratio above 1_1x is positive`() {
        assertTrue(
            isPositiveTimingSideChannelRatio(
                avgAttestedMillis = 0.612,
                avgNonAttestedMillis = 0.400,
            ),
        )
    }

    @Test
    fun `ratio above 1_1x is positive regardless of direction`() {
        assertTrue(
            isPositiveTimingSideChannelRatio(
                avgAttestedMillis = 0.100,
                avgNonAttestedMillis = 0.450,
            ),
        )
    }

    @Test
    fun `ratio equal to 1_1x is not positive`() {
        assertFalse(
            isPositiveTimingSideChannelRatio(
                avgAttestedMillis = 1.10,
                avgNonAttestedMillis = 1.00,
            ),
        )
    }

    @Test
    fun `ratio inside threshold is not positive`() {
        assertFalse(
            isPositiveTimingSideChannelRatio(
                avgAttestedMillis = 1.09,
                avgNonAttestedMillis = 1.00,
            ),
        )
    }

    @Test
    fun `missing or invalid timing is not positive`() {
        assertFalse(isPositiveTimingSideChannelRatio(null, 1.00))
        assertFalse(isPositiveTimingSideChannelRatio(1.00, 0.0))
    }

    @Test
    fun `filtered sample count below minimum is not ratio eligible`() {
        assertFalse(isTimingSideChannelRatioEligible(299))
    }

    @Test
    fun `filtered sample count at minimum remains ratio eligible`() {
        assertTrue(isTimingSideChannelRatioEligible(300))
    }
}
