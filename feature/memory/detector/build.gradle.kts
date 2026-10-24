plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.memory.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:memory:data"))
    api(project(":feature:memory:domain"))
    api(project(":feature:memory:presentation"))
}
