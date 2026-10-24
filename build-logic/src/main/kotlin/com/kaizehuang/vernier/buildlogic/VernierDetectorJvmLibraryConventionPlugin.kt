package com.kaizehuang.vernier.buildlogic

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/** Pure Kotlin module: no Android SDK on the classpath, so its logic is testable on a plain JVM. */
class VernierJvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("com.android.lint")
            pluginManager.apply("vernier.module-boundaries")
            pluginManager.apply("com.autonomousapps.dependency-analysis")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
                // The fused SDK AAR publishes the sources of every module it contains.
                withSourcesJar()
            }
            extensions.configure<KotlinJvmProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    moduleName.set(kotlinModuleName())
                }
                compileForSdkHosts(this, compilerOptions)
            }
            tasks.withType<Test>().configureEach {
                useJUnit()
            }

            dependencies.add("testImplementation", libs.findLibrary("junit").get())
            dependencies.add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
            registerUnitTestLifecycle("test")
        }
    }
}
