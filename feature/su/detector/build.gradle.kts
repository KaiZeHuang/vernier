plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.su.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:su:data"))
    api(project(":feature:su:domain"))
    api(project(":feature:su:presentation"))
}
