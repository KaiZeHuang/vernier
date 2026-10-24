plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.rootmanagers.data"
}

dependencies {
    implementation(project(":capability:packageinventory:data"))
    implementation(project(":capability:packageinventory:domain"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    implementation(project(":core:platform"))
    api(project(":feature:rootmanagers:domain"))
    implementation(libs.hiddenapibypass)
    implementation(libs.kotlinx.coroutines.android)
}
