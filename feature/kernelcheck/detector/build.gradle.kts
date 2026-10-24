plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.kernelcheck.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:kernelcheck:data"))
    api(project(":feature:kernelcheck:domain"))
    api(project(":feature:kernelcheck:presentation"))
}
