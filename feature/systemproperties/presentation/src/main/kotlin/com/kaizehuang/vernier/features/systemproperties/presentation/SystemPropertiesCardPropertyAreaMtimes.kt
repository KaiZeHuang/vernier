package com.kaizehuang.vernier.features.systemproperties.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.systemproperties.domain.PropertyAreaMtime
import com.kaizehuang.vernier.features.systemproperties.presentation.model.SystemPropertiesDetailRowModel
import java.time.DateTimeException
import java.time.Instant

internal const val PROPERTY_AREA_MTIMES_LABEL = "Prop area mtimes"

/** Informational whatever was read: the times are recorded for comparison, never scored. */
internal fun propertyAreaMtimeRow(mtimes: List<PropertyAreaMtime>): SystemPropertiesDetailRowModel {
    val recorded = mtimes.count { it is PropertyAreaMtime.Recorded }
    return SystemPropertiesDetailRowModel(
        label = PROPERTY_AREA_MTIMES_LABEL,
        value = when (recorded) {
            0 -> "Unavailable"
            mtimes.size -> "$recorded recorded"
            else -> "$recorded of ${mtimes.size} recorded"
        },
        status = DetectorStatus.info(InfoKind.SUPPORT),
        detail = (mtimes.map(::mtimeLine) + MTIME_NOTE).joinToString(separator = "\n"),
        detailMonospace = true,
    )
}

private fun mtimeLine(mtime: PropertyAreaMtime): String {
    val observed = when (mtime) {
        is PropertyAreaMtime.Recorded -> utcTimestamp(mtime.epochSeconds, mtime.nanoseconds)
        is PropertyAreaMtime.StatFailed -> "lstat failed: ${mtime.errno}"
        is PropertyAreaMtime.NotRegularFile -> "not a regular file"
    }
    return "${mtime.context}: $observed"
}

// Root can set any mtime with utimensat, including one Instant cannot hold.
private fun utcTimestamp(epochSeconds: Long, nanoseconds: Long): String {
    val raw = "$epochSeconds s + $nanoseconds ns from the epoch"
    return try {
        Instant.ofEpochSecond(epochSeconds, nanoseconds).toString()
    } catch (_: DateTimeException) {
        raw
    } catch (_: ArithmeticException) {
        raw
    }
}

private const val MTIME_NOTE =
    "Not scored. An area's mtime moves only when a process takes a write fault on its shared " +
        "mapping, and it comes from the coarse wall clock, which can still be unset early in boot."
