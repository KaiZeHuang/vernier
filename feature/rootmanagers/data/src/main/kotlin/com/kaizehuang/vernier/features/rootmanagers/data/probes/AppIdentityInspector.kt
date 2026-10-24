package com.kaizehuang.vernier.features.rootmanagers.data.probes

import android.content.pm.ApplicationInfo
import com.kaizehuang.vernier.features.rootmanagers.domain.RootManagerCatalog

/**
 * Reads the manifest and file-system identity fields of one app for one scan.
 *
 * An app installed in several profiles shares one APK and one native library directory, so each
 * directory is stat-ed once per scan and the tallies count directories, not observations.
 */
internal class AppIdentityInspector(
    private val payloadProbe: NativePayloadProbe = NativePayloadProbe(
        RootManagerCatalog.signatures.flatMapTo(linkedSetOf()) { signature -> signature.nativePayloads },
    ),
) {

    private val payloadReads = mutableMapOf<String, PayloadRead>()

    val payloadDirectoriesRead: Int
        get() = payloadReads.values.count { read -> read is PayloadRead.Read }

    val payloadDirectoryFailures: Int
        get() = payloadReads.values.count { read -> read is PayloadRead.Failed }

    val zygotePreloadAvailable: Boolean
        get() = HiddenZygotePreloadField.available

    fun zygotePreloadName(info: ApplicationInfo?): String? = info?.let(HiddenZygotePreloadField::read)

    fun nativePayloads(info: ApplicationInfo?): Set<String> {
        val directory = info?.nativeLibraryDir ?: return emptySet()
        val read = payloadReads.getOrPut(directory) { payloadProbe.read(directory) }
        return (read as? PayloadRead.Read)?.present.orEmpty()
    }
}
