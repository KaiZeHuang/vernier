plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.selinux.ui"
}

dependencies {
    implementation(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:ui"))
    implementation(project(":feature:selinux:detector"))
    implementation(project(":feature:selinux:presentation"))
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
}
