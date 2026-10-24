package com.kaizehuang.vernier.features.lsposed.data.probes

import com.kaizehuang.vernier.core.platform.PlatformFailureName
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignal
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignalSeverity
import java.util.concurrent.TimeUnit

data class LSPosedLogcatProbeResult(
    val signals: List<LSPosedSignal>,
    val available: Boolean,
    val failureReason: String? = null,
) {
    val hitCount: Int
        get() = signals.size

    val dangerHitCount: Int
        get() = signals.count { it.severity == LSPosedSignalSeverity.DANGER }

    val warningHitCount: Int
        get() = signals.count { it.severity == LSPosedSignalSeverity.WARNING }
}

fun interface LSPosedLogcatCommandRunner {
    fun run(
        command: List<String>,
        timeoutMs: Long,
    ): LSPosedLogcatCommandOutput
}

data class LSPosedLogcatCommandOutput(
    val output: String = "",
    val timedOut: Boolean = false,
    val errorMessage: String? = null,
)

class LSPosedLogcatProbe(
    private val commandRunner: LSPosedLogcatCommandRunner = LSPosedLogcatCommandRunner(::runCommand),
) {

    fun run(): LSPosedLogcatProbeResult {
        val outputs = COMMANDS.associate { spec ->
            spec.id to commandRunner.run(spec.command, PROCESS_TIMEOUT_MS)
        }
        return evaluate(outputs)
    }

    internal fun evaluate(
        outputs: Map<String, LSPosedLogcatCommandOutput>,
    ): LSPosedLogcatProbeResult {
        val signals = mutableListOf<LSPosedSignal>()
        val emittedIds = linkedSetOf<String>()
        var available = false

        outputs.forEach { (id, output) ->
            if (output.timedOut) {
                return@forEach
            }

            val failureText = listOfNotNull(
                output.errorMessage,
                output.output.takeIf { it.isNotBlank() },
            ).joinToString(separator = "\n")
            if (failureText.isLogAccessDenied()) {
                return@forEach
            }

            if (output.errorMessage == null || output.output.isNotBlank()) {
                available = true
            }

            when {
                id == PROCESS_COMMAND_ID -> signals += parseProcessOutput(output.output, emittedIds)
                id.startsWith(TAG_COMMAND_PREFIX) -> signals += parseTagOutput(
                    id,
                    output.output,
                    emittedIds
                )

                else -> signals += parseOverview(output.output, emittedIds)
            }
        }

        return LSPosedLogcatProbeResult(
            signals = signals.distinctBy { signal -> signal.id to signal.detail },
            available = available,
            failureReason = if (available) null else "Recent log buffers are not readable from the current app context.",
        )
    }

    private fun String?.isLogAccessDenied(): Boolean {
        return this?.contains("Permission denied", ignoreCase = true) == true ||
                this?.contains("EACCES", ignoreCase = true) == true ||
                this?.contains("not allowed to read logs", ignoreCase = true) == true ||
                this?.contains("READ_LOGS", ignoreCase = true) == true
    }

    private data class LogcatCommandSpec(
        val id: String,
        val command: List<String>,
    )

    private companion object {
        private const val PROCESS_TIMEOUT_MS = 3500L
        private const val OVERVIEW_COMMAND_ID = "overview"
        private const val PROCESS_COMMAND_ID = "process"
        private val COMMANDS = buildList {
            add(
                LogcatCommandSpec(
                    id = OVERVIEW_COMMAND_ID,
                    command = listOf("logcat", "-d", "-v", "brief", "-t", "1000"),
                ),
            )
            LSPosedProbeSupport.logcatTags
                .filter { tag ->
                    tag == "LSPosed" || tag == "LSPosed-Bridge" || tag == "LSPosedService"
                }
                .forEach { tag ->
                    add(
                        LogcatCommandSpec(
                            id = "$TAG_COMMAND_PREFIX$tag",
                            command = listOf(
                                "logcat",
                                "-d",
                                "-v",
                                "brief",
                                "-s",
                                "$tag:*",
                                "-t",
                                "50"
                            ),
                        ),
                    )
                }
            add(
                LogcatCommandSpec(
                    id = PROCESS_COMMAND_ID,
                    command = listOf("logcat", "-d", "-v", "process", "-t", "500"),
                ),
            )
        }

        private fun runCommand(
            command: List<String>,
            timeoutMs: Long,
        ): LSPosedLogcatCommandOutput {
            var process: Process? = null
            val output = StringBuilder()
            return try {
                process = ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start()

                val readerThread = Thread {
                    runCatching {
                        process.inputStream.bufferedReader().useLines { lines ->
                            lines.forEach { line -> output.appendLine(line) }
                        }
                    }
                }
                readerThread.isDaemon = true
                readerThread.start()

                val completed = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
                if (!completed) {
                    process.destroyForcibly()
                    readerThread.join(200)
                    LSPosedLogcatCommandOutput(
                        output = output.toString().trim(),
                        timedOut = true,
                    )
                } else {
                    readerThread.join(200)
                    LSPosedLogcatCommandOutput(
                        output = output.toString().trim(),
                    )
                }
            } catch (throwable: Throwable) {
                LSPosedLogcatCommandOutput(
                    output = output.toString().trim(),
                    errorMessage = throwable.message ?: PlatformFailureName.of(throwable),
                )
            } finally {
                process?.destroy()
            }
        }
    }
}
