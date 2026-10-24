package com.kaizehuang.vernier.buildlogic

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

/**
 * Compiles a library for the oldest Kotlin that SDK hosts may use: the language and API version of
 * `vernier.kotlin.sdkStdlib`, with that standard library as its dependency, instead of the
 * Kotlin that builds the project. The app is not a library and keeps the newer defaults.
 */
internal fun Project.compileForSdkHosts(extension: KotlinBaseExtension, compilerOptions: KotlinJvmCompilerOptions) {
    val stdlib = requiredGradleProperty("vernier.kotlin.sdkStdlib")
    val version = KotlinVersion.fromVersion(stdlib.substringBeforeLast('.'))
    compilerOptions.languageVersion.set(version)
    compilerOptions.apiVersion.set(version)
    extension.coreLibrariesVersion = stdlib
}
