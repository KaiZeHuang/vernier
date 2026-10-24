plugins {
    `kotlin-dsl`
}

group = "com.kaizehuang.vernier.buildlogic"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.json)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.dependency.analysis.gradle.plugin)
    implementation(libs.poko.gradle.plugin)
    compileOnly(libs.kotlin.abi.tools.api)
    testImplementation(libs.junit)
}

tasks.test {
    useJUnit()
}

gradlePlugin {
    plugins {
        register("vernierDetectorModuleBoundaries") {
            id = "vernier.module-boundaries"
            implementationClass = "com.kaizehuang.vernier.buildlogic.boundaries.VernierModuleBoundariesPlugin"
        }
        register("vernierDetectorAndroidApplication") {
            id = "vernier.android.application"
            implementationClass = "com.kaizehuang.vernier.buildlogic.VernierAndroidApplicationConventionPlugin"
        }
        register("vernierDetectorAndroidLibrary") {
            id = "vernier.android.library"
            implementationClass = "com.kaizehuang.vernier.buildlogic.VernierAndroidLibraryConventionPlugin"
        }
        register("vernierDetectorAndroidCompose") {
            id = "vernier.android.compose"
            implementationClass = "com.kaizehuang.vernier.buildlogic.VernierAndroidComposeConventionPlugin"
        }
        register("vernierDetectorJvmLibrary") {
            id = "vernier.jvm.library"
            implementationClass = "com.kaizehuang.vernier.buildlogic.VernierJvmLibraryConventionPlugin"
        }
        register("vernierDetectorAndroidApkArtifacts") {
            id = "vernier.android.apk-artifacts"
            implementationClass = "com.kaizehuang.vernier.buildlogic.VernierApkArtifactsConventionPlugin"
        }
        register("vernierDetectorDependencyAnalysis") {
            id = "vernier.dependency-analysis"
            implementationClass = "com.kaizehuang.vernier.buildlogic.VernierDependencyAnalysisPlugin"
        }
        register("vernierDetectorContractValues") {
            id = "vernier.contract-values"
            implementationClass = "com.kaizehuang.vernier.buildlogic.VernierContractValuesPlugin"
        }
        register("vernierDetectorPublicApi") {
            id = "vernier.public-api"
            implementationClass = "com.kaizehuang.vernier.buildlogic.api.VernierPublicApiPlugin"
        }
        register("vernierDetectorSdkDistribution") {
            id = "vernier.sdk.distribution"
            implementationClass = "com.kaizehuang.vernier.buildlogic.VernierSdkDistributionPlugin"
        }
    }
}
