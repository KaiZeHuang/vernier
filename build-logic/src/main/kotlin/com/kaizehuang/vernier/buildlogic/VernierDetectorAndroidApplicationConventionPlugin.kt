package com.kaizehuang.vernier.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.kaizehuang.vernier.buildlogic.assets.registerGeneratedAssets
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// versionCode is VERSION_CODE_BASE + the commit count, so it must never move backwards or an
// installed build stops being upgradeable. The repository history was collapsed into a single
// initial commit, which reset the commit count to 1; the last build made from the previous history
// was 300 + 718 = 1018, so the base carries that value forward.
private const val VERSION_CODE_BASE = 1018
private const val VERSION_NAME_ZONE_ID = "Asia/Singapore"
private const val isAlphaVersion = true

class VernierAndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        pluginManager.apply("vernier.module-boundaries")
        pluginManager.apply("com.autonomousapps.dependency-analysis")
        registerUnitTestLifecycle("testDebugUnitTest")

        val buildHash = providers.environmentVariable("GITHUB_SHA")
            .map { it.take(12) }
            .orElse(
                providers.of(GitShortHashValueSource::class.java) {
                    parameters.repositoryRoot.set(rootDir.absolutePath)
                }
            )
            .orElse("unknown")
        val buildTimeUtc = providers.gradleProperty("vernier.buildTimeUtc")
            .orElse(providers.environmentVariable("BUILD_TIME_UTC"))
            .orElse(
                providers.of(GitCommitTimestampValueSource::class.java) {
                    parameters.repositoryRoot.set(rootDir.absolutePath)
                }
            )
            .orElse("unknown")
        val versionCode = providers.of(GitCommitCountValueSource::class.java) {
            parameters.repositoryRoot.set(rootDir.absolutePath)
        }.map { commitCount ->
            VERSION_CODE_BASE + commitCount
        }
        val versionNameDate = providers.of(CurrentDateVersionNameValueSource::class.java) {
            parameters.zoneId.set(VERSION_NAME_ZONE_ID)
        }
        val versionName = providers.provider {
            "${versionNameDate.get()}-${buildHash.get()}"
        }

        val releaseKeystorePath = providers.environmentVariable("ANDROID_KEYSTORE_PATH")
        val releaseStorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD")
        val releaseKeyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS")
        val releaseKeyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD")
        val hasReleaseSigning = providers.provider {
            listOf(
                releaseKeystorePath.orNull,
                releaseStorePassword.orNull,
                releaseKeyAlias.orNull,
                releaseKeyPassword.orNull,
            ).all { !it.isNullOrBlank() }
        }

        extensions.configure<ApplicationExtension> {
            configureAndroidCommon(this)
            // The composition root is the single lint entry point, so it must analyse every module it
            // depends on; otherwise code moved out of :app silently leaves lint coverage.
            lint.checkDependencies = true

            defaultConfig {
                targetSdk = requiredIntGradleProperty("vernier.android.targetSdk")
                this.versionCode = versionCode.get()
                this.versionName = versionName.get()
                buildConfigField("String", "BUILD_TIME_UTC", "\"${buildTimeUtc.get()}\"")
                buildConfigField("String", "BUILD_HASH", "\"${buildHash.get()}\"")
                buildConfigField("boolean", "isAlphaVersion", isAlphaVersion.toString())
            }

            signingConfigs {
                if (hasReleaseSigning.get()) {
                    create("ciRelease") {
                        storeFile = file(requireNotNull(releaseKeystorePath.orNull))
                        storePassword = releaseStorePassword.orNull
                        keyAlias = releaseKeyAlias.orNull
                        keyPassword = releaseKeyPassword.orNull
                    }
                }
            }

            buildTypes {
                release {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                    signingConfig = if (hasReleaseSigning.get()) {
                         signingConfigs.getByName("ciRelease")
                    } else {
                         signingConfigs.getByName("debug")
                    }
                }
            }

            buildFeatures {
                compose = true
                buildConfig = true
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }

        registerGeneratedAssets()
    }
}
