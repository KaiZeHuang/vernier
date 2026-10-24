plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.su.ui"
}

dependencies {
    implementation(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:ui"))
    implementation(project(":feature:su:detector"))
    implementation(project(":feature:su:presentation"))
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
}
