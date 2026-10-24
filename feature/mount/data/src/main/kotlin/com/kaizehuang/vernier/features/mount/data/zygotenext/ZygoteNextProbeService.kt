package com.kaizehuang.vernier.features.mount.data.zygotenext

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Manifest component identity for the API 37 native service.
 *
 * Android 17 routes this component to zygote_next and loads libmain.so instead of creating this
 * Java class. The manager never binds it on older releases, where nativeService is unavailable.
 */
class ZygoteNextProbeService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
