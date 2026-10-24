package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.content.Context
import com.kaizehuang.vernier.features.tee.data.R
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

internal class KeyboxFixtureLoader(
    private val context: Context,
) {
    private val certificateFactory = CertificateFactory.getInstance("X.509")

    fun load(): KeyboxFixture {
        val certificatePem = context.resources.openRawResource(R.raw.vernier_marker_cert)
            .bufferedReader()
            .use { it.readText() }
        val keyPem = context.resources.openRawResource(R.raw.vernier_marker_key)
            .bufferedReader()
            .use { it.readText() }
        val cert =
            certificateFactory.generateCertificate(certificatePem.byteInputStream()) as X509Certificate
        val key = decodeEcPrivateKey(keyPem)
        return KeyboxFixture(privateKey = key, certificate = cert)
    }

    private fun decodeEcPrivateKey(pem: String): PrivateKey {
        val body = pem.lineSequence()
            .filterNot { it.startsWith("-----") }
            .joinToString(separator = "")
        val bytes = Base64.getDecoder().decode(body)
        return KeyFactory.getInstance("EC").generatePrivate(PKCS8EncodedKeySpec(bytes))
    }
}

internal data class KeyboxFixture(
    val privateKey: PrivateKey,
    val certificate: X509Certificate,
)
