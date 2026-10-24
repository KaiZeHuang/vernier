plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.dangerousapps.ui"
}

dependencies {
    implementation(libs.uihelper)
    implementation(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:dangerousapps:detector"))
    implementation(project(":feature:dangerousapps:presentation"))
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
}
