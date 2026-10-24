plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.mount.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:helperprocess:data"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:mount:data"))
    api(project(":feature:mount:domain"))
    api(project(":feature:mount:presentation"))
}
