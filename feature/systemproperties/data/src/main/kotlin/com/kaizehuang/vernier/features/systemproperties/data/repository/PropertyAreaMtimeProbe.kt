package com.kaizehuang.vernier.features.systemproperties.data.repository

import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import com.kaizehuang.vernier.features.systemproperties.domain.PropertyAreaMtime

/**
 * Records the modification times of the debug, radio and system property areas. Like the native
 * area scan, it does not follow symlinks and only accepts regular files.
 */
internal class PropertyAreaMtimeProbe {

    fun read(): List<PropertyAreaMtime> = CONTEXTS.map(::read)

    private fun read(context: String): PropertyAreaMtime {
        val stat = try {
            Os.lstat("$PROPERTY_AREA_DIR/$context")
        } catch (error: ErrnoException) {
            return PropertyAreaMtime.StatFailed(
                context = context,
                errno = OsConstants.errnoName(error.errno) ?: error.errno.toString(),
            )
        }
        if (!OsConstants.S_ISREG(stat.st_mode)) {
            return PropertyAreaMtime.NotRegularFile(context)
        }
        return PropertyAreaMtime.Recorded(
            context = context,
            epochSeconds = stat.st_mtim.tv_sec,
            nanoseconds = stat.st_mtim.tv_nsec,
        )
    }

    private companion object {
        const val PROPERTY_AREA_DIR = "/dev/__properties__"
        val CONTEXTS = listOf(
            "u:object_r:debug_prop:s0",
            "u:object_r:radio_prop:s0",
            "u:object_r:system_prop:s0",
        )
    }
}
