package com.kaizehuang.vernier.features.heapresidue.data.retention

import com.kaizehuang.vernier.features.heapresidue.domain.HeapResidueRetention
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/** A local debug-only tee. Storage failures disable the tee without stopping the evidence stream. */
internal class HeapDumpRetention(directory: File, enabled: Boolean) {
    private val maximum = 32L * 1024 * 1024
    private var file: File? = null
    private var output: OutputStream? = null
    private var size = 0L
    var status = HeapResidueRetention.DISABLED
        private set

    init {
        if (enabled) {
            status = HeapResidueRetention.FAILED
            try {
                check(directory.mkdirs() || directory.isDirectory)
                val files = directory.listFiles().orEmpty().filter { it.isFile }.sortedBy { it.lastModified() }.toMutableList()
                files.filter { it.extension == "part" }.forEach { check(it.delete()); files.remove(it) }
                // Reserve a full per-dump budget before writing, including stale crash leftovers.
                while (files.size >= 2 || files.sumOf { it.length() } > maximum) check(files.removeAt(0).delete())
                file = File.createTempFile("capture-", ".part", directory)
                output = file!!.outputStream().buffered(64 * 1024)
                status = HeapResidueRetention.SAVED
            } catch (_: Exception) { abort(HeapResidueRetention.FAILED) }
        }
    }

    fun wrap(source: InputStream): InputStream = object : InputStream() {
        private val single = ByteArray(1)
        override fun read(): Int = if (read(single, 0, 1) < 0) -1 else single[0].toInt() and 255
        override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
            val count = source.read(bytes, offset, length)
            if (count > 0 && output != null) {
                if (count > maximum - size) abort(HeapResidueRetention.TOO_LARGE)
                else try { output!!.write(bytes, offset, count); size += count } catch (_: Exception) { abort(HeapResidueRetention.FAILED) }
            }
            return count
        }
    }

    fun finish(complete: Boolean): HeapResidueRetention {
        if (output != null) {
            if (!complete) abort(HeapResidueRetention.FAILED)
            else try {
                output!!.close()
                output = null
                val current = file!!
                check(current.renameTo(File(current.parentFile, current.nameWithoutExtension + ".hprof")))
                file = null
            } catch (_: Exception) { abort(HeapResidueRetention.FAILED) }
        }
        return status
    }

    private fun abort(reason: HeapResidueRetention) {
        status = reason
        runCatching { output?.close() }
        output = null
        runCatching { file?.delete() }
        file = null
    }
}
