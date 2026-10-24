plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.virtualization.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:helperprocess:data"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:virtualization:data"))
    api(project(":feature:virtualization:domain"))
    api(project(":feature:virtualization:presentation"))
}
