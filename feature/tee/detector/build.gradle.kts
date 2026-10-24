plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.tee.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:attestation:data"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:tee:data"))
    api(project(":feature:tee:domain"))
    api(project(":feature:tee:presentation"))
}
