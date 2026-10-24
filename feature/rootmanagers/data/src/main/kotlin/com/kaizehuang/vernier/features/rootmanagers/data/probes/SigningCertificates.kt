package com.kaizehuang.vernier.features.rootmanagers.data.probes

import android.content.pm.PackageInfo
import com.kaizehuang.vernier.features.rootmanagers.domain.CertificateFingerprint
import java.security.MessageDigest

/**
 * Fingerprints the certificates PackageManager reports for a package.
 *
 * `Signature.toByteArray()` is the DER encoding of each signer's own certificate, the first entry of
 * that signer's chain in the APK signature block, so its length and SHA-256 are the same pair a
 * KernelSU-family kernel computes in `kernel/manager/apk_sign.c`. The rotation history is included
 * so a manager that rotated its key still shows the original one the kernel was built against.
 */
internal object SigningCertificates {

    /** Null when PackageManager returned no signing information, which is a failed read, not a match. */
    fun of(packageInfo: PackageInfo): List<CertificateFingerprint>? {
        val signingInfo = packageInfo.signingInfo ?: return null
        val signatures = buildList {
            addAll(signingInfo.apkContentsSigners.orEmpty())
            if (!signingInfo.hasMultipleSigners()) {
                addAll(signingInfo.signingCertificateHistory.orEmpty())
            }
        }
        return signatures.map { signature -> fingerprint(signature.toByteArray()) }.distinct()
    }

    fun fingerprint(der: ByteArray): CertificateFingerprint = CertificateFingerprint(
        sizeBytes = der.size,
        sha256 = MessageDigest.getInstance("SHA-256").digest(der).toLowerHex(),
    )

    private fun ByteArray.toLowerHex(): String = buildString(size * 2) {
        for (byte in this@toLowerHex) {
            val value = byte.toInt() and 0xff
            append(HEX_DIGITS[value ushr 4])
            append(HEX_DIGITS[value and 0x0f])
        }
    }

    private const val HEX_DIGITS = "0123456789abcdef"
}
