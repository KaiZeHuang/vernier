package com.kaizehuang.vernier.features.systemproperties.domain

/**
 * One property area's modification time, or why it could not be read.
 *
 * The time only marks the area's last write fault through a shared mapping, read from the coarse
 * wall clock, and no modification is known to leave a particular order, so it is never scored
 * (EVIDENCE.md, "Property area modification times").
 */
sealed interface PropertyAreaMtime {
    /** The area's SELinux property context, which is also its file name. */
    val context: String

    data class Recorded(
        override val context: String,
        val epochSeconds: Long,
        val nanoseconds: Long,
    ) : PropertyAreaMtime

    /** lstat failed; [errno] is its symbolic name, such as EACCES or ENOENT. */
    data class StatFailed(
        override val context: String,
        val errno: String,
    ) : PropertyAreaMtime

    /** The path is not a regular file, so it is not an area init created. */
    data class NotRegularFile(
        override val context: String,
    ) : PropertyAreaMtime
}
