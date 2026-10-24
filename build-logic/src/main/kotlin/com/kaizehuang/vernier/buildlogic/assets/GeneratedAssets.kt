package com.kaizehuang.vernier.buildlogic.assets

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * Registers the tasks that keep the app's committed assets current: the GitHub contributors asset
 * before every build and the TEE revocation snapshot per variant. Each reaches the network only
 * when its refresh property or environment variable is set.
 */
internal fun Project.registerGeneratedAssets() {
    pluginManager.withPlugin("com.android.application") {
        val androidComponents = extensions.getByType<ApplicationAndroidComponentsExtension>()
        val generateGithubContributorsAsset = tasks.register(
            "generateGithubContributorsAsset",
            GenerateGithubContributorsAssetTask::class.java,
        ) {
            refreshEnabled.set(
                providers.gradleProperty("vernier.githubContributors.refresh")
                    .map(String::toBoolean)
                    .orElse(
                        providers.environmentVariable("VERNIERDETECTOR_GITHUB_CONTRIBUTORS_REFRESH")
                            .map(String::toBoolean)
                    )
                    .orElse(false)
            )
            endpointUrl.set(
                providers.gradleProperty("vernier.githubContributors.url")
                    .orElse(GITHUB_CONTRIBUTORS_API_URL)
            )
            authToken.set(
                providers.gradleProperty("vernier.githubContributors.token")
                    .orElse(providers.environmentVariable("GITHUB_TOKEN"))
                    .orElse(providers.environmentVariable("GH_TOKEN"))
            )
            maxAttempts.set(
                providers.gradleProperty("vernier.githubContributors.maxAttempts")
                    .map(String::toInt)
                    .orElse(4)
            )
            connectTimeoutMillis.set(
                providers.gradleProperty("vernier.githubContributors.connectTimeoutMillis")
                    .map(String::toInt)
                    .orElse(5_000)
            )
            readTimeoutMillis.set(
                providers.gradleProperty("vernier.githubContributors.readTimeoutMillis")
                    .map(String::toInt)
                    .orElse(10_000)
            )
            contributorsAssetFile.set(
                layout.projectDirectory.file("src/main/assets/$GITHUB_CONTRIBUTORS_ASSET_FILE_NAME")
            )
            avatarOutputDirectory.set(
                layout.projectDirectory.dir("src/main/assets/$GITHUB_CONTRIBUTORS_AVATAR_DIRECTORY")
            )
        }
        tasks.named("preBuild").configure {
            dependsOn(generateGithubContributorsAsset)
        }
        // The asset is written into src/main/assets, so every task that reads the asset sources
        // outside preBuild, such as dependency analysis, has to run after it too.
        tasks.named { it.startsWith("explodeAssetSource") }.configureEach {
            dependsOn(generateGithubContributorsAsset)
        }
        androidComponents.onVariants(androidComponents.selector().all()) { variant ->
            val taskName = variant.computeTaskName("generate", "TeeCrlAsset")
            val generateCrlAsset = tasks.register(
                taskName,
                GenerateTeeCrlAssetTask::class.java,
            ) {
                refreshEnabled.set(
                    providers.gradleProperty("vernier.teeCrl.refresh")
                        .map(String::toBoolean)
                        .orElse(
                            providers.environmentVariable("VERNIERDETECTOR_TEE_CRL_REFRESH")
                                .map(String::toBoolean)
                        )
                        .orElse(false)
                )
                endpointUrl.set(
                    providers.gradleProperty("vernier.teeCrl.url")
                        .orElse(TEE_CRL_STATUS_URL)
                )
                maxAttempts.set(
                    providers.gradleProperty("vernier.teeCrl.maxAttempts")
                        .map(String::toInt)
                        .orElse(5)
                )
                connectTimeoutMillis.set(
                    providers.gradleProperty("vernier.teeCrl.connectTimeoutMillis")
                        .map(String::toInt)
                        .orElse(5_000)
                )
                readTimeoutMillis.set(
                    providers.gradleProperty("vernier.teeCrl.readTimeoutMillis")
                        .map(String::toInt)
                        .orElse(5_000)
                )
                fallbackAsset.set(
                    layout.projectDirectory.file(
                        "src/main/assets/$TEE_CRL_FALLBACK_ASSET_FILE_NAME"
                    )
                )
                outputDirectory.set(
                    layout.buildDirectory.dir("generated/teeCrl/${variant.name}/assets")
                )
            }
            variant.sources.assets?.addGeneratedSourceDirectory(
                generateCrlAsset,
                GenerateTeeCrlAssetTask::outputDirectory,
            )
        }
    }
}
