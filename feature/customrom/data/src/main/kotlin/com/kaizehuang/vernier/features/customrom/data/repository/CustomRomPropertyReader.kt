package com.kaizehuang.vernier.features.customrom.data.repository

import com.kaizehuang.vernier.core.platform.HiddenSystemProperties
import java.util.concurrent.TimeUnit

fun interface CustomRomPropertyReader {
    fun read(name: String): String?
}

internal class DefaultCustomRomPropertyReader : CustomRomPropertyReader {

    override fun read(name: String): String? {
        readViaReflection(name)?.let { value ->
            return value
        }
        return readViaGetprop(name)
    }

    private fun readViaReflection(name: String): String? {
        return HiddenSystemProperties.read(name).getOrNull()?.trim()
    }

    private fun readViaGetprop(name: String): String? {
        var process: Process? = null
        return try {
            process = ProcessBuilder("getprop", name)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().use { it.readText().trim() }
            if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                null
            } else {
                output.takeIf { it.isNotBlank() }
            }
        } catch (_: Exception) {
            null
        } finally {
            process?.destroy()
        }
    }

    private companion object {
        private const val PROCESS_TIMEOUT_SECONDS = 3L
    }
}
