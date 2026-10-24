package com.kaizehuang.vernier.core.platform

import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants

/** What stat(2) on a path established for this process. */
public enum class PathState {
    PRESENT,
    ABSENT,
    NOT_OBSERVABLE,
}

/**
 * stat(2) that keeps "absent" apart from "could not look".
 *
 * Only ENOENT and ENOTDIR show that a path does not exist. Any other error, most often EACCES from
 * DAC or SELinux on the path or a parent directory, means this process could not see it.
 */
public object PathStat {

    public fun of(path: String): PathState = try {
        Os.stat(path)
        PathState.PRESENT
    } catch (error: ErrnoException) {
        if (error.errno == OsConstants.ENOENT || error.errno == OsConstants.ENOTDIR) {
            PathState.ABSENT
        } else {
            PathState.NOT_OBSERVABLE
        }
    }
}
