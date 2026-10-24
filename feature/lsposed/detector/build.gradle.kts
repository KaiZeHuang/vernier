plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.lsposed.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:lsposed:data"))
    api(project(":feature:lsposed:domain"))
    api(project(":feature:lsposed:presentation"))
}
