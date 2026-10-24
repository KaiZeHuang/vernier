package com.kaizehuang.vernier.features.heapresidue.data.ipc

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Debug
import android.os.IBinder
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.Process
import android.os.SystemClock
import com.kaizehuang.vernier.features.heapresidue.data.HeapResidueReleases
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.atomic.AtomicBoolean
import org.lsposed.hiddenapibypass.HiddenApiBypass

/** Only the dump gateway lives here; risk catalogs and the parser are loaded in the host VM. */
class IsolatedHeapDumpService : Service() {
    private val used = AtomicBoolean(false)
    private val endpoint = object : Binder() {
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code != HeapDumpProtocol.DUMP || HeapResidueReleases.current() == null) {
                return super.onTransact(code, data, reply, flags)
            }
            data.enforceInterface(HeapDumpProtocol.SERVICE)
            val output = ParcelFileDescriptor.CREATOR.createFromParcel(data)
            val callback = data.readStrongBinder()
            try {
                data.requireFullyConsumed()
                if (callback != null) {
                    val result = if (used.compareAndSet(false, true)) dump(output) else HeapDumpResult(HeapDumpStatus.REUSED_PROCESS)
                    send(callback, result)
                }
            } finally {
                output.close()
            }
            return true
        }
    }

    override fun onBind(intent: Intent?): IBinder = endpoint

    private fun dump(output: ParcelFileDescriptor): HeapDumpResult {
        val age = SystemClock.uptimeMillis() - Process.getStartUptimeMillis()
        val gcCount = Debug.getRuntimeStat("art.gc.gc-count")?.toLongOrNull() ?: -1
        val start = SystemClock.elapsedRealtime()
        val status = try {
            // FD overload is @hide. Invoke just this method; do not change VM-wide exemptions.
            HiddenApiBypass.invoke(Debug::class.java, null, "dumpHprofData", "heap-residue", output.fileDescriptor)
            HeapDumpStatus.COMPLETE
        } catch (_: InvocationTargetException) {
            HeapDumpStatus.DUMP_FAILED
        } catch (_: ReflectiveOperationException) {
            HeapDumpStatus.HIDDEN_API_UNAVAILABLE
        } catch (_: Exception) {
            HeapDumpStatus.DUMP_FAILED
        } catch (_: LinkageError) {
            HeapDumpStatus.HIDDEN_API_UNAVAILABLE
        }
        return HeapDumpResult(status, age, SystemClock.elapsedRealtime() - start, gcCount)
    }

    private fun send(callback: IBinder, result: HeapDumpResult) {
        val parcel = Parcel.obtain()
        try {
            parcel.writeInterfaceToken(HeapDumpProtocol.CALLBACK)
            parcel.writeInt(result.status.wire)
            parcel.writeLong(result.ageMillis)
            parcel.writeLong(result.durationMillis)
            parcel.writeLong(result.gcCountBefore)
            callback.transact(HeapDumpProtocol.RESULT, parcel, null, IBinder.FLAG_ONEWAY)
        } finally {
            parcel.recycle()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // A completed or cancelled binding must never leave a reusable, aged heap snapshot.
        Process.killProcess(Process.myPid())
    }
}
