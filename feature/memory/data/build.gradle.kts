plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.memory.data"
}

dependencies {
    api(project(":core:detector"))
    api(project(":core:native"))
    api(project(":feature:memory:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
