package com.kaizehuang.vernier.features.tee.data.verification.keystore

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.os.Build
import com.kaizehuang.vernier.capability.attestation.data.AndroidKeyStoreTools
import javax.crypto.SecretKey

class BiometricTeeIntegrationProbe(
    private val context: Context,
) {

    fun inspect(): BiometricTeeIntegrationResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return BiometricTeeIntegrationResult(
                executed = false,
                detail = "Biometric TEE integration probe requires Android 10 or newer.",
            )
        }
        val packageManager = context.packageManager
        val hasBiometricHardware = packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ||
            packageManager.hasSystemFeature(PackageManager.FEATURE_FACE) ||
            packageManager.hasSystemFeature(PackageManager.FEATURE_IRIS)
        if (!hasBiometricHardware) {
            return BiometricTeeIntegrationResult(
                executed = false,
                detail = "No biometric hardware feature was advertised.",
            )
        }
        val biometricManager = context.getSystemService(BiometricManager::class.java)
            ?: return BiometricTeeIntegrationResult(
                executed = false,
                detail = "BiometricManager was unavailable.",
            )
        val strongBiometricAvailable = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                    BiometricManager.BIOMETRIC_SUCCESS
            else -> biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
        }
        if (!strongBiometricAvailable) {
            return BiometricTeeIntegrationResult(
                executed = true,
                strongBiometricAvailable = false,
                detail = "Strong biometric authentication was not currently available.",
            )
        }
        val keyStore = AndroidKeyStoreTools.loadKeyStore()
        val alias = "vernier_biometric_${System.nanoTime()}"
        return runCatching {
            AndroidKeyStoreTools.generateBiometricBoundAesKey(keyStore, alias)
            val secretKey = keyStore.getKey(alias, null) as? SecretKey
            BiometricTeeIntegrationResult(
                executed = true,
                strongBiometricAvailable = true,
                keyCreated = true,
                keyRetrieved = secretKey != null,
                detail = if (secretKey != null) {
                    "Strong biometric path created and returned a user-auth-bound AES key."
                } else {
                    "Strong biometric path created the alias but getKey() returned null."
                },
            )
        }.getOrElse { throwable ->
            BiometricTeeIntegrationResult(
                executed = true,
                strongBiometricAvailable = true,
                detail = throwable.message ?: "Biometric TEE integration probe failed.",
            )
        }.also {
            AndroidKeyStoreTools.safeDelete(keyStore, alias)
        }
    }
}

data class BiometricTeeIntegrationResult(
    val executed: Boolean,
    val strongBiometricAvailable: Boolean = false,
    val keyCreated: Boolean = false,
    val keyRetrieved: Boolean = false,
    val detail: String,
)
