package com.kaizehuang.vernier.buildlogic

import com.android.build.api.dsl.FusedLibraryExtension
import com.kaizehuang.vernier.buildlogic.boundaries.ModuleBoundaryPolicy
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

private const val SDK_RUNTIME = ":sdk:runtime"

/**
 * Fuses the headless SDK into one AAR: [SDK_RUNTIME] and every project module it is made of, with
 * external libraries left as dependencies in the POM. `publish` writes it to `build/repository`.
 */
class VernierSdkDistributionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.fused-library")
        pluginManager.apply("maven-publish")
        pluginManager.apply("vernier.module-boundaries")

        extensions.configure<FusedLibraryExtension> {
            minSdk {
                version = release(requiredIntGradleProperty("vernier.android.minSdk"))
            }
        }
        val policy = boundaryPolicy()
        headlessSdkModules(policy).forEach { module ->
            dependencies.add("include", dependencies.project(mapOf("path" to module)))
        }

        // The Fused Library plugin creates the publication of its fusedLibraryComponent itself.
        extensions.configure<PublishingExtension> {
            publications.withType(MavenPublication::class.java).configureEach {
                groupId = "com.kaizehuang.vernier"
                artifactId = "vernier-sdk"
                version = providers.gradleProperty("vernier.sdk.version").getOrElse("0.0.0-SNAPSHOT")
            }
            repositories {
                maven {
                    name = "build"
                    url = uri(layout.buildDirectory.dir("repository"))
                }
            }
        }

        val verify = tasks.register<VerifyFusedSdkTask>("verifySdkAar") {
            group = "verification"
            description = "Fails when the SDK AAR leaves a project module unfused or reaches a UI library."
            dependsOn("report")
            fusedReport.set(layout.buildDirectory.file("reports/fused_library_report/single/report.json"))
            uiOnlyDependencies.set(policy.uiOnlyDependencies)
            verified.set(layout.buildDirectory.file("verifySdkAar/verified.txt"))
        }
        tasks.named("assemble").configure { dependsOn(verify) }
    }
}

private fun Project.boundaryPolicy(): ModuleBoundaryPolicy =
    ModuleBoundaryPolicy.parse(rootProject.file(ModuleBoundaryPolicy.RELATIVE_PATH).readText())

/**
 * Every project module the headless SDK is made of: [SDK_RUNTIME] and each core, capability and
 * detector-unit module that is not UI. Supporting features such as the dashboard are left out, and
 * the boundary policy guarantees that no module that is not UI depends on one that is.
 */
internal fun Project.headlessSdkModules(policy: ModuleBoundaryPolicy): List<String> {
    val detectorUnits = detectorModules(DETECTOR_LAYER).map { it.substringBeforeLast(':') }.toSet()
    return rootProject.subprojects
        .filter { it.buildFile.exists() && it.path != path && it.path != policy.compositionRoot }
        .map { it.path }
        .filter { module -> policy.classify(module)?.ui == false }
        .filter { module -> !module.startsWith(":sdk:") || module == SDK_RUNTIME }
        .filter { module -> !module.startsWith(":feature:") || module.substringBeforeLast(':') in detectorUnits }
        .sorted()
}
