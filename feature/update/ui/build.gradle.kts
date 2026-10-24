plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.features.update.ui"
}

dependencies {
    implementation(libs.uihelper)
    implementation(project(":core:designsystem"))
    api(project(":feature:update:domain"))
    api(project(":feature:update:presentation"))
    api(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.material.icons.extended)
    api(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    api(libs.kotlinx.coroutines.android)
}
