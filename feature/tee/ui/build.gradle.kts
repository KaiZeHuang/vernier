plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.tee.ui"
}

dependencies {
    implementation(libs.uihelper)
    implementation(project(":capability:attestation:domain"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    implementation(project(":core:report"))
    api(project(":core:scan"))
    api(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:tee:detector"))
    api(project(":feature:tee:domain"))
    api(project(":feature:tee:presentation"))
    api(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.material.icons.extended)
    api(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    api(libs.kotlinx.coroutines.android)
}
