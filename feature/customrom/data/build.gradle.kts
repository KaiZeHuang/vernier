plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.customrom.data"
}

dependencies {
    implementation(project(":capability:packageinventory:data"))
    api(project(":capability:packageinventory:domain"))
    api(project(":core:detector"))
    api(project(":core:native"))
    implementation(project(":core:platform"))
    api(project(":feature:customrom:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
