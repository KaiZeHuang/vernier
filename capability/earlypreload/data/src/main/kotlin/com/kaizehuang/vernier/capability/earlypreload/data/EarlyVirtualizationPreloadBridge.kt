package com.kaizehuang.vernier.capability.earlypreload.data

import com.kaizehuang.vernier.core.native.VernierNativeLibrary

public open class EarlyVirtualizationPreloadBridge {

    public open fun isNativeAvailable(): Boolean = isLoaded

    public open fun getStoredResult(): EarlyVirtualizationPreloadResult {
        if (!isLoaded) {
            return EarlyVirtualizationPreloadResult.empty()
        }

        return runCatching {
            if (!nativeHasEarlyDetectionRun()) {
                return EarlyVirtualizationPreloadResult.empty()
            }

            EarlyVirtualizationPreloadResult(
                hasRun = true,
                detected = nativeWasDetected(),
                detectionMethod = nativeGetDetectionMethod(),
                details = nativeGetDetails(),
                mountNamespaceInode = nativeGetMountNamespaceInode(),
                apexMountKey = nativeGetApexMountKey(),
                systemMountKey = nativeGetSystemMountKey(),
                vendorMountKey = nativeGetVendorMountKey(),
                qemuPropertyDetected = nativeWasQemuPropertyDetected(),
                emulatorHardwareDetected = nativeWasEmulatorHardwareDetected(),
                deviceNodeDetected = nativeWasDeviceNodeDetected(),
                avfRuntimeDetected = nativeWasAvfRuntimeDetected(),
                authfsRuntimeDetected = nativeWasAuthfsRuntimeDetected(),
                nativeBridgeDetected = nativeWasNativeBridgeDetected(),
                findings = nativeGetFindings().toList(),
                isContextValid = nativeIsPreloadContextValid(),
                source = EarlyVirtualizationPreloadSource.NATIVE,
            ).normalize()
        }.getOrElse {
            EarlyVirtualizationPreloadResult.empty()
        }
    }

    public open fun resetForTesting() {
        if (isLoaded) {
            runCatching { nativeReset() }
        }
    }

    private external fun nativeHasEarlyDetectionRun(): Boolean
    private external fun nativeIsPreloadContextValid(): Boolean
    private external fun nativeWasDetected(): Boolean
    private external fun nativeWasQemuPropertyDetected(): Boolean
    private external fun nativeWasEmulatorHardwareDetected(): Boolean
    private external fun nativeWasDeviceNodeDetected(): Boolean
    private external fun nativeWasAvfRuntimeDetected(): Boolean
    private external fun nativeWasAuthfsRuntimeDetected(): Boolean
    private external fun nativeWasNativeBridgeDetected(): Boolean
    private external fun nativeGetDetectionMethod(): String
    private external fun nativeGetDetails(): String
    private external fun nativeGetMountNamespaceInode(): String
    private external fun nativeGetApexMountKey(): String
    private external fun nativeGetSystemMountKey(): String
    private external fun nativeGetVendorMountKey(): String
    private external fun nativeGetFindings(): Array<String>
    private external fun nativeReset()

    public companion object {
        private val isLoaded: Boolean
            get() = VernierNativeLibrary.isLoaded
    }
}
