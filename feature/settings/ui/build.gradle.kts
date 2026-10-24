plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.settings.ui"
}

dependencies {
    api(libs.uihelper)
    implementation(libs.miuix.ui)
    api(project(":core:ui"))
    implementation(project(":core:designsystem"))
    api(project(":feature:settings:presentation"))
    implementation(libs.androidx.material.icons.extended)
    api(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.compose.icons.simple)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.aboutlibraries.compose.m3) {
        exclude(group = "com.github.skydoves", module = "compose-stability-runtime")
    }
}
