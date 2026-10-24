plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.deviceinfo.ui"
}

dependencies {
    implementation(project(":core:evidence"))
    implementation(project(":core:report"))
    api(project(":core:ui"))
    implementation(project(":core:designsystem"))
    api(project(":feature:deviceinfo:domain"))
    api(project(":feature:deviceinfo:presentation"))
    api(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.material.icons.extended)
    api(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    api(libs.kotlinx.coroutines.android)
}
