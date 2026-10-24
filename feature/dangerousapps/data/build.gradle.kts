plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.dangerousapps.data"
}

dependencies {
    api(project(":capability:packageinventory:data"))
    api(project(":capability:packageinventory:domain"))
    api(project(":core:detector"))
    implementation(project(":core:platform"))
    api(project(":feature:dangerousapps:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
