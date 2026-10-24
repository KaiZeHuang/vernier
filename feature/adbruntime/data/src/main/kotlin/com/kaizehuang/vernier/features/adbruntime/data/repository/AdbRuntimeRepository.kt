package com.kaizehuang.vernier.features.adbruntime.data.repository

import android.content.Context
import android.os.Build
import android.os.SystemClock
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.evidence.FailureName
import com.kaizehuang.vernier.features.adbruntime.domain.AdbProbeState
import com.kaizehuang.vernier.features.adbruntime.domain.AdbRuntimeReport
import com.kaizehuang.vernier.features.adbruntime.domain.AdbRuntimeSample
import com.kaizehuang.vernier.features.adbruntime.domain.AdbRuntimeStage
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/** Collects independent ADB state sources and leaves their correlation to the domain layer. */
class AdbRuntimeRepository(
    context: Context,
) : DetectorScanner<AdbRuntimeReport> {
    private val stateProbe = AdbStateProbe(context.applicationContext)
    private val mdnsProbe = AdbMdnsProbe(context.applicationContext)

    override suspend fun scan(): AdbRuntimeReport = withContext(Dispatchers.IO) {
        try {
            collect()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (failure: Exception) {
            AdbRuntimeReport.failed(FailureName.describe(failure))
        }
    }

    /*
     * The interval between the state samples debounces one-shot transitions; it is not an AOSP
     * settling deadline (EVIDENCE.md, "Sampling"). A network finding needs both discovery passes to
     * confirm one endpoint, so the second pass runs only after the first one confirmed it.
     */
    private suspend fun collect(): AdbRuntimeReport {
        val firstSampleAt = SystemClock.elapsedRealtime()
        val first = stateProbe.collect()
        val firstMdns = mdnsProbe.collect(confirmProtocol = first.initStopped())
        val elapsed = SystemClock.elapsedRealtime() - firstSampleAt
        if (elapsed < MIN_CONFIRMATION_INTERVAL_MS) {
            delay(MIN_CONFIRMATION_INTERVAL_MS - elapsed)
        }
        val second = stateProbe.collect()
        val mdnsSamples = if (firstMdns.protocol.confirmed) {
            listOf(firstMdns, mdnsProbe.collect(confirmProtocol = second.initStopped()))
        } else {
            listOf(firstMdns)
        }
        val samples = listOf(first, second)
        val probed = samples.any { sample -> sample.hasObservableSource() } ||
            mdnsSamples.any { snapshot ->
                snapshot.state == AdbProbeState.OBSERVED ||
                    snapshot.state == AdbProbeState.NOT_OBSERVED
            }

        return AdbRuntimeReport(
            stage = AdbRuntimeStage.READY,
            platformApiLevel = Build.VERSION.SDK_INT,
            samples = samples,
            mdnsSamples = mdnsSamples,
            probed = probed,
            unavailableReason = if (probed) null else {
                "Settings, USB state, properties and mDNS were all unavailable from this app process."
            },
        )
    }

    private fun AdbRuntimeSample.initStopped(): Boolean =
        properties.initAdbdState.equals("stopped", ignoreCase = true)

    private fun AdbRuntimeSample.hasObservableSource(): Boolean =
        properties.initAdbdState != null ||
            usb.state == AdbProbeState.OBSERVED ||
            usb.state == AdbProbeState.NOT_OBSERVED ||
            usbDebuggingEnabled != null ||
            wirelessDebuggingEnabled != null

    private companion object {
        const val MIN_CONFIRMATION_INTERVAL_MS = 1_500L
    }
}
