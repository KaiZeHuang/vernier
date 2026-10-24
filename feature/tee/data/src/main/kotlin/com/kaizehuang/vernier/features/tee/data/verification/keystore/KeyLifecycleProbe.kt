package com.kaizehuang.vernier.features.tee.data.verification.keystore

import com.kaizehuang.vernier.capability.attestation.data.AndroidKeyStoreTools
import java.security.KeyStore

class KeyLifecycleProbe {

    fun inspect(
        keyStore: KeyStore = AndroidKeyStoreTools.loadKeyStore(),
        useStrongBox: Boolean = false,
    ): KeyLifecycleResult {
        val alias = "vernier_lifecycle_${System.nanoTime()}"
        return runCatching {
            AndroidKeyStoreTools.generateSigningEcKey(
                keyStore = keyStore,
                alias = alias,
                subject = "CN=Vernier Lifecycle Probe, O=Vernier",
                useStrongBox = useStrongBox,
            )
            val firstSerial = AndroidKeyStoreTools.readLeafCertificate(
                keyStore,
                alias
            )?.serialNumber?.toString(16)
            val created = keyStore.containsAlias(alias)
            AndroidKeyStoreTools.safeDelete(keyStore, alias)
            val deleted = !keyStore.containsAlias(alias)
            AndroidKeyStoreTools.generateSigningEcKey(
                keyStore = keyStore,
                alias = alias,
                subject = "CN=Vernier Lifecycle Probe, O=Vernier",
                useStrongBox = useStrongBox,
            )
            val secondSerial = AndroidKeyStoreTools.readLeafCertificate(
                keyStore,
                alias
            )?.serialNumber?.toString(16)
            KeyLifecycleResult(
                created = created,
                deleteRemovedAlias = deleted,
                regeneratedFreshMaterial = firstSerial != null && secondSerial != null && firstSerial != secondSerial,
                detail = buildString {
                    append("Create=")
                    append(created)
                    append(", delete=")
                    append(deleted)
                    append(", regenerateFresh=")
                    append(firstSerial != null && secondSerial != null && firstSerial != secondSerial)
                },
            )
        }.getOrElse { throwable ->
            val reason = throwable.message ?: "Key lifecycle probe did not complete."
            KeyLifecycleResult(
                executed = false,
                created = false,
                deleteRemovedAlias = false,
                regeneratedFreshMaterial = false,
                probeError = reason,
                detail = reason,
            )
        }.also {
            AndroidKeyStoreTools.safeDelete(keyStore, alias)
        }
    }
}

/** A keystore exception means the probe did not complete; only an observed alias or serial contradicts. */
data class KeyLifecycleResult(
    val executed: Boolean = true,
    val created: Boolean,
    val deleteRemovedAlias: Boolean,
    val regeneratedFreshMaterial: Boolean,
    val probeError: String? = null,
    val detail: String,
)
