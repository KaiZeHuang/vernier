plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.zygisk.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:zygisk:data"))
    api(project(":feature:zygisk:domain"))
    api(project(":feature:zygisk:presentation"))
}
