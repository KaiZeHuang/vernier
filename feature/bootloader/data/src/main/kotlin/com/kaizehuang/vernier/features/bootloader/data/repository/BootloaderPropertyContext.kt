package com.kaizehuang.vernier.features.bootloader.data.repository

import android.os.Build
import com.kaizehuang.vernier.capability.systemproperties.domain.MultiSourcePropertyRead
import com.kaizehuang.vernier.features.bootloader.data.rules.BootloaderCatalog

internal enum class PropertyBootState {
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    UNKNOWN,
}

internal data class BootloaderPropertyContext(
    val bootState: PropertyBootState,
    val isLocked: Boolean?,
    val secureBoot: String?,
    val debuggable: String?,
    val secure: String?,
    val warrantyBit: String?,
    val knoxState: String?,
    val verityMode: String?,
    val lockEvidence: List<String>,
    val hasBootEvidence: Boolean,
    val hasDangerProperty: Boolean,
    val hasWarningProperty: Boolean,
    val isDebugBuild: Boolean,
    val warrantyVoid: Boolean,
    val isSamsungDevice: Boolean,
) {
    companion object {
        fun from(readsByProperty: Map<String, MultiSourcePropertyRead>): BootloaderPropertyContext {
            val flashLocked =
                readsByProperty[BootloaderCatalog.FLASH_LOCKED]?.preferredValue.orEmpty()
            val vbmetaDeviceState =
                readsByProperty[BootloaderCatalog.VBMETA_DEVICE_STATE]?.preferredValue.orEmpty()
            val secureBoot = readsByProperty[BootloaderCatalog.SECURE_BOOT]?.preferredValue
            val debuggable = readsByProperty[BootloaderCatalog.DEBUGGABLE]?.preferredValue
            val secure = readsByProperty[BootloaderCatalog.SECURE]?.preferredValue
            val warrantyBit = listOf(
                readsByProperty[BootloaderCatalog.WARRANTY_BIT]?.preferredValue,
                readsByProperty[BootloaderCatalog.WARRANTY_BIT_ALT]?.preferredValue,
            ).firstOrNull { it.isNullOrBlank().not() }
            val knoxState = readsByProperty[BootloaderCatalog.KNOX_STATE]?.preferredValue
            val verityMode = readsByProperty[BootloaderCatalog.VERITYMODE]?.preferredValue
            val vbmetaDigest = readsByProperty[BootloaderCatalog.VBMETA_DIGEST]?.preferredValue.orEmpty()
            val verifiedBoot =
                readsByProperty[BootloaderCatalog.VERIFIED_BOOT_STATE]?.preferredValue.orEmpty()

            val lockEvidence = buildList {
                if (flashLocked.isNotBlank()) add(BootloaderCatalog.FLASH_LOCKED)
                if (vbmetaDeviceState.isNotBlank()) add(BootloaderCatalog.VBMETA_DEVICE_STATE)
                if (secureBoot.isNullOrBlank().not()) add(BootloaderCatalog.SECURE_BOOT)
            }

            val isLocked = when {
                flashLocked == "1" -> true
                flashLocked == "0" -> false
                vbmetaDeviceState.equals("locked", ignoreCase = true) -> true
                vbmetaDeviceState.equals("unlocked", ignoreCase = true) -> false
                secureBoot == "1" -> true
                else -> null
            }

            val bootState = when (verifiedBoot.lowercase()) {
                "green" -> PropertyBootState.GREEN
                "yellow" -> PropertyBootState.YELLOW
                "orange" -> PropertyBootState.ORANGE
                "red" -> PropertyBootState.RED
                else -> PropertyBootState.UNKNOWN
            }

            val isSamsungDevice = Build.MANUFACTURER.equals("samsung", ignoreCase = true)
            val warrantyVoid =
                warrantyBit == "1" || knoxState.equals("TRIPPED", ignoreCase = true)
            val isDebugBuild = debuggable == "1" || secure == "0"
            val hasDangerProperty = when {
                isLocked == false -> true
                bootState == PropertyBootState.ORANGE || bootState == PropertyBootState.RED -> true
                warrantyVoid -> true
                verityMode.equals("disabled", ignoreCase = true) || verityMode == "0" -> true
                emptyInputDigestAlgorithm(vbmetaDigest) != null -> true
                else -> false
            }
            val hasWarningProperty = when {
                bootState == PropertyBootState.YELLOW -> true
                verityMode.equals("logging", ignoreCase = true) || verityMode == "2" -> true
                isDebugBuild -> true
                else -> false
            }

            return BootloaderPropertyContext(
                bootState = bootState,
                isLocked = isLocked,
                secureBoot = secureBoot,
                debuggable = debuggable,
                secure = secure,
                warrantyBit = warrantyBit,
                knoxState = knoxState,
                verityMode = verityMode,
                lockEvidence = lockEvidence,
                hasBootEvidence = isLocked != null || bootState != PropertyBootState.UNKNOWN || secureBoot.isNullOrBlank()
                    .not(),
                hasDangerProperty = hasDangerProperty,
                hasWarningProperty = hasWarningProperty,
                isDebugBuild = isDebugBuild,
                warrantyVoid = warrantyVoid,
                isSamsungDevice = isSamsungDevice,
            )
        }

        fun empty(): BootloaderPropertyContext {
            return BootloaderPropertyContext(
                bootState = PropertyBootState.UNKNOWN,
                isLocked = null,
                secureBoot = null,
                debuggable = null,
                secure = null,
                warrantyBit = null,
                knoxState = null,
                verityMode = null,
                lockEvidence = emptyList(),
                hasBootEvidence = false,
                hasDangerProperty = false,
                hasWarningProperty = false,
                isDebugBuild = false,
                warrantyVoid = false,
                isSamsungDevice = false,
            )
        }
    }
}
