package com.kaizehuang.vernier.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class VernierAndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("vernier.module-boundaries")
            pluginManager.apply("com.autonomousapps.dependency-analysis")

            extensions.configure<LibraryExtension> {
                configureAndroidCommon(this)
                buildFeatures.buildConfig = false
                val consumerRules = file("consumer-rules.pro")
                if (consumerRules.exists()) {
                    defaultConfig.consumerProguardFiles(consumerRules)
                }
                // The fused SDK AAR publishes the sources of every module it contains.
                publishing {
                    singleVariant("release") {
                        withSourcesJar()
                    }
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                compileForSdkHosts(this, compilerOptions)
            }

            dependencies.add("testImplementation", libs.findBundle("test-unit").get())
            registerUnitTestLifecycle("testDebugUnitTest")
        }
    }
}
