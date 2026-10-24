plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.adbruntime.ui"
}

dependencies {
    implementation(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:ui"))
    implementation(project(":feature:adbruntime:detector"))
    implementation(project(":feature:adbruntime:presentation"))
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui)
}
