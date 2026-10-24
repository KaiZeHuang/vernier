plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.systemproperties.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:systemproperties:data"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:systemproperties:data"))
    api(project(":feature:systemproperties:domain"))
    api(project(":feature:systemproperties:presentation"))
}
