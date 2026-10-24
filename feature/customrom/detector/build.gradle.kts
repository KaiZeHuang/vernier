plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.customrom.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:packageinventory:domain"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:customrom:data"))
    api(project(":feature:customrom:domain"))
    api(project(":feature:customrom:presentation"))
}
