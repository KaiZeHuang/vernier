plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.heapresidue.ui"
}

dependencies {
    implementation(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:ui"))
    implementation(project(":feature:heapresidue:detector"))
    implementation(project(":feature:heapresidue:presentation"))
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui)
}
