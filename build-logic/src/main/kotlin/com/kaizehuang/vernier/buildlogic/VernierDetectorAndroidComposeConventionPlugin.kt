package com.kaizehuang.vernier.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class VernierAndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            if (!pluginManager.hasPlugin("com.android.library")) {
                throw GradleException("$path must apply vernier.android.library before vernier.android.compose")
            }
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            extensions.configure<LibraryExtension> {
                buildFeatures.compose = true
            }
            dependencies.add("implementation", dependencies.platform(libs.findLibrary("androidx-compose-bom").get()))
        }
    }
}
