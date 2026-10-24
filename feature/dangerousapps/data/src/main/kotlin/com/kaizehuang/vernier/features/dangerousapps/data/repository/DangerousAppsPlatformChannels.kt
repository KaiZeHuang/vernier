package com.kaizehuang.vernier.features.dangerousapps.data.repository

import android.content.ComponentName
import android.content.Intent
import android.os.Parcel
import android.provider.Settings
import android.text.TextUtils
import com.kaizehuang.vernier.core.platform.HiddenServiceManager
import java.io.File
import kotlin.random.Random

internal fun DangerousAppsRepository.detectThanoxIpc(): Boolean {
    var data: Parcel? = null
    var reply: Parcel? = null
    return try {
        val dropboxBinder = HiddenServiceManager.getService(THANOX_PROXIED_SERVICE).getOrThrow()
            ?: return false

        data = Parcel.obtain()
        reply = Parcel.obtain()

        val result = dropboxBinder.transact(THANOX_IPC_TRANS_CODE, data, reply, 0)
        if (!result) {
            return false
        }
        reply.setDataPosition(0)
        reply.dataSize() > 0
    } catch (_: Exception) {
        false
    } finally {
        data?.recycle()
        reply?.recycle()
    }
}

internal fun DangerousAppsRepository.isAccessibilityServiceEnabled(packageName: String): Boolean {
    return try {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false

        val services = TextUtils.SimpleStringSplitter(':').apply {
            setString(enabledServices)
        }

        services.any { service -> service.startsWith("$packageName/") }
    } catch (_: Exception) {
        false
    }
}

internal fun DangerousAppsRepository.detectSceneBroadcast(): Boolean {
    val token = Random.nextLong().toULong().toString(16) +
        Random.nextLong().toULong().toString(16)
    val pocPath = "/sdcard/$token"
    val detected = try {
        val intent = Intent().apply {
            component = ComponentName(
                "com.omarea.vtools",
                "com.omarea.scene_mode.ReceiverShortcut",
            )
            putExtra("packageName", "x; touch $pocPath; id >> $pocPath; #")
        }
        context.sendBroadcast(intent)
        waitForScenePocFile(pocPath)
    } catch (_: Exception) {
        false
    } finally {
        runCatching { File(pocPath).delete() }
    }
    return detected
}

internal const val THANOX_PROXIED_SERVICE = "dropbox"

internal val THANOX_IPC_TRANS_CODE =
    "github.tornaco.android.thanos.core.IPC_TRANS_CODE_THANOS_SERVER".hashCode()
