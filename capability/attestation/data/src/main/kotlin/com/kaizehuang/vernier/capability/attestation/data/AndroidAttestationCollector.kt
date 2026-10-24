package com.kaizehuang.vernier.capability.attestation.data

import com.kaizehuang.vernier.capability.attestation.domain.TeeTier
import java.security.SecureRandom
import java.security.cert.X509Certificate

public class AndroidAttestationCollector(
    private val parser: AttestationExtensionParser = AttestationExtensionParser(),
) {

    public fun collect(useStrongBox: Boolean = false): AttestationSnapshot {
        val keyStore = AndroidKeyStoreTools.loadKeyStore()
        val alias = "vernier_attest_${System.nanoTime()}"
        val challenge = ByteArray(32).also { SecureRandom().nextBytes(it) }
        return runCatching {
            val chain = AndroidKeyStoreTools.generateAttestedEcChain(
                keyStore = keyStore,
                alias = alias,
                challenge = challenge,
                useStrongBox = useStrongBox,
            )
            parser.parse(chain, challenge)
        }.getOrElse { throwable ->
            AttestationSnapshot(
                tier = TeeTier.UNKNOWN,
                attestationVersion = null,
                keymasterVersion = null,
                attestationTier = null,
                keymasterTier = null,
                challengeVerified = false,
                challengeSummary = null,
                rootOfTrust = null,
                osVersion = null,
                osPatchLevel = null,
                vendorPatchLevel = null,
                bootPatchLevel = null,
                keyProperties = AttestedKeyProperties(),
                authState = AttestedAuthState(),
                applicationInfo = AttestedApplicationInfo(),
                deviceInfo = AttestedDeviceInfo(),
                deviceUniqueAttestation = false,
                trustedAttestationIndex = null,
                rawCertificates = emptyList(),
                displayCertificates = emptyList(),
                errorMessage = throwable.message ?: "Attestation collection failed",
            )
        }.also {
            AndroidKeyStoreTools.safeDelete(keyStore, alias)
        }
    }

    public fun collectComparisonChains(useStrongBox: Boolean = false): Pair<List<X509Certificate>, List<X509Certificate>> {
        val keyStore = AndroidKeyStoreTools.loadKeyStore()
        val base = "vernier_compare_${System.nanoTime()}"
        val challenge = ByteArray(24).also { SecureRandom().nextBytes(it) }
        return runCatching {
            val rsa = AndroidKeyStoreTools.generateAttestedRsaChain(
                keyStore,
                "${base}_rsa",
                challenge,
                useStrongBox
            )
            val ec = AndroidKeyStoreTools.generateAttestedEcChain(
                keyStore,
                "${base}_ec",
                challenge,
                useStrongBox
            )
            rsa to ec
        }.getOrDefault(emptyList<X509Certificate>() to emptyList()).also {
            AndroidKeyStoreTools.cleanup(keyStore, listOf("${base}_rsa", "${base}_ec"))
        }
    }
}
