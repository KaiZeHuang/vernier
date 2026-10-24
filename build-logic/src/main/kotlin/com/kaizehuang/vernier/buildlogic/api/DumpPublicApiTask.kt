package com.kaizehuang.vernier.buildlogic.api

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor

/** Writes the ABI dump of the module's compiled classes with Kotlin's ABI tools. */
@CacheableTask
abstract class DumpPublicApiTask : DefaultTask() {

    /** Class directories of an Android variant, from AGP's scoped `CLASSES` artifact. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classDirectories: ListProperty<Directory>

    /** Class jars of an Android variant, from the same artifact. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classJars: ListProperty<RegularFile>

    /** The class output of a JVM module. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val jvmClasses: ConfigurableFileCollection

    /** `org.jetbrains.kotlin:abi-tools` with its runtime classpath. */
    @get:Classpath
    abstract val abiTools: ConfigurableFileCollection

    @get:OutputFile
    abstract val dump: RegularFileProperty

    @get:Inject
    abstract val workers: WorkerExecutor

    @TaskAction
    fun write() {
        val classFiles = ApiClassFiles.collect(
            directories = classDirectories.get().map { it.asFile } + jvmClasses.files.filter { it.isDirectory },
            jars = classJars.get().map { it.asFile },
            extractionDirectory = temporaryDir.resolve("jars"),
        )
        workers.classLoaderIsolation { classpath.from(abiTools) }.submit(WriteAbiDump::class.java) {
            this.classFiles.from(classFiles)
            dump.set(this@DumpPublicApiTask.dump)
        }
    }
}
