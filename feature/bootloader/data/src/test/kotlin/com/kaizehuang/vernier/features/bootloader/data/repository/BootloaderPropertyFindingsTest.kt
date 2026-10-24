package com.kaizehuang.vernier.features.bootloader.data.repository

import com.kaizehuang.vernier.capability.systemproperties.domain.MultiSourcePropertyRead
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertyCategory
import com.kaizehuang.vernier.capability.systemproperties.domain.SystemPropertySource
import com.kaizehuang.vernier.features.bootloader.data.rules.BootloaderCatalog
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BootloaderPropertyFindingsTest {

    @Test
    fun `vbmeta digest of empty sha256 input is danger that sets the verdict`() {
        val finding = vbmetaDigestFinding(EMPTY_SHA256)

        assertEquals(BootloaderFindingSeverity.DANGER, finding.severity)
        assertFalse(finding.corroborating)
        assertEquals("Empty-input digest", finding.value)
        assertTrue(finding.detail.orEmpty().contains("SHA-256 digest of empty input"))
        assertTrue(finding.detail.orEmpty().contains("Observed value: $EMPTY_SHA256"))
    }

    @Test
    fun `vbmeta digest of empty sha512 input is danger`() {
        val finding = vbmetaDigestFinding(EMPTY_SHA512)

        assertEquals(BootloaderFindingSeverity.DANGER, finding.severity)
        assertTrue(finding.detail.orEmpty().contains("SHA-512 digest of empty input"))
    }

    @Test
    fun `empty input digest matches regardless of case and padding`() {
        val finding = vbmetaDigestFinding(" ${EMPTY_SHA256.uppercase()} ")

        assertEquals(BootloaderFindingSeverity.DANGER, finding.severity)
    }

    @Test
    fun `vbmeta digest of loaded images stays info`() {
        val finding = vbmetaDigestFinding(IMAGE_DIGEST)

        assertEquals(BootloaderFindingSeverity.INFO, finding.severity)
        assertEquals(IMAGE_DIGEST, finding.value)
        assertFalse(finding.detail.orEmpty().contains("empty input"))
    }

    @Test
    fun `empty input vbmeta digest marks the property catalog as danger`() {
        assertTrue(BootloaderPropertyContext.from(reads(EMPTY_SHA256)).hasDangerProperty)
        assertFalse(BootloaderPropertyContext.from(reads(IMAGE_DIGEST)).hasDangerProperty)
    }

    private fun vbmetaDigestFinding(value: String) =
        buildPropertyFindings(BootloaderPropertyContext.empty(), reads(value)).single()

    private fun reads(vbmetaDigest: String): Map<String, MultiSourcePropertyRead> {
        return mapOf(
            BootloaderCatalog.VBMETA_DIGEST to MultiSourcePropertyRead(
                property = BootloaderCatalog.VBMETA_DIGEST,
                category = SystemPropertyCategory.VERIFIED_BOOT,
                preferredValue = vbmetaDigest,
                preferredSource = SystemPropertySource.REFLECTION,
                sourceValues = mapOf(SystemPropertySource.REFLECTION to vbmetaDigest),
            ),
        )
    }

    private companion object {
        const val EMPTY_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        const val EMPTY_SHA512 =
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"
        const val IMAGE_DIGEST = "74ff0077db9a6a22058c1d11dbb1107916fcb1c096399309f6d9e9d91eba8f13"
    }
}
