plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.core.ui"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.uihelper)
    implementation(libs.miuix.ui)
    implementation(project(":core:designsystem"))
    api(project(":core:detector"))
    api(project(":core:evidence"))
    api(project(":core:report"))
    api(project(":core:scan"))
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    api(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.kotlinx.coroutines.android)
}
