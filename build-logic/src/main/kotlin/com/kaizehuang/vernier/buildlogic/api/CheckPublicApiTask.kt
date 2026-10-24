package com.kaizehuang.vernier.buildlogic.api

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor

/** Fails when the module's public API differs from the reference dump committed under `api/`. */
abstract class CheckPublicApiTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val actual: RegularFileProperty

    /** The committed dump; a collection so that a missing file reaches the task action. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val reference: ConfigurableFileCollection

    @get:Input
    abstract val updateTask: Property<String>

    /** `org.jetbrains.kotlin:abi-tools` with its runtime classpath. */
    @get:Classpath
    abstract val abiTools: ConfigurableFileCollection

    @get:Inject
    abstract val workers: WorkerExecutor

    @TaskAction
    fun check() {
        val referenceFile = reference.singleFile
        if (!referenceFile.isFile) {
            throw GradleException("$referenceFile is missing. Run ./gradlew ${updateTask.get()} and commit it.")
        }
        workers.classLoaderIsolation { classpath.from(abiTools) }.submit(CompareAbiDumps::class.java) {
            expected.set(referenceFile)
            actual.set(this@CheckPublicApiTask.actual)
            updateTask.set(this@CheckPublicApiTask.updateTask)
        }
    }
}
