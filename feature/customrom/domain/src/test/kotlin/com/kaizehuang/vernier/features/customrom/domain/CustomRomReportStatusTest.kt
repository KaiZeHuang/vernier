package com.kaizehuang.vernier.features.customrom.domain

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomRomReportStatusTest {

    private val fullCoverage = CustomRomReport.loading().copy(
        stage = CustomRomStage.READY,
        packageVisibility = CustomRomPackageVisibility.FULL,
        nativeAvailable = true,
        propertyAreaAvailable = true,
        symbolScanAvailable = true,
        serviceScanAvailable = true,
    )

    @Test
    fun `full coverage without findings is clear`() {
        assertEquals(DetectorStatus.allClear(), fullCoverage.toDetectorStatus())
    }

    @Test
    fun `an unqueried ServiceManager is reduced coverage, not a clean result`() {
        assertEquals(
            DetectorStatus.info(InfoKind.SUPPORT),
            fullCoverage.copy(serviceScanAvailable = false).toDetectorStatus(),
        )
    }
}
