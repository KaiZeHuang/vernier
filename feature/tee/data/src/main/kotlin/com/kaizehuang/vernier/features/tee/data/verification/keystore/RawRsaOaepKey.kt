package com.kaizehuang.vernier.features.tee.data.verification.keystore

import java.security.PublicKey

internal data class RawRsaOaepKey(
    val service: Any,
    val cleanupDescriptor: Any,
    val operationDescriptor: Any,
    val securityLevel: Any,
    val expectedSecurityLevel: Int,
    val returnedSecurityLevel: Int,
    val publicKey: PublicKey,
    val authorizations: List<AuthorizationSummary>,
)

internal fun RawRsaOaepKey.operationDiagnostic(operation: String): String = buildString {
    append("serviceClass=")
    append(service.javaClass.name)
    append(", securityLevelClass=")
    append(securityLevel.javaClass.name)
    append(", descriptorClass=")
    append(operationDescriptor.javaClass.name)
    append(", publicKeyAlgorithm=")
    append(publicKey.algorithm)
    append(", ")
    append("selectedSecurityLevel=")
    append(expectedSecurityLevel)
    append(", returnedSecurityLevel=")
    append(returnedSecurityLevel)
    append(", authorizationCount=")
    append(authorizations.size)
    append(", mgfAuthorizations=")
    append(
        authorizations
            .filter { it.tag == KEYMINT_TAG_RSA_OAEP_MGF_DIGEST }
            .joinToString { authorization ->
                "value=${authorization.intValue ?: "null"}," +
                    "securityLevel=${authorization.securityLevel ?: "null"}"
            }
            .ifBlank { "none" },
    )
    append(", ")
    append(operation)
}
