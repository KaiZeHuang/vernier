plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.heapresidue.data"
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":feature:heapresidue:domain"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hiddenapibypass)
    implementation(libs.androidx.annotation)
}
