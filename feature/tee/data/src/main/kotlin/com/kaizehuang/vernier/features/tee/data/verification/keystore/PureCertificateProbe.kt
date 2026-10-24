package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore

class PureCertificateProbe {

    fun inspect(): PureCertificateResult {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val tempAlias = "vernier_pure_cert_temp_${System.nanoTime()}"
        val certAlias = "vernier_pure_cert_${System.nanoTime()}"
        return runCatching {
            val generator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC,
                "AndroidKeyStore",
            )
            generator.initialize(
                KeyGenParameterSpec.Builder(
                    tempAlias,
                    KeyProperties.PURPOSE_SIGN,
                )
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .build(),
            )
            generator.generateKeyPair()
            val certificate = keyStore.getCertificate(tempAlias)
                ?: return PureCertificateResult.notCompleted(
                    "Failed to create temporary certificate for pure certificate probe.",
                )
            keyStore.deleteEntry(tempAlias)
            keyStore.setCertificateEntry(certAlias, certificate)
            val keyResult = runCatching { keyStore.getKey(certAlias, null) }.getOrNull()
            PureCertificateResult(
                pureCertificateReturnsNullKey = keyResult == null,
                detail = if (keyResult == null) {
                    "Pure certificate entry returned null from getKey()."
                } else {
                    "Pure certificate entry unexpectedly returned a Key object."
                },
            )
        }.getOrElse { throwable ->
            PureCertificateResult.notCompleted(throwable.message ?: "Pure certificate probe did not complete.")
        }.also {
            runCatching { keyStore.deleteEntry(tempAlias) }
            runCatching { keyStore.deleteEntry(certAlias) }
        }
    }
}

data class PureCertificateResult(
    val executed: Boolean = true,
    val pureCertificateReturnsNullKey: Boolean,
    val probeError: String? = null,
    val detail: String,
) {
    companion object {
        /** The certificate-only entry was never read back, so getKey() said nothing either way. */
        fun notCompleted(reason: String) = PureCertificateResult(
            executed = false,
            pureCertificateReturnsNullKey = false,
            probeError = reason,
            detail = reason,
        )
    }
}
