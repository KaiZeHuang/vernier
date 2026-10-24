package com.kaizehuang.vernier.sdk

import android.app.ZygotePreload
import android.content.pm.ApplicationInfo
import android.util.Log
import com.kaizehuang.vernier.capability.selinuxpolicy.data.SelinuxContextValidityPreload

/**
 * The detection work that has to run in the app zygote, before any isolated process forks from it.
 *
 * The SDK's manifest names this class in `android:zygotePreloadName`, so a host gets it without
 * declaring anything; a host with its own [ZygotePreload] delegates to it. It runs every
 * detector's app zygote preload in catalog order and then captures the SELinux context validity
 * evidence that the SELinux and LSPosed detectors read from their app zygote carriers. Without it,
 * both carriers report their app zygote evidence as unavailable.
 */
public class VernierZygotePreload : ZygotePreload {
    private val selinuxContextValidity = SelinuxContextValidityPreload()

    override fun doPreload(appInfo: ApplicationInfo) {
        selinuxContextValidity.preload(appInfo, trace = ::trace) {
            DetectorCatalog.all.forEach { detector ->
                trace("detector: ${detector.id}")
                detector.appZygotePreload(appInfo)
            }
        }
        trace("preload finished")
    }

    // An app zygote killed with SIGKILL leaves no crash report, so the last step logged before it
    // died is the only record of what the preload was doing.
    private fun trace(step: String) {
        Log.i(TRACE_TAG, step)
    }

    private companion object {
        const val TRACE_TAG = "VernierZygotePreloadNative"
    }
}
