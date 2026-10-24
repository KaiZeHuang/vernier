plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.adbruntime.data"
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    implementation(project(":capability:systemproperties:data"))
    implementation(project(":capability:systemproperties:domain"))
    api(project(":feature:adbruntime:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
