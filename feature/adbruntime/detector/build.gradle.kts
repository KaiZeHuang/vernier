plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.adbruntime.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:adbruntime:data"))
    api(project(":feature:adbruntime:domain"))
    api(project(":feature:adbruntime:presentation"))
}
