package com.kaizehuang.vernier.core.native

import com.kaizehuang.vernier.core.evidence.FailureName

/**
 * Load state of the shared native library, injectable so that collection failure paths can be
 * exercised on the JVM where no `.so` exists.
 */
public interface NativeLibraryHandle {

    public val isLoaded: Boolean

    /** Empty when [isLoaded] is true. */
    public val loadFailureDetail: String
}

/**
 * The single load site for `libvernier.so`.
 *
 * Every JNI bridge used to run its own `System.loadLibrary` in a companion initialiser, which made
 * the failure invisible: each bridge independently decided that an unloadable library looked the
 * same as a device with nothing to report. Loading here once keeps that decision in one place and
 * lets [NativeSnapshotCollector] name the failure.
 */
public object VernierNativeLibrary : NativeLibraryHandle {

    private const val LIBRARY_NAME = "vernier"

    private val loadResult: Result<Unit> = runCatching { System.loadLibrary(LIBRARY_NAME) }

    override val isLoaded: Boolean = loadResult.isSuccess

    override val loadFailureDetail: String = loadResult.exceptionOrNull()
        ?.let { cause -> "$LIBRARY_NAME could not be loaded: ${FailureName.messageOrName(cause)}" }
        .orEmpty()
}
