package com.kaizehuang.vernier.features.bootloader.data.widevine

import com.kaizehuang.vernier.core.native.VernierNativeLibrary

internal fun interface WidevineNativePropertyReader {
    fun readProperties(): WidevineNativeSnapshot
}

internal class WidevineNativeBridge : WidevineNativePropertyReader {

    override fun readProperties(): WidevineNativeSnapshot {
        // The Java MediaDrm path stays available when this optional native path cannot load, so an
        // unavailable library is not an error here. Asking also triggers the one-time load attempt.
        if (!VernierNativeLibrary.isLoaded) {
            return WidevineNativeSnapshot()
        }
        return try {
            parse(nativeReadProperties())
        } catch (_: LinkageError) {
            WidevineNativeSnapshot()
        } catch (_: Exception) {
            WidevineNativeSnapshot()
        }
    }

    internal fun parse(raw: Array<String?>): WidevineNativeSnapshot {
        if (raw.size < PAYLOAD_FIELD_COUNT || raw[INDEX_AVAILABLE] != "1") {
            return WidevineNativeSnapshot()
        }

        val securityStatus = raw[INDEX_SECURITY_STATUS].toStatusCode()
        val systemIdStatus = raw[INDEX_SYSTEM_ID_STATUS].toStatusCode()
        if (securityStatus == MEDIA_ERROR_INVALID_OBJECT &&
            systemIdStatus == MEDIA_ERROR_INVALID_OBJECT
        ) {
            return WidevineNativeSnapshot(
                securityLevelStatusCode = securityStatus,
                systemIdStatusCode = systemIdStatus,
            )
        }
        return WidevineNativeSnapshot(
            available = true,
            securityLevel = propertyRead(securityStatus, raw[INDEX_SECURITY_VALUE]),
            systemId = propertyRead(systemIdStatus, raw[INDEX_SYSTEM_ID_VALUE]),
            securityLevelStatusCode = securityStatus,
            systemIdStatusCode = systemIdStatus,
        )
    }

    private fun propertyRead(statusCode: Int?, value: String?): WidevinePropertyRead {
        return when {
            statusCode == MEDIA_STATUS_OK && value?.isValidWidevinePropertyValue() == true ->
                WidevinePropertyRead(
                    status = WidevinePropertyStatus.AVAILABLE,
                    value = value,
                )

            statusCode == MEDIA_ERROR_UNSUPPORTED ||
                statusCode == MEDIA_ERROR_INVALID_PARAMETER ->
                WidevinePropertyRead(WidevinePropertyStatus.UNSUPPORTED)

            else -> WidevinePropertyRead(WidevinePropertyStatus.ERROR)
        }
    }

    private fun String?.toStatusCode(): Int? = this?.toIntOrNull()

    private external fun nativeReadProperties(): Array<String?>

    private companion object {
        const val MEDIA_STATUS_OK = 0
        const val MEDIA_ERROR_UNSUPPORTED = -10002
        const val MEDIA_ERROR_INVALID_OBJECT = -10003
        // NdkMediaDrm maps DRM_CANNOT_HANDLE (including unknown vendor properties) to this code.
        const val MEDIA_ERROR_INVALID_PARAMETER = -10004
        const val PAYLOAD_FIELD_COUNT = 5
        const val INDEX_AVAILABLE = 0
        const val INDEX_SECURITY_STATUS = 1
        const val INDEX_SECURITY_VALUE = 2
        const val INDEX_SYSTEM_ID_STATUS = 3
        const val INDEX_SYSTEM_ID_VALUE = 4
    }
}
