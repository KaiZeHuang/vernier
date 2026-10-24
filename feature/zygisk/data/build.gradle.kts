plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.zygisk.data"
}

dependencies {
    api(project(":core:detector"))
    api(project(":core:native"))
    api(project(":feature:zygisk:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
