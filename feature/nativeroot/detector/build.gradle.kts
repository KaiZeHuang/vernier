plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.nativeroot.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    api(libs.kotlinx.coroutines.core)
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:nativeroot:data"))
    api(project(":feature:nativeroot:domain"))
    api(project(":feature:nativeroot:presentation"))
}
