plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kaizehuang.vernier.core.navigation"
    // MIUIX Nav 0.9.4's entry<T>() is JVM-21 inline code. Isolate that one source-level
    // requirement here; all regular Vernier modules, including :app and the detector SDK, stay 17.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    api(libs.uihelper)
    api(libs.androidx.ui)
    api(libs.kotlinx.serialization.core)
    api(libs.miuix.nav)
}
