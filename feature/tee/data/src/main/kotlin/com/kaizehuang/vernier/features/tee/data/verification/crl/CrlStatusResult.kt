package com.kaizehuang.vernier.features.tee.data.verification.crl

import com.kaizehuang.vernier.features.tee.domain.TeeNetworkState

data class CrlStatusResult(
    val networkState: TeeNetworkState,
    val revokedCertificates: List<RevokedCertificate> = emptyList(),
)

data class RevokedCertificate(
    val serial: String,
    val reason: String,
    val evidenceKind: RevokedCertificateEvidenceKind = RevokedCertificateEvidenceKind.STANDARD_REVOCATION,
)

enum class RevokedCertificateEvidenceKind {
    STANDARD_REVOCATION,
    LOCAL_MASS_ABUSE,
}
