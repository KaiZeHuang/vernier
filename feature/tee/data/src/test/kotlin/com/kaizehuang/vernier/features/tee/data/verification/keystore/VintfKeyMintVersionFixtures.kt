package com.kaizehuang.vernier.features.tee.data.verification.keystore

import com.kaizehuang.vernier.capability.attestation.data.AttestationSnapshot
import com.kaizehuang.vernier.capability.attestation.domain.TeeTier
import java.nio.file.Files

internal fun withManifest(xml: String, block: (String) -> Unit) {
    val dir = Files.createTempDirectory("vernier_vintf").toFile()
    try {
        val manifest = dir.resolve("manifest.xml")
        manifest.writeText(xml)
        block(manifest.absolutePath)
    } finally {
        dir.deleteRecursively()
    }
}

internal fun withManifestDirectory(files: Map<String, String>, block: (String) -> Unit) {
    val dir = Files.createTempDirectory("vernier_vintf_dir").toFile()
    try {
        files.forEach { (name, content) -> dir.resolve(name).writeText(content) }
        block(dir.absolutePath)
    } finally {
        dir.deleteRecursively()
    }
}

internal fun vendorApiLevelReader(level: Int): VendorApiLevelReader = VendorApiLevelReader {
    VendorApiLevelResult(level, "test vendor API level=$level")
}

internal fun keyMintDeclaration(aidlVersion: Int): VintfKeyMintVersionDeclaration {
    return VintfKeyMintVersionDeclaration(
        family = VintfKeyMintVersionFamily.KEYMINT_AIDL,
        sourcePath = "/test/manifest.xml",
        format = "aidl",
        halName = "android.hardware.security.keymint",
        interfaceName = "IKeyMintDevice",
        instance = "default",
        vintfVersion = aidlVersion.toString(),
        expectedKeymasterVersion = aidlVersion * 100,
        expectedAttestationVersion = aidlVersion * 100,
    )
}

internal fun snapshot(attestationVersion: Int?, keymasterVersion: Int?): AttestationSnapshot {
    return AttestationSnapshot(
        tier = TeeTier.TEE,
        attestationVersion = attestationVersion,
        keymasterVersion = keymasterVersion,
        attestationTier = TeeTier.TEE,
        keymasterTier = TeeTier.TEE,
        challengeVerified = true,
        challengeSummary = "ok",
        rootOfTrust = null,
        osVersion = null,
        osPatchLevel = null,
        vendorPatchLevel = null,
        bootPatchLevel = null,
        rawCertificates = emptyList(),
        displayCertificates = emptyList(),
    )
}
