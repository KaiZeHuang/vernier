plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.mount.data"
}

dependencies {
    api(project(":capability:earlypreload:data"))
    api(project(":capability:helperprocess:data"))
    api(project(":core:detector"))
    api(project(":core:native"))
    api(project(":feature:mount:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
