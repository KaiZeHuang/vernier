plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.systemproperties.data"
}

dependencies {
    api(project(":capability:systemproperties:data"))
    api(project(":capability:systemproperties:domain"))
    api(project(":core:detector"))
    api(project(":feature:systemproperties:domain"))
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(project(":core:native"))
}
