package com.kaizehuang.vernier.features.bootloader.data.widevine

import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingGroup
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingSeverity
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderMethodOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidevineCredentialRepositoryTest {

    @Test
    fun `sentinel maps to consistency warning without a bootloader state result`() {
        val repository = repository(snapshot(systemId = WIDEVINE_SENTINEL_SYSTEM_ID))

        val evidence = repository.inspect(
            WidevineBootContext(
                rootOfTrustUnlocked = false,
                bootStateAppearsLocked = true,
            ),
        )

        assertTrue(evidence.findings.all { it.group == BootloaderFindingGroup.CONSISTENCY })
        val credential = evidence.findings.single { it.id == "widevine_credential" }
        assertEquals(BootloaderFindingSeverity.WARNING, credential.severity)
        assertTrue(credential.corroborating)
        assertFalse(evidence.findings.single { it.id == "widevine_property_parity" }.corroborating)
        assertEquals(BootloaderMethodOutcome.WARNING, evidence.method.outcome)
        assertEquals(1, evidence.anomalyCount)
        assertEquals(BootloaderFindingSeverity.WARNING, evidence.impacts.single().severity)
    }

    @Test
    fun `RootOfTrust unlock remains separate from sentinel evidence`() {
        val repository = repository(snapshot(systemId = WIDEVINE_SENTINEL_SYSTEM_ID))

        val evidence = repository.inspect(
            WidevineBootContext(
                rootOfTrustUnlocked = true,
                bootStateAppearsLocked = false,
            ),
        )

        assertEquals(
            BootloaderFindingSeverity.WARNING,
            evidence.findings.single { it.id == "widevine_credential" }.severity,
        )
        assertEquals(BootloaderMethodOutcome.WARNING, evidence.method.outcome)
    }

    @Test
    fun `sentinel with a lower session maps to a corroborating warning`() {
        val repository = repository(
            snapshot(
                systemId = WIDEVINE_SENTINEL_SYSTEM_ID,
                actualLevel = WidevineSessionSecurityLevel.SW_SECURE_CRYPTO,
            ),
        )

        val evidence = repository.inspect(
            WidevineBootContext(
                rootOfTrustUnlocked = false,
                bootStateAppearsLocked = true,
            ),
        )

        val credential = evidence.findings.single { it.id == "widevine_credential" }
        assertEquals(BootloaderFindingSeverity.WARNING, credential.severity)
        assertTrue(credential.corroborating)
        assertEquals(BootloaderMethodOutcome.WARNING, evidence.method.outcome)
    }

    @Test
    fun `unsupported Widevine maps to support method and no anomaly`() {
        val repository = repository(WidevineCredentialSnapshot(schemeSupported = false))

        val evidence = repository.inspect(
            WidevineBootContext(
                rootOfTrustUnlocked = false,
                bootStateAppearsLocked = true,
            ),
        )

        assertEquals(BootloaderMethodOutcome.SUPPORT, evidence.method.outcome)
        assertEquals(0, evidence.anomalyCount)
        assertTrue(evidence.impacts.isEmpty())
    }

    private fun repository(snapshot: WidevineCredentialSnapshot): WidevineCredentialRepository {
        return WidevineCredentialRepository(
            source = WidevineCredentialSource { snapshot },
        )
    }

    private fun snapshot(
        systemId: String,
        actualLevel: WidevineSessionSecurityLevel = WidevineSessionSecurityLevel.HW_SECURE_ALL,
    ): WidevineCredentialSnapshot {
        val securityLevel = WidevinePropertyRead(WidevinePropertyStatus.AVAILABLE, "L1")
        val id = WidevinePropertyRead(WidevinePropertyStatus.AVAILABLE, systemId)
        return WidevineCredentialSnapshot(
            schemeSupported = true,
            hardwareSecureAllSupported = true,
            javaSecurityLevel = securityLevel,
            javaSystemId = id,
            native = WidevineNativeSnapshot(
                available = true,
                securityLevel = securityLevel,
                systemId = id,
                securityLevelStatusCode = 0,
                systemIdStatusCode = 0,
            ),
            sessionStatus = WidevineOperationStatus.SUCCESS,
            actualSessionSecurityLevel = actualLevel,
            credentialStatus = WidevineOperationStatus.SUCCESS,
            credentialAvailable = true,
            keyRequestStatus = WidevineOperationStatus.SUCCESS,
        )
    }
}
