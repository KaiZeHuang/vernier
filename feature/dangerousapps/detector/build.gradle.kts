plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.dangerousapps.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:packageinventory:data"))
    implementation(project(":capability:packageinventory:domain"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:dangerousapps:data"))
    api(project(":feature:dangerousapps:domain"))
    api(project(":feature:dangerousapps:presentation"))
}
