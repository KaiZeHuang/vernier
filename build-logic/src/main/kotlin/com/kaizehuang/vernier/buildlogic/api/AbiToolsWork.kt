package com.kaizehuang.vernier.buildlogic.api

import java.util.ServiceLoader
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.jetbrains.kotlin.abi.tools.AbiFilters
import org.jetbrains.kotlin.abi.tools.AbiTools
import org.jetbrains.kotlin.abi.tools.AbiToolsFactory

/**
 * Kotlin's ABI tools, found as the Kotlin Gradle plugin finds them: through their service entry, in
 * the isolated worker class loader that holds `org.jetbrains.kotlin:abi-tools`.
 */
private fun WorkAction<*>.abiTools(): AbiTools =
    ServiceLoader.load(AbiToolsFactory::class.java, javaClass.classLoader).single().get()

/** Writes the ABI dump of a module's class files. */
abstract class WriteAbiDump : WorkAction<WriteAbiDump.Parameters> {

    interface Parameters : WorkParameters {
        val classFiles: ConfigurableFileCollection
        val dump: RegularFileProperty
    }

    override fun execute() {
        parameters.dump.get().asFile.bufferedWriter().use { writer ->
            abiTools().printJvmDump(writer, parameters.classFiles.files, AbiFilters.EMPTY)
        }
    }
}

/** Fails with the ABI tools' diff when a module's dump differs from the committed one. */
abstract class CompareAbiDumps : WorkAction<CompareAbiDumps.Parameters> {

    interface Parameters : WorkParameters {
        val expected: RegularFileProperty
        val actual: RegularFileProperty
        val updateTask: Property<String>
    }

    override fun execute() {
        val expected = parameters.expected.get().asFile
        val actual = parameters.actual.get().asFile
        if (expected.readText() != actual.readText()) {
            throw GradleException(
                "The public API no longer matches $expected:\n${abiTools().filesDiff(expected, actual)}\n" +
                    "If the change is intended, run ./gradlew ${parameters.updateTask.get()} and commit the updated dump.",
            )
        }
    }
}
