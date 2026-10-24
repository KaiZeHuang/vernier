package com.kaizehuang.vernier.features.heapresidue.data.ipc

import androidx.annotation.RequiresApi
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import android.system.StructPollfd
import com.kaizehuang.vernier.features.heapresidue.data.HeapResidueReleases
import java.io.InputStream
import java.net.SocketTimeoutException

/** Nonblocking reads plus finite poll: coroutine cancellation never waits on an unbounded pipe read. */
@RequiresApi(HeapResidueReleases.FIRST_AUDITED_API)
internal class DeadlinePipeInput(
    private val pipe: ParcelFileDescriptor,
    private val deadlineMillis: Long,
    private val checkActive: () -> Unit,
) : InputStream() {
    private val single = ByteArray(1)
    private val poll = StructPollfd().apply {
        fd = pipe.fileDescriptor
        events = OsConstants.POLLIN.toShort()
    }

    private val polls = arrayOf(poll)

    init {
        Os.fcntlInt(pipe.fileDescriptor, OsConstants.F_SETFL,
            Os.fcntlInt(pipe.fileDescriptor, OsConstants.F_GETFL, 0) or OsConstants.O_NONBLOCK)
    }

    override fun read(): Int = if (read(single, 0, 1) < 0) -1 else single[0].toInt() and 255

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        require(offset >= 0 && length >= 0 && offset <= bytes.size - length)
        if (length == 0) return 0
        while (true) {
            checkActive()
            val remaining = deadlineMillis - SystemClock.elapsedRealtime()
            if (remaining <= 0) throw SocketTimeoutException("Heap capture deadline exceeded")
            try {
                if (Os.poll(polls, minOf(remaining, 250).toInt()) == 0) continue
                val count = Os.read(pipe.fileDescriptor, bytes, offset, length)
                if (count == 0) { pipe.checkError(); return -1 }
                return count
            } catch (failure: ErrnoException) {
                if (failure.errno != OsConstants.EAGAIN && failure.errno != OsConstants.EINTR) throw failure
            }
        }
    }
}
