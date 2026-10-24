plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.dashboard.ui"
}

dependencies {
    implementation(libs.uihelper)
    implementation(project(":core:evidence"))
    implementation(project(":core:report"))
    api(project(":core:ui"))
    implementation(project(":core:designsystem"))
    api(project(":feature:dashboard:presentation"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material.icons.extended)
    api(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.activity)
}
