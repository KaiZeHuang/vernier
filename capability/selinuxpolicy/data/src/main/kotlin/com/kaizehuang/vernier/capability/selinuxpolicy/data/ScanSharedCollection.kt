package com.kaizehuang.vernier.capability.selinuxpolicy.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Gives every detector that reads the carrier during one scan the same collection.
 *
 * The SELinux and LSPosed scans both read the SELinux carrier, and the dashboard and the SDK start
 * them together. A collection that began at or after a caller's own scan start was gathered during
 * that scan, so the caller takes it rather than starting the carrier's app zygote again. One that
 * began earlier belongs to an older scan, and the caller collects afresh.
 */
internal class ScanSharedCollection<T : Any>(
    private val clock: () -> Long,
) {
    private val mutex = Mutex()
    private var latest: Collected<T>? = null

    suspend fun collect(scanStartedAt: Long, collect: suspend () -> T): T = mutex.withLock {
        latest?.takeIf { it.startedAt >= scanStartedAt }?.let { return@withLock it.value }
        val startedAt = clock()
        val value = collect()
        latest = Collected(startedAt, value)
        value
    }

    private class Collected<T>(val startedAt: Long, val value: T)
}
