package com.kaizehuang.vernier.features.heapresidue.data.repository

import androidx.annotation.RequiresApi
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.evidence.FailureName
import com.kaizehuang.vernier.features.heapresidue.data.HeapResidueReleases
import com.kaizehuang.vernier.features.heapresidue.data.hprof.ArtHprofScanner
import com.kaizehuang.vernier.features.heapresidue.data.hprof.HprofFormatException
import com.kaizehuang.vernier.features.heapresidue.data.ipc.DeadlinePipeInput
import com.kaizehuang.vernier.features.heapresidue.data.ipc.HeapCaptureSession
import com.kaizehuang.vernier.features.heapresidue.data.ipc.HeapDumpStatus
import com.kaizehuang.vernier.features.heapresidue.data.retention.HeapDumpRetention
import com.kaizehuang.vernier.features.heapresidue.domain.HeapResidueOutcome
import com.kaizehuang.vernier.features.heapresidue.domain.HeapResidueReport
import com.kaizehuang.vernier.features.heapresidue.domain.HeapResidueStage
import com.kaizehuang.vernier.features.heapresidue.domain.HeapResidueProbeFailure
import com.kaizehuang.vernier.features.heapresidue.domain.heapResidueOutcome
import com.kaizehuang.vernier.features.heapresidue.domain.HeapResidueRetention
import java.io.File
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class HeapResidueRepository(context: Context) : DetectorScanner<HeapResidueReport> {
    private val context = context.applicationContext

    override suspend fun scan(): HeapResidueReport = scanMutex.withLock {
        withContext(Dispatchers.IO) {
            val release = HeapResidueReleases.current()
            // Lint's API check sees only this comparison, not current(); both reject the same releases.
            if (release == null || Build.VERSION.SDK_INT < HeapResidueReleases.FIRST_AUDITED_API) {
                return@withContext HeapResidueReport(stage = HeapResidueStage.READY, outcome = HeapResidueOutcome.UNSUPPORTED)
            }
            val report = try {
                withTimeoutOrNull(30_000) { collect() } ?: HeapResidueReport.failed("Heap capture timed out").copy(probeFailure = HeapResidueProbeFailure.TIMEOUT)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (failure: HprofFormatException) {
                HeapResidueReport(stage = HeapResidueStage.READY, outcome = HeapResidueOutcome.INCONCLUSIVE,
                    errorMessage = FailureName.describe(failure))
            } catch (failure: Exception) {
                HeapResidueReport.failed(FailureName.of(failure))
            }
            report.copy(release = release)
        }
    }

    @RequiresApi(HeapResidueReleases.FIRST_AUDITED_API)
    private suspend fun collect(): HeapResidueReport {
        val session = HeapCaptureSession(context)
        val pipes = ParcelFileDescriptor.createReliablePipe()
        var retention: HeapDumpRetention? = null
        var complete = false
        var connected = false
        try {
            val enabled = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            retention = HeapDumpRetention(File(context.noBackupFilesDir, "heap-residue"), enabled)
            val service = session.connect()
            connected = true
            val deadline = SystemClock.elapsedRealtime() + 25_000
            session.start(service, pipes[1])
            val coroutine = currentCoroutineContext()
            val raw = DeadlinePipeInput(pipes[0], deadline) { coroutine.ensureActive() }
            val scan = ArtHprofScanner(HeapResidueTargets.packages, host = context.packageName).scan(retention.wrap(raw))
            val dump = session.result.await()
            complete = dump.status == HeapDumpStatus.COMPLETE
            val retained = retention.finish(complete)
            return HeapResidueReport(
                stage = HeapResidueStage.READY,
                outcome = heapResidueOutcome(complete, scan.candidates, scan.signals),
                probeFailure = when (dump.status) {
                    HeapDumpStatus.COMPLETE -> null
                    HeapDumpStatus.HIDDEN_API_UNAVAILABLE -> HeapResidueProbeFailure.HIDDEN_API
                    HeapDumpStatus.DUMP_FAILED, HeapDumpStatus.REUSED_PROCESS -> HeapResidueProbeFailure.DUMP
                },
                signals = if (complete) scan.signals else emptyList(),
                bytesRead = scan.bytesRead, candidateCount = scan.candidates,
                captureAgeMillis = dump.ageMillis, dumpDurationMillis = dump.durationMillis,
                gcCountBefore = dump.gcCountBefore.takeIf { it >= 0 }, retention = retained,
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (failure: Exception) {
            // Closing the reader also releases a producer blocked by pipe backpressure.
            runCatching { pipes[0].close() }
            val dump = if (connected) withTimeoutOrNull(500) { session.result.await() } else null
            val reason = when {
                !connected -> HeapResidueProbeFailure.BINDING
                dump?.status == HeapDumpStatus.HIDDEN_API_UNAVAILABLE -> HeapResidueProbeFailure.HIDDEN_API
                dump != null && dump.status != HeapDumpStatus.COMPLETE -> HeapResidueProbeFailure.DUMP
                failure is SocketTimeoutException -> HeapResidueProbeFailure.TIMEOUT
                failure is HprofFormatException -> HeapResidueProbeFailure.MALFORMED_HPROF
                else -> HeapResidueProbeFailure.STREAM
            }
            return HeapResidueReport(
                stage = HeapResidueStage.READY,
                outcome = if (reason == HeapResidueProbeFailure.MALFORMED_HPROF) HeapResidueOutcome.INCONCLUSIVE
                    else HeapResidueOutcome.UNAVAILABLE,
                probeFailure = reason, errorMessage = FailureName.of(failure),
                retention = retention?.finish(false) ?: HeapResidueRetention.FAILED,
            )
        } finally {
            retention?.finish(complete)
            pipes.forEach { runCatching { it.close() } }
            session.close()
        }
    }

    private companion object {
        // Serializes this expensive feature across SDK sessions and reserves one debug spool at a time.
        val scanMutex = Mutex()
    }
}
