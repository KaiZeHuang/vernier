plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.mount.ui"
}

dependencies {
    implementation(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:ui"))
    implementation(project(":feature:mount:detector"))
    implementation(project(":feature:mount:presentation"))
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
}
