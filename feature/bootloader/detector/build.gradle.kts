plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.bootloader.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:attestation:data"))
    implementation(project(":capability:systemproperties:data"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:bootloader:data"))
    api(project(":feature:bootloader:domain"))
    api(project(":feature:bootloader:presentation"))
}
