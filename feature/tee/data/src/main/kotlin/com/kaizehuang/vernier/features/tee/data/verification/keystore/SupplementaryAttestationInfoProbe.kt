package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.content.Context
import com.kaizehuang.vernier.capability.attestation.data.AttestationSnapshot
import com.kaizehuang.vernier.core.platform.PlatformFailureName
import java.lang.reflect.InvocationTargetException
import java.security.MessageDigest
import org.lsposed.hiddenapibypass.HiddenApiBypass

class SupplementaryAttestationInfoProbe(
    private val context: Context,
) {

    fun inspect(snapshot: AttestationSnapshot): SupplementaryAttestationInfoResult {
        val attestedHash = snapshot.moduleHashHex
        val fetched = fetchModuleInfo()
        val expectedHash = fetched.moduleInfoDer?.sha256Hex()
        val expectsModuleHash =
            (snapshot.attestationVersion ?: 0) >= MODULE_HASH_ATTESTATION_VERSION ||
                (snapshot.keymasterVersion ?: 0) >= MODULE_HASH_ATTESTATION_VERSION
        val anomalyKind = when {
            expectedHash != null && attestedHash == null && expectsModuleHash ->
                SupplementaryAttestationInfoAnomalyKind.MISSING_ATTESTATION_MODULE_HASH
            expectedHash != null && attestedHash != null && expectedHash != attestedHash ->
                SupplementaryAttestationInfoAnomalyKind.MISMATCH
            expectedHash == null && attestedHash != null ->
                SupplementaryAttestationInfoAnomalyKind.UNEXPECTED_ATTESTATION_MODULE_HASH
            expectedHash == null -> SupplementaryAttestationInfoAnomalyKind.UNSUPPORTED
            else -> SupplementaryAttestationInfoAnomalyKind.NONE
        }
        return SupplementaryAttestationInfoResult(
            available = expectedHash != null,
            anomalyKind = anomalyKind,
            expectedModuleHashHex = expectedHash,
            attestedModuleHashHex = attestedHash,
            attestationVersion = snapshot.attestationVersion,
            keymasterVersion = snapshot.keymasterVersion,
            detail = detailFor(fetched.detail, anomalyKind, expectedHash, attestedHash),
        )
    }

    private fun fetchModuleInfo(): ModuleInfoFetchResult {
        return runCatching {
            HiddenApiBypass.addHiddenApiExemptions("")
            val managerClass = loadClass(CLASS_KEYSTORE_MANAGER)
            val manager = context.applicationContext.getSystemService(managerClass)
                ?: managerClass.getDeclaredMethod("getInstance")
                    .also { it.isAccessible = true }
                    .invoke(null)
                ?: return@runCatching ModuleInfoFetchResult(null, "KeyStoreManager service unavailable.")
            val tag = moduleHashTag(managerClass)
            val info = invokeGetSupplementaryAttestationInfo(managerClass, manager, tag)
            ModuleInfoFetchResult(info, "getSupplementaryAttestationInfo(MODULE_HASH) returned ${info.size} DER bytes.")
        }.getOrElse { throwable ->
            ModuleInfoFetchResult(null, "getSupplementaryAttestationInfo(MODULE_HASH) unavailable: ${describe(throwable)}")
        }
    }

    private fun invokeGetSupplementaryAttestationInfo(
        managerClass: Class<*>,
        manager: Any,
        tag: Int,
    ): ByteArray {
        return try {
            val method = managerClass.getDeclaredMethod(
                "getSupplementaryAttestationInfo",
                Int::class.javaPrimitiveType!!,
            )
            method.isAccessible = true
            method.invoke(manager, tag) as ByteArray
        } catch (throwable: InvocationTargetException) {
            throw throwable.cause ?: throwable
        } catch (throwable: Throwable) {
            try {
                HiddenApiBypass.invoke(
                    managerClass,
                    manager,
                    "getSupplementaryAttestationInfo",
                    tag,
                ) as ByteArray
            } catch (bypassThrowable: InvocationTargetException) {
                throw bypassThrowable.cause ?: bypassThrowable
            }
        }
    }

    private fun moduleHashTag(managerClass: Class<*>): Int {
        return runCatching { managerClass.getField("MODULE_HASH").getInt(null) }
            .getOrElse {
                runCatching {
                    loadClass(CLASS_KEYMINT_TAG).getField("MODULE_HASH").getInt(null)
                }.getOrDefault(MODULE_HASH_TAG)
            }
    }

    private fun loadClass(className: String): Class<*> {
        return try {
            Class.forName(className)
        } catch (primary: ClassNotFoundException) {
            try {
                ClassLoader.getSystemClassLoader().loadClass(className)
            } catch (secondary: ClassNotFoundException) {
                HiddenApiBypass.invoke(Class::class.java, null, "forName", className) as Class<*>
            }
        }
    }

    private fun detailFor(
        fetchDetail: String,
        anomalyKind: SupplementaryAttestationInfoAnomalyKind,
        expectedHash: String?,
        attestedHash: String?,
    ): String {
        return buildString {
            append(fetchDetail)
            append(" kind=")
            append(anomalyKind.name)
            expectedHash?.let { append(" expected=${it.take(16)}") }
            attestedHash?.let { append(" attested=${it.take(16)}") }
        }
    }

    private fun describe(throwable: Throwable): String {
        val cause = (throwable as? InvocationTargetException)?.cause ?: throwable
        return "${PlatformFailureName.of(cause)}: ${cause.message ?: "no message"}"
    }

    private fun ByteArray.sha256Hex(): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(this)
            .joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    private data class ModuleInfoFetchResult(
        val moduleInfoDer: ByteArray?,
        val detail: String,
    )

    companion object {
        private const val CLASS_KEYSTORE_MANAGER = "android.security.keystore.KeyStoreManager"
        private const val CLASS_KEYMINT_TAG = "android.hardware.security.keymint.Tag"
        private const val MODULE_HASH_TAG = -1879047468
        private const val MODULE_HASH_ATTESTATION_VERSION = 400
    }
}

enum class SupplementaryAttestationInfoAnomalyKind {
    NONE,
    UNSUPPORTED,
    MISSING_ATTESTATION_MODULE_HASH,
    MISMATCH,
    UNEXPECTED_ATTESTATION_MODULE_HASH,
}

data class SupplementaryAttestationInfoResult(
    val available: Boolean,
    val anomalyKind: SupplementaryAttestationInfoAnomalyKind,
    val expectedModuleHashHex: String? = null,
    val attestedModuleHashHex: String? = null,
    val attestationVersion: Int? = null,
    val keymasterVersion: Int? = null,
    val detail: String,
) {
    val diagnosticCopyText: String
        get() = buildString {
            append("kind=")
            append(anomalyKind.name)
            append('\n')
            append("available=")
            append(available)
            append('\n')
            append("attestationVersion=")
            append(attestationVersion ?: "null")
            append('\n')
            append("keymasterVersion=")
            append(keymasterVersion ?: "null")
            append('\n')
            append("expectedModuleHash=")
            append(expectedModuleHashHex ?: "null")
            append('\n')
            append("attestedModuleHash=")
            append(attestedModuleHashHex ?: "null")
            append('\n')
            append(detail)
        }
}
