plugins {
    id("vernier.android.library")
    id("vernier.android.compose")
}

android {
    namespace = "com.kaizehuang.vernier.core.designsystem"
}

kotlin {
    explicitApi()
}

dependencies {
    api(libs.uihelper)
    implementation(libs.miuix.ui)
    api(libs.androidx.material3)
    implementation(libs.material.kolor)
    implementation(libs.material.color.utilities)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.graphics.shapes)
}
