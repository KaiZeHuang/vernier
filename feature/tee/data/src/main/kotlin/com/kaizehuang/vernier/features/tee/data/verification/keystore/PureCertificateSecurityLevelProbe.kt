package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.kaizehuang.vernier.capability.attestation.data.AndroidKeyStoreTools
import java.security.KeyPairGenerator
import java.security.KeyStore

class PureCertificateSecurityLevelProbe(
    private val binderClient: Keystore2PrivateBinderClient = Keystore2PrivateBinderClient(),
) {

    fun inspect(): PureCertificateSecurityLevelResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return PureCertificateSecurityLevelResult(
                executed = false,
                detail = "Pure certificate security-level probe requires Android 12 or newer.",
            )
        }
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val tempAlias = "vernier_pure_cert_sec_temp_${System.nanoTime()}"
        val certAlias = "vernier_pure_cert_sec_${System.nanoTime()}"
        return runCatching {
            val generator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC,
                "AndroidKeyStore",
            )
            generator.initialize(
                KeyGenParameterSpec.Builder(
                    tempAlias,
                    KeyProperties.PURPOSE_SIGN,
                ).setDigests(KeyProperties.DIGEST_SHA256).build(),
            )
            generator.generateKeyPair()
            val certificate = keyStore.getCertificate(tempAlias)
                ?: return PureCertificateSecurityLevelResult(
                    executed = true,
                    detail = "Failed to create temporary certificate for pure certificate security-level probe.",
                )
            keyStore.deleteEntry(tempAlias)
            keyStore.setCertificateEntry(certAlias, certificate)

            val service = binderClient.getKeystoreService()
                ?: return PureCertificateSecurityLevelResult(
                    executed = false,
                    detail = "Keystore2 service interface was unavailable.",
                )
            val response = binderClient.getKeyEntryResponse(service, binderClient.createKeyDescriptor(certAlias))
                ?: return PureCertificateSecurityLevelResult(
                    executed = true,
                    detail = "Keystore2 getKeyEntry() returned null for the certificate-only entry.",
                )
            val topLevelSecurityLevel = binderClient.getSecurityLevelBinder(response)
            val metadataSecurityLevel = binderClient.getMetadataSecurityLevel(response)
            PureCertificateSecurityLevelResult(
                executed = true,
                securityLevelPresent = topLevelSecurityLevel != null,
                metadataSecurityLevelPresent = metadataSecurityLevel != null,
                detail = when {
                    topLevelSecurityLevel != null -> {
                        "Certificate-only entry exposed top-level iSecurityLevel: $topLevelSecurityLevel"
                    }
                    metadataSecurityLevel != null -> {
                        "Certificate-only entry kept metadata.keySecurityLevel populated: $metadataSecurityLevel"
                    }
                    else -> {
                        "Certificate-only entry did not expose top-level securityLevel metadata."
                    }
                },
            )
        }.getOrElse { throwable ->
            PureCertificateSecurityLevelResult(
                executed = false,
                detail = throwable.message ?: "Pure certificate security-level probe could not complete.",
            )
        }.also {
            AndroidKeyStoreTools.cleanup(keyStore, listOf(tempAlias, certAlias))
        }
    }
}

data class PureCertificateSecurityLevelResult(
    val executed: Boolean,
    val securityLevelPresent: Boolean = false,
    val metadataSecurityLevelPresent: Boolean = false,
    val detail: String,
)
