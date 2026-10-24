package com.kaizehuang.vernier.features.rootmanagers.domain

/**
 * The value a KernelSU-family kernel uses to recognise its manager app.
 *
 * `kernel/manager/apk_sign.c` reads the first certificate of the first signer in the APK's v2
 * signature block and accepts the app as manager only when that certificate's byte length and
 * SHA-256 equal the build's `EXPECTED_SIZE` and `EXPECTED_HASH`. Matching both values is the same
 * test the kernel applies, so it survives a renamed package, label or class: none of those change
 * the signing key, and a manager re-signed with another key is no longer the manager that kernel
 * trusts.
 */
data class CertificateFingerprint(
    val sizeBytes: Int,
    /** Lowercase hex SHA-256 of the DER certificate bytes. */
    val sha256: String,
)
