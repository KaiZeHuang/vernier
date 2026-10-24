plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.rootmanagers.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:rootmanagers:data"))
    api(project(":feature:rootmanagers:domain"))
    api(project(":feature:rootmanagers:presentation"))
}
