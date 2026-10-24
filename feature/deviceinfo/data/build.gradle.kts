plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.deviceinfo.data"
}

dependencies {
    api(project(":feature:deviceinfo:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
