plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.bootloader.data"
}

dependencies {
    api(project(":capability:attestation:data"))
    implementation(project(":capability:attestation:domain"))
    api(project(":capability:systemproperties:data"))
    api(project(":capability:systemproperties:domain"))
    api(project(":core:detector"))
    implementation(project(":core:native"))
    api(project(":feature:bootloader:domain"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.annotation)
}
