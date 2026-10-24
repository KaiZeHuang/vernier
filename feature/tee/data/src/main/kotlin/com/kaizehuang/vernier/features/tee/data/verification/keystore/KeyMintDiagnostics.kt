package com.kaizehuang.vernier.features.tee.data.verification.keystore

internal fun buildKeyMintDiagnosticCopyText(
    sdkInt: Int,
    useStrongBox: Boolean,
    attestationVersion: Int?,
    keymasterVersion: Int?,
    declaredKeyMintVersion: Int?,
    legacyKeymasterDeclared: Boolean,
    securityLevelsConsistent: Boolean,
    runtimeIdentityConsistent: Boolean,
    checks: List<KeyMintDiagnosticEntry>,
): String = buildString {
    appendLine("keymint-capability-diagnostic=v1")
    appendLine("sdkInt=$sdkInt")
    appendLine("useStrongBox=$useStrongBox")
    appendLine("attestationVersion=${attestationVersion ?: "null"}")
    appendLine("keymasterVersion=${keymasterVersion ?: "null"}")
    appendLine("declaredKeyMintVersion=${declaredKeyMintVersion ?: "null"}")
    appendLine("legacyKeymasterDeclared=$legacyKeymasterDeclared")
    appendLine("securityLevelsConsistent=$securityLevelsConsistent")
    appendLine("runtimeIdentityConsistent=$runtimeIdentityConsistent")
    appendLine("checks:")
    checks.forEach { check ->
        appendLine("- ${check.name}: executed=${check.executed}, ok=${check.ok}, detail=${check.detail}")
        check.diagnostic?.takeIf { it.isNotBlank() }?.let { diagnostic ->
            appendLine("  diagnostic=$diagnostic")
        }
    }
}

internal data class KeyMintDiagnosticEntry(
    val name: String,
    val executed: Boolean,
    val ok: Boolean,
    val detail: String,
    val diagnostic: String?,
)
