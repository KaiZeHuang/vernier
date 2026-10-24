plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.heapresidue.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:heapresidue:data"))
    api(project(":feature:heapresidue:domain"))
    api(project(":feature:heapresidue:presentation"))
}
