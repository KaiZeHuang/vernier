plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.kernelcheck.data"
}

dependencies {
    api(project(":core:detector"))
    api(project(":core:native"))
    api(project(":feature:kernelcheck:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
