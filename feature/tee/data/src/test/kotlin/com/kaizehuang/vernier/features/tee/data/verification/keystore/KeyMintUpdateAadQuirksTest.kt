package com.kaizehuang.vernier.features.tee.data.verification.keystore

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyMintUpdateAadQuirksTest {

    private fun identity(
        manufacturer: String,
        brand: String = manufacturer,
        hardware: String = "",
        roHardware: String? = null,
    ) = KeyMintVendorIdentity(manufacturer, brand, hardware, roHardware)

    @Test
    fun `samsung by manufacturer or brand accepts updateAad`() {
        assertTrue(updateAadAcceptanceExpected(identity("samsung")))
        assertTrue(updateAadAcceptanceExpected(identity(manufacturer = "Unknown", brand = "Samsung")))
    }

    @Test
    fun `xiaomi family accepts updateAad only on mediatek`() {
        assertTrue(updateAadAcceptanceExpected(identity("Xiaomi", roHardware = "mt6789")))
        assertTrue(updateAadAcceptanceExpected(identity(manufacturer = "Xiaomi", brand = "Redmi", hardware = "MT6877")))
        assertFalse(updateAadAcceptanceExpected(identity("Xiaomi", roHardware = "qcom", hardware = "qcom")))
    }

    @Test
    fun `other vendors are not expected to accept updateAad`() {
        assertFalse(updateAadAcceptanceExpected(identity("Google", hardware = "tensor")))
        assertFalse(updateAadAcceptanceExpected(identity("OnePlus", roHardware = "mt6983")))
    }
}
