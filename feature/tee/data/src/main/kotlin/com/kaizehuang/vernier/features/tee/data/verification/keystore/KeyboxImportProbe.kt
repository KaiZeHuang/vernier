package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.content.Context
import android.os.Build
import android.security.keystore.KeyProtection
import android.security.keystore.KeyProperties
import com.kaizehuang.vernier.capability.attestation.data.AndroidKeyStoreTools
import java.security.KeyStore
import java.security.cert.X509Certificate

class KeyboxImportProbe(
    private val context: Context,
) {

    fun inspect(
        keyStore: KeyStore = AndroidKeyStoreTools.loadKeyStore(),
    ): KeyboxImportResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return KeyboxImportResult(
                executed = false,
                markerPreserved = true,
                marker = FIXTURE_MARKER,
                detail = "Keybox import probe requires Android 12 or newer.",
            )
        }
        val alias = "vernier_keybox_probe_${System.nanoTime()}"
        if (!AndroidKeyStoreTools.generateAttestOnlyEcKey(keyStore, alias)) {
            return KeyboxImportResult(
                executed = false,
                markerPreserved = true,
                marker = FIXTURE_MARKER,
                detail = "PURPOSE_ATTEST_KEY could not be provisioned for the probe alias.",
            )
        }
        return runCatching {
            val fixture = KeyboxFixtureLoader(context).load()
            val protection = KeyProtection.Builder(KeyProperties.PURPOSE_ATTEST_KEY)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build()
            keyStore.setEntry(
                alias,
                KeyStore.PrivateKeyEntry(fixture.privateKey, arrayOf(fixture.certificate)),
                protection,
            )
            val retrieved = keyStore.getCertificate(alias) as? X509Certificate
            if (retrieved == null) {
                return KeyboxImportResult(
                    executed = false,
                    markerPreserved = true,
                    marker = FIXTURE_MARKER,
                    detail = "Probe alias returned no certificate after setEntry().",
                )
            }
            val subject = retrieved.subjectX500Principal.name
            val preserved = subject.contains(FIXTURE_MARKER)
            KeyboxImportResult(
                executed = true,
                markerPreserved = preserved,
                marker = FIXTURE_MARKER,
                detail = if (preserved) {
                    "Imported marker certificate returned with its custom subject intact."
                } else {
                    "Imported marker certificate came back with a replaced subject: $subject"
                },
            )
        }.getOrElse { throwable ->
            KeyboxImportResult(
                executed = false,
                markerPreserved = true,
                marker = FIXTURE_MARKER,
                detail = throwable.message ?: "Keybox import probe failed.",
            )
        }.also {
            AndroidKeyStoreTools.safeDelete(keyStore, alias)
        }
    }

    companion object {
        const val FIXTURE_MARKER = "VernierMarker-Keybox"
    }
}

data class KeyboxImportResult(
    val executed: Boolean,
    val markerPreserved: Boolean,
    val marker: String,
    val detail: String,
)
